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

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

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
        String key = "item" + index;
        _text = props.getProperty(key);
        for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements(); ) {
            String pname = e.nextElement().toString();
            if (pname.startsWith(key + ".ext.")) {
                _exids.put(pname.substring(key.length()+5), props.getProperty(pname));
            }
        }
    }

    public String getText ()
    {
        return _text;
    }

    public void setText (String text)
    {
        if (!text.equals(_text)) {
            _text = text;
            notifyUpdated();
        }
    }

    public String getExternalId (String source)
    {
        return _exids.get(source);
    }

    public void setExternalId (String source, String id)
    {
        String oid = _exids.put(source, id);
        if (!id.equals(oid)) {
            notifyUpdated();
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

    protected void setJournal (ItemJournal journal)
    {
        _journal = journal;
    }

    protected void notifyUpdated ()
    {
        if (_journal != null) {
            _journal.itemUpdated(this);
        }
    }

    protected void store (Properties props, int index)
    {
        String key = "item" + index;
        props.setProperty(key, _text);
        for (Map.Entry<String, String> entry : _exids.entrySet()) {
            props.setProperty(key + ".ext." + entry.getKey(), entry.getValue());
        }
    }

    protected String _text;
    protected Map<String, String> _exids = Maps.newHashMap();
    protected ItemJournal _journal;
}
