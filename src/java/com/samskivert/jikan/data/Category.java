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

/**
 * Contains the information associated with a category of items.
 */
public class Category
    implements Comparable<Category>
{
    /** A special category for events. */
    public static final Category EVENTS = new Category("__EVENTS__", "events");

    /** This category's human readable name. */
    public final String name;

    /** The name of the file in which this category stores data. */
    public final String file;

    public Category (String name, String file)
    {
        this.name = name;
        this.file = file;
    }

    // from interface Comparable<Category>
    public int compareTo (Category other)
    {
        return this.name.compareTo(other.name);
    }

    @Override // from Object
    public int hashCode ()
    {
        return this.file.hashCode();
    }

    @Override // from Object
    public boolean equals (Object other)
    {
        return this.file.equals(((Category)other).file);
    }

    @Override // from Object
    public String toString ()
    {
        return this.name + " (" + this.file + ")";
    }
}
