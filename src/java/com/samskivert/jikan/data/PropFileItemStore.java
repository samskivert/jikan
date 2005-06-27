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

import com.samskivert.util.Interval;
import com.samskivert.util.Tuple;

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
                File propfile = new File(propdir, files[ii]);
                loadCategory(files[ii], propfile, new Category());
            }
        }

        // queue up an interval to check for modifications periodically
        Interval checker = new Interval() {
            public void expired () {
                checkModified();
            }
        };
        checker.schedule(5000L, true);
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
        // journal categories are loaded on demand
        if (items == null && category instanceof JournalCategory) {
            loadJournalCategory(category);
            items = _cats.get(category);
        }
        return (items == null) ? null : items.iterator();
    }

    @Override // documentation inherited
    public synchronized void createCategory (Category category)
    {
        if (_cats.containsKey(category)) {
            log.warning("Requested to create category that already exists " +
                        "[cat=" + category + "].");
            return;
        }
    }

    @Override // documentation inherited
    public synchronized void addItem (Item item)
    {
        ArrayList<Item> items = _cats.get(item.getCategory());
        if (items == null) {
            log.warning("Requested to add item to non-existent category " +
                        "[item=" + item + "].");
            return;
        }
        items.add(item);
        item.setStore(this);
        _modified.add(item.getCategory());
        queueFlush();
    }

    @Override // documentation inherited
    public synchronized void deleteItem (Item item)
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
    public synchronized void itemModified (Item item)
    {
        _modified.add(item.getCategory());
        queueFlush();
    }

    @Override // documentation inherited
    public void flushModified ()
    {
        for (Tuple tup : flattenCategories()) {
            Category category = (Category)tup.left;
            Properties props = (Properties)tup.right;
            CategoryInfo catinfo = _catinfo.get(category);
            log.info("Flushing " + category);
            try {
                BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(catinfo.source));
                props.store(bout, "");
                bout.close();
                catinfo.flushed();
            } catch (IOException ioe) {
                log.log(Level.WARNING, "Failed writing '" + catinfo.source +
                        "'.", ioe);
            }
        }            
    }

    protected synchronized ArrayList<Tuple> flattenCategories ()
    {
        ArrayList<Tuple> flats = new ArrayList<Tuple>();
        for (Iterator<Category> iter = _modified.iterator(); iter.hasNext(); ) {
            Category category = iter.next();
            Properties props = new Properties();
            props.setProperty("category", category.getName());
            props.setProperty("items", "" + _cats.get(category).size());
            int idx = 0;
            for (Item item : _cats.get(category)) {
                item.store(props, idx++);
            }
            iter.remove();
            flats.add(new Tuple(category, props));
        }
        return flats;
    }

    protected Category loadCategory (
        String fname, File propfile, Category category)
        throws IOException
    {
        Properties props = new Properties();
        // TODO: use UTF-8 and perhaps the XML format as well
        props.load(new BufferedInputStream(new FileInputStream(propfile)));

        String catname = props.getProperty("category", category.getName());
        if (catname == null) {
            log.warning("Property file missing category " +
                        "[file=" + propfile + "].");
            return null;
        }

        int sufidx = fname.indexOf(".properties");
        if (sufidx != -1) {
            fname = fname.substring(0, sufidx);
        }
        category.init(catname, fname);
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

        // TODO: merge loaded data with in-memory category if it has been
        // modified recently
        synchronized (this) {
            _cats.put(category, items);
            _catinfo.put(category, new CategoryInfo(category, fname, propfile));
        }

        return category;
    }

    protected void loadJournalCategory (Category category)
    {
        // make sure the year directory exists
        File propfile = new File(_propdir, category.getFile() + ".properties");
        File parent = propfile.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                log.warning("Failed to create journal year directory " +
                            "[dir=" + parent + "].");
            }
        }

        try {
            // create the properties file if it doesn't already exist
            // (this NOOPs if it already exists)
            propfile.createNewFile();
            loadCategory(category.getFile(), propfile, category);
        } catch (IOException ioe) {
            log.log(Level.WARNING, "Failed to load journal category " +
                    "[file=" + propfile + "].", ioe);
        }
    }

    protected void checkModified ()
    {
        // make a first pass detecting which are modified
        HashSet<CategoryInfo> modified = new HashSet<CategoryInfo>();
        for (CategoryInfo catinfo : _catinfo.values()) {
            if (catinfo.checkNewer()) {
                modified.add(catinfo);
            }
        }

        // then reload which will modify the catinfo table
        for (CategoryInfo catinfo : modified) {
            log.info("Reloading modified category " + catinfo.source);
            try {
                loadCategory(catinfo.sourceName, catinfo.source,
                             catinfo.category);
                categoryUpdated(catinfo.category);
            } catch (IOException ioe) {
                log.log(Level.WARNING, "Failed reloading category '" +
                        catinfo.source + "'.", ioe);
            }
        }
    }

    protected static class CategoryInfo
    {
        public Category category;
        public String sourceName;
        public File source;
        public long lastModified;

        public CategoryInfo (Category category, String sourceName, File source)
        {
            this.category = category;
            this.sourceName = sourceName;
            this.source = source;
            this.lastModified = source.lastModified();
        }

        public synchronized boolean checkNewer ()
        {
            long newLastMod = source.lastModified();
            if (newLastMod > lastModified) {
                lastModified = newLastMod;
                return true;
            }
            return false;
        }

        public synchronized void flushed ()
        {
            lastModified = source.lastModified();
        }
    }

    protected File _propdir;
    protected HashMap<Category,ArrayList<Item>> _cats =
        new HashMap<Category,ArrayList<Item>>();
    protected HashMap<Category,CategoryInfo> _catinfo =
        new HashMap<Category,CategoryInfo>();
    protected HashSet<Category> _modified = new HashSet<Category>();
}
