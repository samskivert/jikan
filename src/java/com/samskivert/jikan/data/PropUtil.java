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
import java.util.logging.Level;

import static com.samskivert.jikan.Jikan.log;

/**
 * Property utility methods.
 */
public class PropUtil
{
    public static int getIntProperty (Properties props, String key)
    {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (Exception e) {
            log.log(Level.WARNING, "Unable to read int '" + key + "'.", e);
            return 0;
        }
    }

    public static long getLongProperty (Properties props, String key)
    {
        try {
            return Long.parseLong(props.getProperty(key));
        } catch (Exception e) {
            log.log(Level.WARNING, "Unable to read long '" + key + "'.", e);
            return 0L;
        }
    }
}
