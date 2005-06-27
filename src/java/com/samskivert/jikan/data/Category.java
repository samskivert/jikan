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

/**
 * Contains the information associated with a category of items.
 */
public class Category
{
    /** A special category for events. */
    public static final Category EVENTS = new Category();

    /**
     * Initializes a category instance with the supplied name and file.
     */
    public void init (String name, String file)
    {
        _name = name;
        _file = file;
    }

    public String getName ()
    {
        return _name;
    }

    public void setName (String name)
    {
        _name = name;
    }

    public String getFile ()
    {
        return _file;
    }

    @Override // documentation inherited
    public int hashCode ()
    {
        return _file.hashCode();
    }

    @Override // documentation inherited
    public boolean equals (Object other)
    {
        return _file.equals(((Category)other)._file);
    }

    public String toString ()
    {
        return _name + " (" + _file + ")";
    }

    protected String _name, _file;

    static {
        EVENTS.init("__EVENTS__", "events");
    }
}
