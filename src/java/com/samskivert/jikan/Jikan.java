//
// Jikan - an application for managing your time
// Copyright (C) 2005-2009 Michael Bayne
// 
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or (at your
// option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

package com.samskivert.jikan;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;
import com.samskivert.util.Interval;
import com.samskivert.util.Logger;
import com.samskivert.util.RunQueue;
import com.samskivert.util.StringUtil;

import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.GCalSyncer;
import com.samskivert.jikan.data.ItemJournal;
import com.samskivert.jikan.data.ItemStore;
import com.samskivert.jikan.data.PropFileItemStore;
import com.samskivert.jikan.ui.GetCredsDialog;
import com.samskivert.jikan.ui.JikanShell;

/**
 * The main entry point for the Jikan application.
 */
public class Jikan
{
    /** Used by entities that wish to update themselves when time ticks over to a new day. */
    public static interface DateDisplay
    {
        public void dateChanged ();
    }

    /** We dispatch our log messages through this logger. */
    public static Logger log = Logger.getLogger("jikan");

    /** Provides access to all of our configuration. */
    public static JikanConfig config;

    /** Manages our user interface. */
    public static JikanShell shell;

    /** Coordinates the modification of items. */
    public static ItemJournal journal;

    /** Provides access to our items. */
    public static ItemStore store;

    /** A RunQueue that posts runnables to the SWT event processing thread. */
    public static RunQueue swtQueue;

    /**
     * Returns the directory in which we store our local data.
     */
    public static String localDataDir ()
    {
        String home = System.getProperty("user.home");
        if (!StringUtil.isBlank(home)) {
            home += File.separator;
        }
        return home + ".jikan";
    }

    /**
     * Registers an entity to be notified when the date changes.
     */
    public static void registerDateDisplay (DateDisplay display)
    {
        _displays.add(display);
    }

    public static void main (String[] args)
    {
	final Display display = new Display();
        config = new JikanConfig(display);

        // create our SWT RunQueue
        swtQueue = new RunQueue() {
            public void postRunnable (Runnable r) {
                display.asyncExec(r);
            }
            public boolean isDispatchThread () {
                return (Display.findDisplay(Thread.currentThread()) == display);
            }
            public boolean isRunning () {
                return true;
            }
        };

        log.info("Jikan running at " + new Date());

        File ldir = new File(localDataDir());
        if (!ldir.exists()) {
            if (!ldir.mkdir()) {
                log.warning("Unable to create '" + ldir + "'.");
            }
        }

        final GCalSyncer gsyncer;
        try {
            // create our journal, item store and syncers
            journal = new ItemJournal();
            store = new PropFileItemStore(journal, ldir);
            gsyncer = new GCalSyncer(journal);
            // now initialize our journal and process pending events
            journal.init(ldir);
        } catch (IOException ioe) {
            // TODO: report the error via the UI
            log.warning("Error initializing.", ioe);
            System.exit(255);
            return;
        }

        // make sure we have our default category
        if (!store.getCategories().iterator().hasNext()) {
            Category cat = new Category("General", "general");
            store.createCategory(cat);
        }

        // schedule our next date tick
        scheduleDateTick();

        // this handles the main user interface
        shell = new JikanShell(display);

        // display a logon dialog for the Gcal syncer
        new GetCredsDialog(shell.getShell(), "Logon to Google Calendar:") {
            protected String getDefaultUsername () {
                return _prefs.get("gcal.username", "");
            }
            protected void onLogon (String username, String password) {
                try {
                    _prefs.put("gcal.username", username);
                    gsyncer.init(username, password);
                } catch (IOException ioe) {
                    // TODO: report error via UI
                    log.warning("Failed to initialize GCal syncer", ioe);
                }
            }
        };

        // finally start everything up and runnin'
        shell.run();

        config.dispose();
	display.dispose();

        // shut down our various bits
        gsyncer.shutdown();
        store.shutdown();
    }

    protected static void scheduleDateTick ()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // schedule an interval to expire one second after midnight
        long dt = cal.getTime().getTime() - System.currentTimeMillis() + 1000;
        new Interval(swtQueue) {
            public void expired () {
                fireDateTick();
            }
        }.schedule(dt);
    }

    protected static void fireDateTick ()
    {
        for (DateDisplay dd : _displays) {
            dd.dateChanged();
        }
        scheduleDateTick();
    }

    protected static Preferences _prefs = Preferences.userNodeForPackage(Jikan.class);
    protected static List<DateDisplay> _displays = Lists.newArrayList();
}
