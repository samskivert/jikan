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
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.samskivert.jikan.data.Item;

/**
 * Displays the main user interface.
 */
public class JikanShell extends Shell
{
    public JikanShell (Display display)
    {
        super(display, SWT.BORDER|SWT.SHELL_TRIM);
        setLayout(new FillLayout());

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item(new Date(), "Test item one"));
        items.add(new Item(new Date(), "Test item two"));
        items.add(new Item(new Date(), "Test item three"));
        items.add(new Item(new Date(), "Test item four"));
        items.add(new Item(new Date(), "Test item five"));

        Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
        group.setText("Bits!");
        group.setLayout(new FillLayout());
        ItemList list = new ItemList(group, "General", items);
    }
}
