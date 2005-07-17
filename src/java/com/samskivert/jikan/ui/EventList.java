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

import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.Event;
import com.samskivert.jikan.data.Item;

import static com.samskivert.jikan.Jikan.log;

/**
 * Displays a list of events which can be extended or edited.
 */
public class EventList extends Composite
    implements JikanShell.Refreshable
{
    public EventList (Composite parent)
    {
        super(parent, 0);

        GridLayout gl = new GridLayout();
        gl.numColumns = 3;
        gl.horizontalSpacing = 5;
        gl.verticalSpacing = 0;
        setLayout(gl);

        refresh();
    }

    public void createEvent (Date when)
    {
        _nevent = new Event("<new>", true, when, 0);
        Jikan.store.addItem(_nevent);
        Jikan.shell.categoryUpdated(_nevent.getCategory());
    }

    /**
     * Called when one of our children wants to be deleted.
     */
    protected void deleteEvent (EventWidget widget)
    {
        Event event = widget.getEvent();
        if (event != null) {
            Jikan.store.deleteItem(event);
            Jikan.shell.categoryUpdated(event.getCategory());
        } else {
            log.warning("Requested to delete event widget with no event.");
        }
    }

    // documentation inherited from interface JikanShell.Refreshable
    public void refresh ()
    {
        // blow away our children
        Control[] children = getChildren();
        for (int ii = 0; ii < children.length; ii++) {
            children[ii].dispose();
        }

        Iterator<Item> eiter = Jikan.store.getItems(Category.EVENTS);
        while (eiter.hasNext()) {
            Event event = (Event)eiter.next();
            EventWidget ew = new EventWidget(this, event);
            if (event == _nevent) {
                ew.edit();
                _nevent = null;
            }
        }

        // relayout our parent
        layout();
        getParent().layout();
    }

    protected Event _nevent;
}
