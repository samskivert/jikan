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

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.eclipse.swt.graphics.Color;

/**
 * Extends an item with the notion of date (and possibly time).
 */
public class Event extends Item
    implements Comparable<Event>
{
    public Event (String text, boolean allday, Date when, int duration)
    {
        super(Category.EVENTS, text);
        _allday = allday;
        _when = when;
        _duration = duration;
        _date = normalize(_when);
    }

    public Event (Properties props, int index)
    {
        super(Category.EVENTS, props, index);
        String key = "item" + index;
        _allday = "true".equals(props.getProperty(key + ".allday"));
        _when = new Date(PropUtil.getLongProperty(props, key + ".when"));
        _duration = PropUtil.getIntProperty(props, key + ".duration");
        _date = normalize(_when);
    }

    /**
     * Returns true if this event is an all day event.
     */
    public boolean isAllDay ()
    {
        return _allday;
    }

    /**
     * Returns the date on which this event starts (the time components of
     * the returned date will be normalized to zero milliseconds after
     * midnight).
     */
    public Date getDate ()
    {
        return _date;
    }

    /**
     * Returns the date and time at which this event starts.
     */
    public Date getWhen ()
    {
        return _when;
    }

    /**
     * Updates the date and time at which this event starts.
     */
    public void setWhen (Date when, boolean allday)
    {
        if (!when.equals(_when) || allday != _allday) {
            _when = when;
            _allday = allday;
            notifyModified();
        }
    }

    /**
     * Returns the duration of this event which will be in minutes if the
     * event has a date and time, days if the event has only a date (it's
     * an "all day" event.
     */
    public int getDuration ()
    {
        return _duration;
    }

    /**
     * Assigns a color to this event.
     */
    public void setColor (Color color)
    {
        _color = color;
    }

    /**
     * Returns the color assigned to this event for use when displaying it
     * on the calendar widget.
     */
    public Color getColor ()
    {
        return _color;
    }

    // documentation inherited from interface Comparable
    public int compareTo (Event other)
    {
        return _when.compareTo(other._when);
    }

    public String toString ()
    {
        return "[cat=" + _category.getName() + ", when=" + _when +
            ", allday=" + _allday + ", text=" + _text + "]";
    }

    protected void store (Properties props, int index)
    {
        super.store(props, index);
        String key = "item" + index;
        props.setProperty(key + ".allday", String.valueOf(_allday));
        props.setProperty(key + ".when", String.valueOf(_when.getTime()));
        props.setProperty(key + ".duration", String.valueOf(_duration));
    }

    protected Date normalize (Date date)
    {
        _cal.setTime(date);
        _cal.set(Calendar.HOUR, 0);
        _cal.set(Calendar.MINUTE, 0);
        _cal.set(Calendar.SECOND, 0);
        _cal.set(Calendar.MILLISECOND, 0);
        return _cal.getTime();
    }

    protected Date _date;
    protected Date _when;
    protected int _duration;
    protected boolean _allday;
    protected Color _color;

    protected static Calendar _cal = Calendar.getInstance();
}
