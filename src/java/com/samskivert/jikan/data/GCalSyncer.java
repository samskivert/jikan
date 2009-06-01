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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import com.samskivert.jikan.Jikan;
import com.samskivert.util.Invoker;
import com.samskivert.util.RunQueue;

import static com.samskivert.jikan.Jikan.log;

/**
 * Syncs our item journal with Google Calendar.
 */
public class GCalSyncer
    implements ItemJournal.Listener
{
    public GCalSyncer (ItemJournal journal, String username, String password)
        throws IOException
    {
        _username = username;
        _journal = journal;
        _journal.addListener(this);

        try { // yay for checked exceptions
            _feedURL = new URL(FEED_BASE + username + FEED_SUFFIX);
        } catch (MalformedURLException mue) {
            throw new IOException("Failed to create feed URL", mue);
        }

        try { // create our service, check for recent modifications
            _service = new CalendarService("samskivert-Jikan-1");
            _service.setUserCredentials(username, password);
        } catch (AuthenticationException ae) {
            throw new IOException("Failed to authenticate with Google Calendar", ae);
        }

        // start our background processing thread
        _invoker.start();

        log.info("GCalSyncer running", "username", username);

        // use Query.getUpdatedMin() to check for changes since our most recent sync

//         try {
//             CalendarEventFeed feed = _service.getFeed(_feedURL, CalendarEventFeed.class);
//             for (CalendarEventEntry entry : feed.getEntries()) {
//                 log.info("Entry", "title", entry.getTitle().getPlainText(),
//                          "times", entry.getTimes(), "id", entry.getId(), "uid", entry.getIcalUID());
//             }

//         } catch (ServiceException se) {
//             throw new IOException("Failed to load feed", se);
//         }
    }

    // from interface ItemJournal.Listener
    public String getId ()
    {
        return GCAL_ID + ":" + _username;
    }

    // from interface ItemJournal.Listener
    public void onEvent (ItemJournal.Event event)
    {
        switch (event.type) {
        case ADD:
            log.info("Item added", "id", event.id, "item", event.item);
            if (event.item instanceof Event) {
                eventAdded(event.id, (Event)event.item);
            }
            break;

        case UPDATE:
            log.info("Item updated", "id", event.id, "item", event.item);
            if (event.item instanceof Event) {
                eventUpdated(event.id, (Event)event.item);
            }
            break;

        case DELETE:
            log.info("Item deleted", "id", event.id, "item", event.item);
            if (event.item instanceof Event) {
                eventDeleted(event.id, (Event)event.item);
            }
            break;
        }
    }

    protected void eventAdded (final UUID eventId, final Event event)
    {
        _invoker.postUnit(new Invoker.Unit() {
            public boolean invoke () {
                try {
                    _entry = _service.insert(_feedURL, toEntry(new CalendarEventEntry(), event));
                    return true;
                } catch (Exception e) {
                    log.warning("Failed to sync event", "event", event, e);
                    return false;
                }
            }
            public void handleResult () {
                event.setExternalId(GCAL_ID, _entry.getSelfLink().getHref());
                _journal.confirmEvent(eventId, GCalSyncer.this);
            }
            protected CalendarEventEntry _entry;
        });
    }

    protected void eventUpdated (final UUID eventId, final Event event)
    {
        _invoker.postUnit(new Invoker.Unit() {
            public boolean invoke () {
                try {
                    CalendarEventEntry entry = fetchEvent(event);
                    toEntry(entry, event);
                    entry.update();
                    return true;
                } catch (Exception e) {
                    log.warning("Failed to sync event", "event", event, e);
                    return false;
                }
            }
            public void handleResult () {
                _journal.confirmEvent(eventId, GCalSyncer.this);
            }
        });
    }

    protected void eventDeleted (final UUID eventId, final Event event)
    {
        _invoker.postUnit(new Invoker.Unit() {
            public boolean invoke () {
                try {
                    CalendarEventEntry entry = fetchEvent(event);
                    entry.delete();
                    return true;
                } catch (Exception e) {
                    log.warning("Failed to sync event", "event", event, e);
                    return false;
                }
            }
            public void handleResult () {
                _journal.confirmEvent(eventId, GCalSyncer.this);
            }
        });
    }

    protected CalendarEventEntry toEntry (CalendarEventEntry entry, Event event)
    {
        entry.setTitle(new PlainTextConstruct(event.getText()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(event.getWhen());
        DateTime start = new DateTime(cal.getTime(), TimeZone.getDefault());
        // TODO: handle duration
        cal.add(Calendar.MINUTE, 30);
        DateTime end = new DateTime(cal.getTime(), TimeZone.getDefault());

        When time;
        List<When> times = entry.getTimes();
        if (times.size() > 0) {
            time = times.get(0);
        } else {
            entry.addTime(time = new When());
        }
        time.setStartTime(start);
        time.setEndTime(end);

        return entry;
    }

    protected CalendarEventEntry fetchEvent (Event event)
        throws IOException, ServiceException
    {
        String gcalURL = event.getExternalId(GCAL_ID);
        if (gcalURL == null) {
            throw new IllegalArgumentException("Can't fetch event with no GCal id " + event);
        }
        try {
            return _service.getEntry(new URL(gcalURL), CalendarEventEntry.class, (DateTime)null);
        } catch (MalformedURLException mue) {
            throw new RuntimeException("Event has invalid GCal URL: " + gcalURL, mue);
        }
    }

    protected String _username;
    protected ItemJournal _journal;

    protected URL _feedURL;
    protected CalendarService _service;

    protected Invoker _invoker = new Invoker("gcal_syncer", Jikan.swtQueue);

    protected static final String FEED_BASE = "http://www.google.com/calendar/feeds/";
    protected static final String FEED_SUFFIX = "/private/full";

    protected static final String GCAL_ID = "gcal";
}
