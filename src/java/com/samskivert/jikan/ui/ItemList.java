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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.samskivert.jikan.Log;
import com.samskivert.jikan.data.Item;

/**
 * Displays a list of items and provides the ability to edit those items
 * inline and add new items to the list.
 */
public class ItemList extends Composite
{
    public ItemList (Composite parent, ArrayList<Item> items)
    {
        super(parent, 0);
        GridLayout gl = new GridLayout();
        gl.verticalSpacing = 0;
        gl.numColumns = 1;
        setLayout(gl);

        for (Item item : items) {
            GridData gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            ItemWidget iw = new ItemWidget(this, item);
            iw.setLayoutData(gd);
        }
    }
}
