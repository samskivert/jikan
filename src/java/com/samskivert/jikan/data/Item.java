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

import java.util.Properties;

/**
 * Contains information on a particular item.
 */
public class Item
    implements Comparable<Item>
{
    public Item (Category category, String text)
    {
        _category = category;
        _text = text;
    }

    public Item (Category category, Properties props, int index)
    {
        _category = category;
        _text = props.getProperty("item" + index);
    }

    public Category getCategory ()
    {
        return _category;
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
        return "[cat=" + _category.getName() + ", text=" + _text + "]";
    }

    // documentation inherited from interface Comparable
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

    protected Category _category;
    protected String _text;
    protected ItemStore _store;
}
