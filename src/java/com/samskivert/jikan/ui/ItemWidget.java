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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.samskivert.jikan.data.Item;

import static com.samskivert.jikan.Jikan.log;

/**
 * Displays a single item and allows it to be edited if it is clicked
 * upon.
 */
public class ItemWidget extends EditableLabel
{
    public ItemWidget (Composite parent, Item item)
    {
        super(parent, item == null ? START_TEXT : item.getText());
        _item = item;
        if (_item != null) {
            setMenu(createPopup());
        }
    }

    public Item getItem ()
    {
        return _item;
    }

    protected Menu createPopup ()
    {
        Menu popup = new Menu(getShell(), SWT.POP_UP);

        MenuItem edit = new MenuItem(popup, SWT.PUSH);
        edit.setText("&Edit");
        edit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent event) {
                editSelected();
            }
        });

        MenuItem delete = new MenuItem(popup, SWT.PUSH);
        delete.setText("&Delete");
        delete.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent event) {
                deleteSelected();
            }
        });
        return popup;
    }

    protected void textUpdated (String text)
    {
        if (START_TEXT.equals(text)) {
            return;
        }

        if (_item == null) {
            _item = ((ItemList)getParent()).createItem();
            setMenu(createPopup());
        }
        _item.setText(text);
    }

    protected void editSelected ()
    {
        startEdit();
    }

    protected void deleteSelected ()
    {
        ((ItemList)getParent()).deleteItem(this);
    }

    protected Item _item;

    protected static final String START_TEXT = "<new>";
}
