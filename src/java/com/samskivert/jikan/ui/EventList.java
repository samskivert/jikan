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

package com.samskivert.jikan.ui;

import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

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
    public EventList (Composite parent, int maxEvents)
    {
        super(parent, 0);
        _maxEvents = maxEvents;

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
        Jikan.shell.categoryUpdated(_nevent.category);
    }

    /**
     * Called when one of our children wants to be deleted.
     */
    protected void deleteEvent (EventWidget widget)
    {
        Event event = widget.getEvent();
        if (event != null) {
            Jikan.store.deleteItem(event);
            Jikan.shell.categoryUpdated(event.category);
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

        int added = 0;
        for (Item item : Jikan.store.getItems(Category.EVENTS)) {
            Event event = (Event)item;
            EventWidget ew = new EventWidget(this, event);
            if (event == _nevent) {
                ew.edit();
                _nevent = null;
            }
            if (++added == _maxEvents) {
                break;
            }
        }

        // add a button for showing all events if we have more than we can display
        if ((_maxEvents > 0) && (Jikan.store.getItems(Category.EVENTS).size() > _maxEvents)) {
            new Label(this, 0);
            new Label(this, 0);
            Button showAll = new Button(this, SWT.FLAT);
            showAll.setFont(Jikan.config.getFont(Jikan.config.SMALL_ICON_FONT));
            showAll.setText(" ... ");
            showAll.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, false, false));
            showAll.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected (SelectionEvent e) {
                    displayFullList();
                }
            });
        }

        // relayout our parent
        layout();
        getParent().layout();

        // if we're displaying a full list, refresh that too
        if (_full != null) {
            _full.refresh();
        }
    }

    protected void displayFullList ()
    {
        Shell shell = new Shell(Jikan.shell.getShell(), SWT.BORDER|SWT.SHELL_TRIM);
        shell.setLayout(new GridLayout(1, true));
        _full = new EventList(shell, -1);
        _full.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
        shell.setSize(size.x, size.y);
        shell.open();
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent (org.eclipse.swt.widgets.Event event) {
                _full = null;
            }
        });
    }

    protected int _maxEvents;
    protected Event _nevent;
    protected EventList _full;
}
