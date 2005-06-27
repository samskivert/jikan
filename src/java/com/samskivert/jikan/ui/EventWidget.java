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
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.samskivert.jikan.data.Event;

import static com.samskivert.jikan.Jikan.log;

/**
 * This is not actually a widget, but rather two widgets that work in
 * harmony.
 */
public class EventWidget
{
    public EventWidget (EventList parent, Event event)
    {
        _parent = parent;
        _event = event;

        Menu popup = createPopup();

        DateFormat fmt = event.isAllDay() ? _adfmt : _dfmt;
        _when = new EditableLabel(parent, fmt.format(event.getWhen())) {
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
                    _event.setWhen(when, allday);
                    if (!text.equals(ntext)) {
                        setText(ntext);
                    }
                    // force our parent to resort the events
                    _parent.refresh();
                } else {
                    // TODO: report an error
                    startEdit();
                }
            }
        };
        _when.setMenu(popup);
        _when.setLayoutData(
            new GridData(GridData.FILL, GridData.CENTER, false, true));

        _text = new EditableLabel(parent, event.getText()) {
            protected void textUpdated (String text) {
                _event.setText(text);
            }
        };
        _text.setMenu(popup);
        _text.setLayoutData(
            new GridData(GridData.FILL, GridData.CENTER, true, true));
    }

    public Event getEvent ()
    {
        return _event;
    }

    public void edit ()
    {
        _text.startEdit();
    }

    public void delete ()
    {
        _parent.deleteEvent(this);
    }

    public void dispose ()
    {
        _when.dispose();
        _text.dispose();
    }

    protected Menu createPopup ()
    {
        Menu popup = new Menu(_parent.getShell(), SWT.POP_UP);

        MenuItem edit = new MenuItem(popup, SWT.PUSH);
        edit.setText("&Edit");
        edit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent event) {
                edit();
            }
        });

        MenuItem delete = new MenuItem(popup, SWT.PUSH);
        delete.setText("&Delete");
        delete.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent event) {
                delete();
            }
        });
        return popup;
    }

    protected EventList _parent;
    protected Event _event;
    protected EditableLabel _when;
    protected EditableLabel _text;

    protected static DateFormat _adfmt =
        DateFormat.getDateInstance(DateFormat.SHORT);
    protected static DateFormat _dfmt =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
}
