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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.samskivert.util.CollectionUtil;

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
        gl.numColumns = 2;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        setLayout(gl);

        refresh();
    }

    public void createEvent (Date when)
    {
        _nevent = new Event("<new>", true, when, 0);
        Jikan.store.addItem(_nevent);
        refresh();
    }

    /**
     * Called when one of our children wants to be deleted.
     */
    protected void deleteEvent (EventWidget widget)
    {
        Event event = widget.getEvent();
        if (event != null) {
            Jikan.store.deleteItem(event);
        } else {
            log.warning("Requested to delete event widget with no event.");
        }
        widget.dispose();
        getParent().layout();
    }

    // documentation inherited from interface JikanShell.Refreshable
    public void refresh ()
    {
        // blow away our children
        Control[] children = getChildren();
        for (int ii = 0; ii < children.length; ii++) {
            children[ii].dispose();
        }

        Iterator<Item> events = Jikan.store.getItems(Category.EVENTS);
        ArrayList<Event> elist = new ArrayList<Event>();
        CollectionUtil.addAll(elist, events);
        Collections.sort(elist);

        for (Event event : elist) {
            EventWidget ew = new EventWidget(this, event);
            if (event == _nevent) {
                ew.edit();
                _nevent = null;
            }
        }

//         // and add a blank one at the bottom
//         addBlankItem();

        // relayout our parent
        layout();
        getParent().layout();
    }

    protected Event _nevent;
}
