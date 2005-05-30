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

package com.samskivert.jikan.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import static com.samskivert.jikan.Jikan.log;

/**
 * Maintains an item store in a set of property files.
 */
public class PropFileItemStore extends ItemStore
{
    public PropFileItemStore (File propdir)
        throws IOException
    {
        // scan the directory for all .properties files and load them up
        String[] files = propdir.list();
        for (int ii = 0; ii < files.length; ii++) {
            if (files[ii].endsWith(".properties")) {
                loadCategory(new File(propdir, files[ii]));
            }
        }
    }

    @Override // documentation inherited
    public List<String> getCategories ()
    {
        return new ArrayList<String>(_cats.keySet());
    }

    @Override // documentation inherited
    public List<Item> getItems (String category)
    {
        List<Item> items = _cats.get(category);
        return items == null ? null : new ArrayList<Item>(items);
    }

    @Override // documentation inherited
    public void storeCategory (String category, List<Item> items)
    {
        // todo
    }

    protected void loadCategory (File propfile)
        throws IOException
    {
        Properties props = new Properties();
        // TODO: use UTF-8
        props.load(new BufferedInputStream(new FileInputStream(propfile)));

        String catname = props.getProperty("category");
        ArrayList<Item> items = new ArrayList<Item>();
        int icount = getIntProperty(props, "items");
        for (int ii = 0; ii < icount; ii++) {
            items.add(new Item(props, ii));
        }
        _cats.put(catname, items);
    }

    protected int getIntProperty (Properties props, String key)
    {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (Exception e) {
            log.log(Level.WARNING, "Unable to read '" + key + "'.", e);
            return 0;
        }
    }

    protected HashMap<String,ArrayList<Item>> _cats =
        new HashMap<String,ArrayList<Item>>();
}
