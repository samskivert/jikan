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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.layout.GridData;
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
        Event event = new Event("<new>", false, when, 0);
        Jikan.store.addItem(event);
        addEvent(event);
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
            addEvent(event);
        }

//         // and add a blank one at the bottom
//         addBlankItem();

        // relayout our parent
        layout();
        getParent().layout();
    }

    protected void addEvent (final Event event)
    {
        EditableLabel el;
        final DateFormat fmt = event.isAllDay() ? _adfmt : _dfmt;
        el = new EditableLabel(this, fmt.format(event.getWhen())) {
            protected void textUpdated (String text) {
                Date when = null;
                boolean allday = false;
                String ntext = null;
                // first try parsing a date and time
                try {
                    when = _dfmt.parse(text);
                    ntext = _dfmt.format(when);
                } catch (ParseException pe) {
                    // pe.printStackTrace(System.err);
                }
                // if that failed, try just the date
                if (when == null) {
                    try {
                        when = _adfmt.parse(text);
                        ntext = _adfmt.format(when);
                        allday = true;
                    } catch (ParseException pe) {
                        System.err.println(pe.getMessage());
                    }
                }
                // if we got something, update the event
                if (when != null) {
                    event.setWhen(when, allday);
                    if (!text.equals(ntext)) {
                        setText(ntext);
                        getParent().layout();
                    }
                } else {
                    // TODO: report an error
                    startEdit();
                }
            }
        };
        el = new EditableLabel(this, event.getText()) {
            protected void textUpdated (String text) {
                event.setText(text);
            }
        };
        el.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    protected static DateFormat _adfmt =
        DateFormat.getDateInstance(DateFormat.SHORT);
    protected static DateFormat _dfmt =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
}
