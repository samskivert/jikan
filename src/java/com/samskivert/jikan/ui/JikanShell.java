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

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.Event;
import com.samskivert.jikan.data.Item;

/**
 * Displays the main user interface.
 */
public class JikanShell
{
    public JikanShell (Display display)
    {
        _display = display;
        _shell = new Shell(display, SWT.BORDER|SWT.SHELL_TRIM);
        _shell.setLayout(new GridLayout(1, true));

        CalendarWidget cal = new CalendarWidget(_shell);
        cal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        EventList elist = new EventList(
            _shell, Jikan.store.getItems(Category.EVENTS));
        elist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cal.setEvents(elist, Jikan.store.getItems(Category.EVENTS));

        Iterator<Category> iter = Jikan.store.getCategories();
        while (iter.hasNext()) {
            Category category = iter.next();
            if (category.equals(Category.EVENTS)) {
                continue;
            }
            ItemList ilist = new ItemList(
                _shell, category, Jikan.store.getItems(category));
            ilist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        _shell.setSize(400, 800);
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

    protected Display _display;
    protected Shell _shell;
}
