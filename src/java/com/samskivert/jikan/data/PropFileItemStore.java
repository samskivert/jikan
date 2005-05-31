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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
        _propdir = propdir;
        // scan the directory for all .properties files and load them up
        String[] files = propdir.list();
        for (int ii = 0; ii < files.length; ii++) {
            if (files[ii].endsWith(".properties")) {
                loadCategory(files[ii], new File(propdir, files[ii]));
            }
        }
    }

    @Override // documentation inherited
    public Iterator<Category> getCategories ()
    {
        return _cats.keySet().iterator();
    }

    @Override // documentation inherited
    public Iterator<Item> getItems (Category category)
    {
        ArrayList<Item> items = _cats.get(category);
        return (items == null) ? null : items.iterator();
    }

    @Override // documentation inherited
    public void addItem (Item item)
    {
        ArrayList<Item> items = _cats.get(item.getCategory());
        if (items == null) {
            _cats.put(item.getCategory(), items = new ArrayList<Item>());
        }
        items.add(item);
        item.setStore(this);
        _modified.add(item.getCategory());
        queueFlush();
    }

    @Override // documentation inherited
    public void deleteItem (Item item)
    {
        ArrayList<Item> items = _cats.get(item.getCategory());
        if (items == null) {
            log.warning("Requested to delete item in non-existent category " +
                        "[item=" + item + "].");
            return;
        }
        if (items.remove(item)) {
            _modified.add(item.getCategory());
            queueFlush();
        } else {
            log.warning("Requested to delete unknown item " +
                        "[item=" + item + "].");
        }
    }

    @Override // documentation inherited
    public void itemModified (Item item)
    {
        _modified.add(item.getCategory());
        queueFlush();
    }

    @Override // documentation inherited
    public void flushModified ()
    {
        for (Iterator<Category> iter = _modified.iterator(); iter.hasNext(); ) {
            Category category = iter.next();
            log.info("Flushing " + category);
            Properties props = new Properties();
            props.setProperty("category", category.getName());
            props.setProperty("items", "" + _cats.get(category).size());
            int idx = 0;
            for (Item item : _cats.get(category)) {
                item.store(props, idx++);
            }
            File file = new File(_propdir, category.getFile() + ".properties");
            try {
                BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(file));
                props.store(bout, "");
                bout.close();
                iter.remove();
            } catch (IOException ioe) {
                log.log(Level.WARNING, "Failed writing '" + file + "'.", ioe);
            }
        }
    }

    protected void loadCategory (String fname, File propfile)
        throws IOException
    {
        Properties props = new Properties();
        // TODO: use UTF-8 and perhaps the XML format as well
        props.load(new BufferedInputStream(new FileInputStream(propfile)));

        String catname = props.getProperty("category");
        if (catname == null) {
            log.warning("Property file missing category " +
                        "[file=" + propfile + "].");
            return;
        }

        fname = fname.substring(0, fname.indexOf(".properties"));
        Category category = new Category(catname, fname);
        ArrayList<Item> items = new ArrayList<Item>();
        int icount = PropUtil.getIntProperty(props, "items");
        for (int ii = 0; ii < icount; ii++) {
            Item item;
            if (category.equals(Category.EVENTS)) {
                items.add(item = new Event(props, ii));
            } else {
                items.add(item = new Item(category, props, ii));
            }
            item.setStore(this);
        }
        _cats.put(category, items);
    }

    protected File _propdir;
    protected HashMap<Category,ArrayList<Item>> _cats =
        new HashMap<Category,ArrayList<Item>>();
    protected HashSet<Category> _modified = new HashSet<Category>();
}
