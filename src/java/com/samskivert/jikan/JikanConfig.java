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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.samskivert.util.PrefsConfig;

/**
 * Provides access to all of the configuration information for Jikan.
 */
public class JikanConfig
{
    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int CATEGORY_FONT = 0;

    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int ITEM_FONT = 1;

    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int ICON_FONT = 2;

    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int SMALL_ICON_FONT = 3;

    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int DATE_FONT = 4;

    /** A constant indicating a font used in a particular part of the user interface. */
    public static final int MONTH_FONT = 5;

    /** Indicates the total number of font types. */
    public static final int FONT_TYPES = 6;

    /**
     * Initializes the configuration and creates all the necessary user
     * interface bits.
     */
    public JikanConfig (Display display)
    {
        _display = display;
        _fonts = new Font[FONT_TYPES];
        _todayColor = new Color(display, new RGB(0x99, 0xCC, 0xFF));
        _iconColor = new Color(display, new RGB(0xCC, 0xFF, 0x99));
    }

    /**
     * Returns the specified font.
     */
    public Font getFont (int type)
    {
        if (_fonts[type] == null) {
            FontData data;
            switch (type) {
            case CATEGORY_FONT:
                data = new FontData("Helvetica", 12, SWT.BOLD);
                break;
            default:
            case ITEM_FONT:
                data = new FontData("Helvetica", 12, SWT.BOLD);
                break;
            case ICON_FONT:
                data = new FontData("Helvetica", 9, SWT.NORMAL);
                break;
            case SMALL_ICON_FONT:
                data = new FontData("Helvetica", 7, SWT.NORMAL);
                break;
            case DATE_FONT:
                data = new FontData("Helvetica", 11, SWT.NORMAL);
                break;
            case MONTH_FONT:
                data = new FontData("Helvetica", 11, SWT.ITALIC);
                break;
            }
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
     * Returns the color in which to render the event icons.
     */
    public Color getIconColor ()
    {
        return _iconColor;
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

    /**
     * Returns the saved bounds of the main window.
     */
    public Rectangle getWindowBounds ()
    {
        return new Rectangle(
            _config.getValue("bounds.x", 0),
            _config.getValue("bounds.y", 0),
            _config.getValue("bounds.width", 455),
            _config.getValue("bounds.height", 800));
    }

    /**
     * Saves the preferred size of the main window.
     */
    public void saveWindowBounds (Rectangle bounds)
    {
        _config.setValue("bounds.x", bounds.x);
        _config.setValue("bounds.y", bounds.y);
        _config.setValue("bounds.width", bounds.width);
        _config.setValue("bounds.height", bounds.height);
    }

    /**
     * Returns the most recently selected category.
     */
    public String getSelectedCategory ()
    {
        return _config.getValue("category", "General");
    }

    /**
     * Updates the most recently selected category.
     */
    public void setSelectedCategory (String category)
    {
        _config.setValue("category", category);
    }

    protected PrefsConfig _config = new PrefsConfig("jikan");
    protected Display _display;
    protected Font[] _fonts;
    protected Color _todayColor, _iconColor;
}
