//
// Jikan - an application for managing your time
// Copyright (C) 2005 Michael Bayne
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;

import com.samskivert.util.StringUtil;

import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.ItemStore;
import com.samskivert.jikan.data.PropFileItemStore;
import com.samskivert.jikan.ui.JikanShell;

/**
 * The main entry point for the Jikan application.
 */
public class Jikan
{
    /** We dispatch our log messages through this logger. */
    public static Logger log = Logger.getLogger("jikan");

    /** Provides access to all of our configuration. */
    public static JikanConfig config;

    /** Provides access to our items. */
    public static ItemStore store;

    /**
     * Returns the directory in which we store our local data.
     */
    public static String localDataDir ()
    {
        String home = System.getProperty("user.home");
        if (!StringUtil.blank(home)) {
            home += File.separator;
        }
        return home + ".jikan";
    }

    public static void main (String[] args)
    {
	Display display = new Display();
        config = new JikanConfig(display);

        File ldir = new File(localDataDir());
        if (!ldir.exists()) {
            if (!ldir.mkdir()) {
                log.warning("Unable to create '" + ldir + "'.");
            }
        }

        try {
            // create the appropriate item stoer
            store = new PropFileItemStore(ldir);
        } catch (IOException ioe) {
            log.log(Level.WARNING, "Error creating item store.", ioe);
            // TODO: report the error
        }

        // make sure we have our default category
        if (!store.getCategories().hasNext()) {
            Category cat = new Category();
            cat.init("General", "general");
            store.createCategory(cat);
        }

        // this handles the main user interface
        JikanShell shell = new JikanShell(display);
        shell.run();

        config.dispose();
	display.dispose();

        // shut down our item store
        store.shutdown();
    }
}
