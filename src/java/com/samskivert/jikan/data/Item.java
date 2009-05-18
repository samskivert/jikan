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

import java.util.Properties;

/**
 * Contains information on a particular item.
 */
public class Item
    implements Comparable<Item>
{
    /** The category to which this item belongs. */
    public final Category category;

    public Item (Category category, String text)
    {
        this.category = category;
        _text = text;
    }

    public Item (Category category, Properties props, int index)
    {
        this.category = category;
        _text = props.getProperty("item" + index);
    }

    public String getText ()
    {
        return _text;
    }

    public void setText (String text)
    {
        if (!text.equals(_text)) {
            _text = text;
            notifyModified();
        }
    }

    public String toString ()
    {
        return "[cat=" + this.category.name + ", text=" + _text + "]";
    }

    // from interface Comparable<Item>
    public int compareTo (Item other)
    {
        // some day we'll add a total ordering
        return _text.compareTo(other._text);
    }

    protected void setStore (ItemStore store)
    {
        _store = store;
    }

    protected void notifyModified ()
    {
        if (_store != null) {
            _store.itemModified(this);
        }
    }

    protected void store (Properties props, int index)
    {
        props.setProperty("item" + index, _text);
    }

    protected String _text;
    protected ItemStore _store;
}
