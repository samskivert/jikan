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

package com.samskivert.jikan.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
                loadCategory(files[ii], propfile, new Category(null, null));
            }
        }

        // if we have no categories at all (we're just starting up), create a general category
        if (_cats.size() == 0) {
            Category general = new Category("General", "general");
            createCategory(general);
        }

        // make sure we have an events category
        if (!_cats.containsKey(Category.EVENTS)) {
            createCategory(Category.EVENTS);
        }

        // queue up an interval to check for modifications periodically
        Interval checker = new Interval() {
            public void expired () {
                checkModified();
            }
        };
        checker.schedule(30000L, true);
    }

    @Override // documentation inherited
    public Collection<Category> getCategories ()
    {
        return _cats.keySet();
    }

    @Override // documentation inherited
    public Collection<Item> getItems (Category category)
    {
        List<Item> items = _cats.get(category);
        // journal categories are loaded on demand
        if (items == null && category instanceof JournalCategory) {
            loadJournalCategory(category);
            items = _cats.get(category);
        }
        return items;
    }

    @Override // documentation inherited
    public synchronized void createCategory (Category category)
    {
        if (_cats.containsKey(category)) {
            log.warning("Requested to create category that already exists", "cat", category);
            return;
        }

        File catfile = new File(_propdir, category.file + ".properties");
        List<Item> items = Lists.newArrayList();
        synchronized (this) {
            _cats.put(category, items);
            _catinfo.put(category, new CategoryInfo(category, category.file, catfile));
        }

        categoryModified(category);
    }

    @Override // documentation inherited
    public synchronized void addItem (Item item)
    {
        List<Item> items = _cats.get(item.category);
        if (items == null) {
            log.warning("Requested to add item to non-existent category", "item", item);
            return;
        }
        items.add(item);
        item.setStore(this);
        categoryModified(item.category);
    }

    @Override // documentation inherited
    public synchronized void deleteItem (Item item)
    {
        List<Item> items = _cats.get(item.category);
        if (items == null) {
            log.warning("Requested to delete item in non-existent category", "item", item);
            return;
        }
        if (items.remove(item)) {
            _modified.add(item.category);
            queueFlush();
        } else {
            log.warning("Requested to delete unknown item", "item", item);
        }
    }

    @Override // documentation inherited
    public int getItemIndex (Item item)
    {
        List<Item> items = _cats.get(item.category);
        if (items == null) {
            log.warning("Requested index of item in non-existent category", "item", item);
            return -1;
        }
        return items.indexOf(item);
    }

    @Override // documentation inherited
    public void categoryModified (Category cat)
    {
        // the events category must be resorted when modified
        if (cat.equals(Category.EVENTS)) {
            Collections.sort(_cats.get(cat));
        }
        _modified.add(cat);
        queueFlush();
    }

    @Override // documentation inherited
    public void flushModified ()
    {
        for (Tuple<Category,Properties> tup : flattenCategories()) {
            Category category = tup.left;
            Properties props = tup.right;
            CategoryInfo catinfo = _catinfo.get(category);
            log.info("Flushing " + category);
            try {
                BufferedWriter bout = new BufferedWriter(new FileWriter(catinfo.source));
                props.store(bout, "");
                bout.close();
                catinfo.flushed();
            } catch (IOException ioe) {
                log.warning("Failed writing '" + catinfo.source + "'.", ioe);
            }
        }            
    }

    protected synchronized List<Tuple<Category,Properties>> flattenCategories ()
    {
        List<Tuple<Category,Properties>> flats = Lists.newArrayList();
        for (Category category : _modified) {
            Properties props = new Properties();
            props.setProperty("category", category.name);
            props.setProperty("items", "" + _cats.get(category).size());
            int idx = 0;
            for (Item item : _cats.get(category)) {
                item.store(props, idx++);
            }
            flats.add(new Tuple<Category,Properties>(category, props));
        }
        _modified.clear();
        return flats;
    }

    protected Category loadCategory (String fname, File propfile, Category category)
        throws IOException
    {
        Properties props = new Properties();
        // TODO: use UTF-8 and perhaps the XML format as well
        try {
            props.load(new BufferedReader(new FileReader(propfile)));
        } catch (FileNotFoundException fnfe) {
            // no problem; just proceed with an empty properties file
        }

        String catname = props.getProperty("category", category.name);
        if (catname == null) {
            log.warning("Property file missing category", "file", propfile);
            return null;
        }

        int sufidx = fname.indexOf(".properties");
        if (sufidx != -1) {
            fname = fname.substring(0, sufidx);
        }
        category = new Category(catname, fname);
        List<Item> items = Lists.newArrayList();
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

        if (category.equals(Category.EVENTS)) {
            Collections.sort(items);
        }

        // TODO: merge loaded data with in-memory category if it has been modified recently
        synchronized (this) {
            _cats.put(category, items);
            _catinfo.put(category, new CategoryInfo(category, fname, propfile));
        }

        return category;
    }

    protected void loadJournalCategory (Category category)
    {
        // make sure the year directory exists
        File propfile = new File(_propdir, category.file + ".properties");
        File parent = propfile.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                log.warning("Failed to create journal year directory", "dir", parent);
            }
        }

        try {
            loadCategory(category.file, propfile, category);
        } catch (IOException ioe) {
            log.warning("Failed to load journal category", "file", propfile, ioe);
        }
    }

    protected void checkModified ()
    {
        // make a first pass detecting which are modified
        Set<CategoryInfo> modified = Sets.newHashSet();
        for (CategoryInfo catinfo : _catinfo.values()) {
            if (catinfo.checkNewer()) {
                modified.add(catinfo);
            }
        }

        // then reload which will modify the catinfo table
        for (CategoryInfo catinfo : modified) {
            log.info("Reloading modified category " + catinfo.source);
            try {
                loadCategory(catinfo.sourceName, catinfo.source, catinfo.category);
                categoryUpdated(catinfo.category);
            } catch (IOException ioe) {
                log.warning("Failed reloading category '" + catinfo.source + "'.", ioe);
            }
        }
    }

    protected static class CategoryInfo
    {
        public Category category;
        public String sourceName;
        public File source;
        public long lastModified;

        public CategoryInfo (Category category, String sourceName, File source) {
            this.category = category;
            this.sourceName = sourceName;
            this.source = source;
            this.lastModified = source.lastModified();
        }

        public synchronized boolean checkNewer () {
            long newLastMod = source.lastModified();
            if (newLastMod > lastModified) {
                lastModified = newLastMod;
                return true;
            }
            return false;
        }

        public synchronized void flushed () {
            lastModified = source.lastModified();
        }
    }

    protected File _propdir;
    protected Map<Category,List<Item>> _cats = Maps.newHashMap();
    protected Map<Category,CategoryInfo> _catinfo = Maps.newHashMap();
    protected Set<Category> _modified = Sets.newHashSet();
}
