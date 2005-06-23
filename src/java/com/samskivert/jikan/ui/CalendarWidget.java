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

package com.samskivert.jikan.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.samskivert.jikan.data.Event;
import com.samskivert.jikan.data.Item;
import com.samskivert.jikan.Jikan;

import static com.samskivert.jikan.Jikan.log;

/**
 * Displays a calendar and a set of icons indicating events on certain
 * days.
 */
public class CalendarWidget extends Canvas
{
    public CalendarWidget (Composite parent)
    {
        super(parent, 0);

        // display 5 weeks at a time
        _wcount = 5;

	addPaintListener(new PaintListener() {
            public void paintControl (PaintEvent e) {
                paint(e);
            }
	});

        if (_headers[0] == null) {
            for (int dd = 0; dd < _headers.length; dd++) {
                _cal.set(Calendar.DAY_OF_WEEK, dd+Calendar.SUNDAY);
                _headers[dd] = _hfmt.format(_cal.getTime());
            }
        }

        Date today = new Date();
        _cal.setTime(today);
        _tdate = _cal.get(Calendar.DAY_OF_YEAR);
        _tyear = _cal.get(Calendar.YEAR);

        setStartDate(today);

        addMouseListener(new MouseAdapter() {
            public void mouseDown (MouseEvent event) {
                if (event.button == 3) {
                    createEvent(event.x, event.y);
                }
            }
        });
    }

    public void setStartDate (Date when)
    {
        _cal.setTime(when);
        _sweek = _cal.get(Calendar.WEEK_OF_YEAR);
    }

    public void setEvents (EventList elist, Iterator<Item> events)
    {
        _elist = elist;

        while (events.hasNext()) {
            Event event = (Event)events.next();
            List<Event> devents = _events.get(event.getDate());
            if (devents == null) {
                _events.put(event.getDate(), devents = new ArrayList<Event>());
            }
            devents.add(event);
        }
    }

    @Override // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);

        // compute some metrics
        GC gc = new GC(this);
        try {
            _csize = width / 7;
            _hheight = 0;
            for (int dd = 0; dd < _hpos.length; dd++) {
                Point hs = gc.stringExtent(_headers[0]);
                _hheight = Math.max(_hheight, hs.y);
                _hpos[dd] = new Point((_csize*dd) + (_csize-hs.x)/2, 0);
            }
            _hheight += 5;

        } finally {
            gc.dispose();
        }

        // compute our precise height for this width and force a relayout
        // of our parent if we're not the right size
        int pheight = _hheight + _wcount * _csize;
        if (pheight != height) {
            _psize.x = width;
            _psize.y = pheight;
            getDisplay().asyncExec(new Runnable() {
                public void run () {
                    getParent().layout();
                }
            });
        }
    }

    @Override // documentation inherited
    public Point computeSize (int wHint, int hHint, boolean changed)
    {
        return (wHint < 0) ? _psize : new Point(wHint, (wHint/7)*6);
    }

    protected void paint (PaintEvent event)
    {
        GC gc = event.gc;

        // draw the days of the week
        for (int dd = 0; dd < 7; dd++) {
            gc.drawString(_headers[dd], _hpos[dd].x, _hpos[dd].y, true);
        }

        // outline the whole business
        int width = getSize().x, cheight = (_wcount*_csize);
        gc.drawRectangle(0, _hheight, width-1, cheight-1);

        // draw the verticla lines
        int xx = _csize, yy = _hheight;
        for (int ii = 0; ii < 6; ii++) {
            gc.drawLine(xx, yy, xx, _hheight+cheight-1);
            xx += _csize;
        }

        // draw the horizontal lines and the text
        _cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        _cal.set(Calendar.WEEK_OF_YEAR, _sweek);
        Color bgcolor = gc.getBackground();
        int month = -1;
        for (int ii = 0; ii < _wcount; ii++) {
            gc.drawLine(0, yy, width, yy);
            xx = 0;
            for (int dd = 0; dd < 7; dd++) {
                if (isToday(_cal)) {
                    gc.setBackground(Jikan.config.getTodayColor());
                    gc.fillRectangle(xx+1, yy+1, _csize-1, _csize-1);
                    gc.setBackground(bgcolor);
                }
                String date;
                int dmonth = _cal.get(Calendar.MONTH);
                if (dmonth != month) {
                    date = _sfmt.format(_cal.getTime());
                    month = dmonth;
                } else {
                    date = _dfmt.format(_cal.getTime());
                }
                _cal.add(Calendar.DATE, 1);
                Point te = gc.stringExtent(date);
                gc.drawString(date, xx + _csize - te.x - 3, yy + 3, true);
                xx += _csize;
            }
            yy += _csize;
        }
    }

    protected boolean isToday (Calendar cal)
    {
        // TODO: update our notion of "today" every few minutes
        return (_cal.get(Calendar.DAY_OF_YEAR) == _tdate &&
                _cal.get(Calendar.YEAR) == _tyear);
    }

    protected void createEvent (int cx, int cy)
    {
        // determine which day the user clicked on
        int row = (cy-_hheight) / _csize, col = Math.min(cx / _csize, 6);
        _cal.set(Calendar.WEEK_OF_YEAR, _sweek + row);
        _cal.set(Calendar.DAY_OF_WEEK, col + 1);
        _elist.createEvent(_cal.getTime());
    }

    protected EventList _elist;

    protected int _sweek;
    protected int _tdate, _tyear;
    protected int _hheight, _csize, _wcount;
    protected HashMap<Date,List<Event>> _events =
        new HashMap<Date,List<Event>>();

    protected Point _psize = new Point(55*7, 55*6);

    protected static String[] _headers = new String[7];
    protected static Point[] _hpos = new Point[7];

    protected static Calendar _cal = Calendar.getInstance();
    protected static SimpleDateFormat _hfmt = new SimpleDateFormat("EEE");
    protected static SimpleDateFormat _sfmt = new SimpleDateFormat("MMM d");
    protected static SimpleDateFormat _dfmt = new SimpleDateFormat("d");
}
