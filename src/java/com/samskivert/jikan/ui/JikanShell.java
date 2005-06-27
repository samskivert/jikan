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

package com.samskivert.jikan.ui;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.Event;
import com.samskivert.jikan.data.Item;
import com.samskivert.jikan.data.ItemStore;
import com.samskivert.jikan.data.JournalCategory;

/**
 * Displays the main user interface.
 */
public class JikanShell
    implements ItemStore.StoreListener
{
    public static interface Refreshable
    {
        /** Instructs this category display to refresh itself. */
        public void refresh ();
    }

    public JikanShell (Display display)
    {
        _display = display;
        _shell = new Shell(display, SWT.BORDER|SWT.SHELL_TRIM);
        _shell.setText("Jikan");
        _shell.setLayout(new GridLayout(1, true));

        Jikan.store.setStoreListener(this);

        CalendarWidget cal = new CalendarWidget(_shell);
        cal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        EventList elist = new EventList(_shell);
        elist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cal.setEvents(elist, Jikan.store.getItems(Category.EVENTS));
        _catmap.put(Category.EVENTS, elist);

        Iterator<Category> iter = Jikan.store.getCategories();
        while (iter.hasNext()) {
            Category category = iter.next();
            if (category.equals(Category.EVENTS)) {
                continue;
            }
            ItemList ilist = new ItemList(_shell, category);
            ilist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            _catmap.put(category, ilist);
        }

        _jlist = new JournalItemList(_shell);
        _jlist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // add a listener to our shell to note changes in size and location
        _shell.addControlListener(new ControlListener() {
            public void controlMoved (ControlEvent e) {
                Rectangle bounds = _shell.getBounds();
                Rectangle trim = _shell.computeTrim(0, 0, 0, 0);
                bounds.width -= trim.width;
                bounds.height -= trim.height;
                Jikan.config.saveWindowBounds(bounds);
            }
            public void controlResized (ControlEvent e) {
                controlMoved(e);
            }
        });

        // size, position and display the main window; yes all this trim
        // hackery looks weird but without it the goddamned window doesn't
        // stay the same size when we save and restore the bounds
        Rectangle bounds = Jikan.config.getWindowBounds();
        Rectangle tbounds =
            _shell.computeTrim(0, 0, bounds.width, bounds.height);
        bounds.width = tbounds.width;
        bounds.height = tbounds.height;
        _shell.setBounds(bounds);
	_shell.open();
    }

    public void run ()
    {
	while (!_shell.isDisposed()) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
	}
    }

    // documentation inherited from interface ItemStore.StoreListener
    public void categoryUpdated (Category category)
    {
        final Refreshable list = (category instanceof JournalCategory) ?
            _jlist : _catmap.get(category);
        if (list != null) {
            _display.asyncExec(new Runnable() {
                public void run () {
                    list.refresh();
                }
            });
        }
    }

    protected Display _display;
    protected Shell _shell;
    protected JournalItemList _jlist;

    protected HashMap<Category,Refreshable> _catmap =
        new HashMap<Category,Refreshable>();
}
