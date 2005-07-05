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
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.JournalCategory;
import com.samskivert.util.CollectionUtil;

/**
 * Displays a single category's items and allows scrolling through
 * different categories.
 */
public class CategoryItemList extends ItemList
{
    public CategoryItemList (Composite parent)
    {
        // the application ensures there's always at least one category
        super(parent, getCategories().get(0));
    }

    protected void createHeader ()
    {
        _header = new Composite(this, 0);
        _header.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = new GridLayout(5, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        _header.setLayout(layout);

        Button back = new Button(_header, SWT.ARROW | SWT.LEFT);
        back.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                selectCategory(-1);
            }
        });

        _title = new Label(_header, 0);
        _title.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        _title.setText(_category.getName());

        Button forward = new Button(_header, SWT.ARROW | SWT.RIGHT);
        forward.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                selectCategory(1);
            }
        });

        Composite spacer = new Composite(_header, 0);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 1;
        spacer.setLayoutData(gd);

        Button info = new Button(_header, 0);
        info.setText(" ? ");
        info.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                displayInfo();
            }
        });
    }

    protected void selectCategory (int delta)
    {
        // we do this the hard way to ensure that we pick up newly created
        // categories
        ArrayList<Category> cats = getCategories();
        int idx = cats.indexOf(_category);
        int nidx = (idx + delta);
        if (nidx < 0) {
            nidx += cats.size();
        }
        selectCategory(cats.get(nidx % cats.size()));
    }

    protected void displayInfo ()
    {
        new NewCategoryDialog(getShell(), this);
    }

    protected void selectCategory (Category cat)
    {
        _category = cat;
        _title.setText(_category.getName());
        _header.layout();
        refresh();
    }

    /**
     * Returns an array list with the {@link Category#EVENTS} and any
     * other special categories filtered out.
     */
    protected static ArrayList<Category> getCategories ()
    {
        ArrayList<Category> cats = new ArrayList<Category>();
        CollectionUtil.addAll(cats, Jikan.store.getCategories());
        for (Iterator<Category> iter = cats.iterator(); iter.hasNext(); ) {
            Category cat = iter.next();
            if (cat.equals(Category.EVENTS) || cat instanceof JournalCategory) {
                iter.remove();
            }
        }
        return cats;
    }

    protected Composite _header;
    protected Label _title;
}
