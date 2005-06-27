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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.jikan.data.Item;

import static com.samskivert.jikan.Jikan.log;

/**
 * Displays a list of items and provides the ability to edit those items
 * inline and add new items to the list.
 */
public class ItemList extends Composite
    implements JikanShell.Refreshable
{
    public ItemList (Composite parent, Category category)
    {
        super(parent, 0);
        _category = category;

        GridLayout gl = new GridLayout();
        gl.verticalSpacing = 0;
        gl.numColumns = 1;
        setLayout(gl);

        createHeader();
        refresh();
    }

    // documentation inherited from interface JikanShell.Refreshable
    public void refresh ()
    {
        // blow away everything but the title label
        Control[] children = getChildren();
        for (int ii = 1; ii < children.length; ii++) {
            children[ii].dispose();
        }

        // now add new widgets for our items
        Iterator<Item> items = Jikan.store.getItems(_category);
        while (items.hasNext()) {
            ItemWidget iw = new ItemWidget(this, items.next());
            iw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        // and add a blank one at the bottom
        addBlankItem();

        // relayout our parent
        layout();
        getParent().layout();
    }

    protected void createHeader ()
    {
        // add the title
        Label label = new Label(this, 0);
        label.setFont(Jikan.config.getFont(Jikan.config.CATEGORY_FONT));
        label.setText(_category.getName());
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Called when the blank item we added to the end of our list is
     * filled in.
     */
    protected Item createItem ()
    {
        Item item = new Item(_category, "");
        Jikan.store.addItem(item);
        addBlankItem();
        return item;
    }

    /**
     * Called when one of our children wants to be deleted.
     */
    protected void deleteItem (ItemWidget widget)
    {
        Item item = widget.getItem();
        if (item != null) {
            Jikan.store.deleteItem(item);
        } else {
            log.warning("Requested to delete item widget with no item.");
        }
        widget.dispose();
        getParent().layout();
    }

    protected void addBlankItem ()
    {
        ItemWidget iw = new ItemWidget(this, null);
        iw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        getParent().layout();
    }

    protected Category _category;
}
