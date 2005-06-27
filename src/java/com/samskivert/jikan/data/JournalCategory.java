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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A category identifier for a particular journal date.
 */
public class JournalCategory extends Category
{
    /**
     * Creates a journal category entry for the specified date.
     */
    public JournalCategory (Date when)
    {
        init(_nfmt.format(when), _ffmt.format(when));
    }

    protected static DateFormat _nfmt =
        DateFormat.getDateInstance(DateFormat.MEDIUM);
    protected static SimpleDateFormat _ffmt =
        new SimpleDateFormat("yyyy/MM-dd");
}
