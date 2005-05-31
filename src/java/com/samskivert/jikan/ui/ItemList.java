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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.Item;

/**
 * Displays a list of items and provides the ability to edit those items
 * inline and add new items to the list.
 */
public class ItemList extends Composite
{
    public ItemList (Composite parent, Category category, Iterator<Item> items)
    {
        super(parent, 0);
        _category = category;

        GridLayout gl = new GridLayout();
        gl.verticalSpacing = 0;
        gl.numColumns = 1;
        setLayout(gl);

        // add the title
        Label label = new Label(this, 0);
        label.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        label.setText(category.getName());
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // add the items
        while (items.hasNext()) {
            ItemWidget iw = new ItemWidget(this, items.next());
            iw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
    }

    protected Category _category;
}
