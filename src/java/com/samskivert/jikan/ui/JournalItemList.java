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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.JournalCategory;

/**
 * Displays an item list for our journal along with date navigation
 * buttons.
 */
public class JournalItemList extends ItemList
    implements Jikan.DateDisplay
{
    public JournalItemList (Composite parent)
    {
        super(parent, new JournalCategory(new Date()));

        Jikan.registerDateDisplay(this);
    }

    // documentation inherited from interface Jikan.DateDisplay
    public void dateChanged ()
    {
        // switch to the current day's journal at midnight
        selectDay(new Date());
    }

    protected void createHeader ()
    {
        _header = new Composite(this, 0);
        _header.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = new GridLayout(6, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        _header.setLayout(layout);

        Label jlabel = new Label(_header, 0);
        jlabel.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        jlabel.setText("Journal:");

        Button back = new Button(_header, SWT.ARROW | SWT.LEFT);
        back.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                adjustDate(-1);
            }
        });

        Button forward = new Button(_header, SWT.ARROW | SWT.RIGHT);
        forward.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                adjustDate(1);
            }
        });

        _title = new Label(_header, 0);
        _title.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        _title.setText(_category.getName());

        Composite spacer = new Composite(_header, 0);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 1;
        spacer.setLayoutData(gd);

        Button today = new Button(_header, SWT.ARROW | SWT.DOWN);
        today.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                selectDay(new Date());
            }
        });
    }

    protected void adjustDate (int adjust)
    {
        _when.add(Calendar.DATE, adjust);
        selectDay(_when.getTime());
    }

    protected void selectDay (Date when)
    {
        _when.setTime(when);
        _category = new JournalCategory(when);
        _title.setText(_category.getName());
        _header.layout();
        refresh();
    }

    protected Composite _header;
    protected Label _title;
    protected Calendar _when = Calendar.getInstance();
}
