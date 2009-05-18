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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Menu;

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
        super(parent, null);
    }

    protected void createHeader ()
    {
        _header = new Composite(this, 0);
        _header.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        _header.setLayout(layout);

        ArrayList<Category> cats = getCategories();
        _category = cats.get(0);
        String selection = Jikan.config.getSelectedCategory();
        for (Category cat : cats) {
            if (cat.getName().equals(selection)) {
                _category = cat;
                break;
            }
        }

        _title = new Label(_header, 0);
        _title.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        _title.setText(_category.getName());
        _title.addMouseListener(new MouseAdapter() {
            public void mouseDown (MouseEvent e) {
                if (e.button == 1) {
                    selectNextCategory();
                }
            }
        });
        createMenu();

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

    protected void createMenu ()
    {
        Menu popup = new Menu(getShell(), SWT.POP_UP);
        ArrayList<Category> catlist = getCategories();
        _cats = catlist.toArray(new Category[catlist.size()]);
        Arrays.sort(_cats);
        for (int ii = 0; ii < _cats.length; ii++) {
            MenuItem item = new MenuItem(popup, SWT.PUSH);
            Category cat = _cats[ii];
            item.setText(cat.getName());
            final Category fcat = cat;
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected (SelectionEvent event) {
                    selectCategory(fcat);
                }
            });
        }
        _title.setMenu(popup);
        _header.setMenu(popup);
    }

    protected void displayInfo ()
    {
        new NewCategoryDialog(getShell(), this);
    }

    protected void selectNextCategory ()
    {
        int idx = 0;
        for (int ii = 0; ii < _cats.length; ii++) {
            if (_cats[ii].equals(_category)) {
                idx = ii;
                break;
            }
        }
        selectCategory(_cats[(idx+1)%_cats.length]);
    }

    protected void selectCategory (Category cat)
    {
        _category = cat;
        _title.setText(cat.getName());
        _header.layout();
        Jikan.config.setSelectedCategory(cat.getName());
        refresh();
    }

    /**
     * Returns an array list with the {@link Category#EVENTS} and any
     * other special categories filtered out.
     */
    protected static ArrayList<Category> getCategories ()
    {
        ArrayList<Category> cats = new ArrayList<Category>();
        CollectionUtil.addAll(cats, Jikan.store.getCategories().iterator());
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
    protected Category[] _cats;
}
