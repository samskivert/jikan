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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
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
{
    public JournalItemList (Composite parent)
    {
        super(parent, new JournalCategory(new Date()));
    }

    protected void createHeader ()
    {
        _header = new Composite(this, 0);
        RowLayout layout = new RowLayout();
        layout.marginLeft = layout.marginRight = 0;
        layout.marginTop = layout.marginBottom = 0;
        layout.type = SWT.HORIZONTAL;
        _header.setLayout(layout);
        _header.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button back = new Button(_header, SWT.ARROW | SWT.LEFT);
        back.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                adjustDate(-1);
            }
        });

        _title = new Label(_header, 0);
        _title.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        _title.setText(_category.getName());

        Button forward = new Button(_header, SWT.ARROW | SWT.RIGHT);
        forward.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                adjustDate(1);
            }
        });
    }

    protected void adjustDate (int adjust)
    {
        _when.add(Calendar.DATE, adjust);
        _category = new JournalCategory(_when.getTime());
        _title.setText(_category.getName());
        _header.layout();
        refresh();
    }

    protected Composite _header;
    protected Label _title;
    protected Calendar _when = Calendar.getInstance();
}
