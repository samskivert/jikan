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

package com.samskivert.jikan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provides access to all of the configuration information for Jikan.
 */
public class JikanConfig
{
    /** A constant indicating a font used in a particular part of the user
     * interface. */
    public static final int CATEGORY_FONT = 0;

    /** A constant indicating a font used in a particular part of the user
     * interface. */
    public static final int ITEM_FONT = 1;

    /** Indicates the total number of font types. */
    public static final int FONT_TYPES = 2;

    /**
     * Initializes the configuration and creates all the necessary user
     * interface bits.
     */
    public JikanConfig (Display display)
    {
        _display = display;
        _fonts = new Font[FONT_TYPES];
        _todayColor = new Color(display, new RGB(0x99, 0xCC, 0xFF));
    }

    /**
     * Returns the specified font.
     */
    public Font getFont (int type)
    {
        if (_fonts[type] == null) {
            FontData data = new FontData("Helvetica", 12, SWT.BOLD);
            _fonts[type] = new Font(_display, data);
        }
        return _fonts[type];
    }

    /**
     * Configures the font of the specified type.
     */
    public void setFont (int type, FontData data)
    {
        if (_fonts[type] != null) {
            _fonts[type].dispose();
        }
        _fonts[type] = new Font(_display, data);
    }

    /**
     * Returns the color in which to render the current date.
     */
    public Color getTodayColor ()
    {
        return _todayColor;
    }

    /**
     * Cleans up any resources used by the configuration.
     */
    public void dispose ()
    {
        // free our fonts
        for (int ii = 0; ii < FONT_TYPES; ii++) {
            if (_fonts[ii] != null) {
                _fonts[ii].dispose();
            }
        }
    }

    protected Display _display;
    protected Font[] _fonts;
    protected Color _todayColor;
}
