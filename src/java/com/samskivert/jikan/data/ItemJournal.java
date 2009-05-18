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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import static com.samskivert.jikan.Jikan.log;

/**
 * Maintains a journal of item additions, updates and deletions and dispatches events indicating
 * such to listeners. When all listeners have confirmed the processing of an event, it is purged
 * from the journal.
 */
public class ItemJournal
{
    /** Represents an action taken on an item. */
    public static class Event
    {
        public enum Type { ADD, UPDATE, DELETE };

        /** A unique identifier for this event. */
        public final UUID id;

        /** Indicates what happened to the item in question. */
        public final Type type;

        /** The item that was added, updated or deleted. */
        public final Item item;

        public Event (UUID id, Type type, Item item) {
            this.id = id;
            this.type = type;
            this.item = item;
        }
    }

    /** An interface implemented by listeners of the journal. */
    public static interface Listener
    {
        /**
         * Returns an identifier for this listener which will be used to track (between JVM
         * invocations) whether or not this listener has processed any particular event.
         */
        public String getId ();

        /**
         * Called when an event has been added to the journal, or when the application starts up
         * and there are unprocessed events in the journal for this listener.
         */
        public void onEvent (Event event);
    }

    /**
     * Initializes the item journal, reading in any journal files and dispatching unprocessed
     * events. Don't call this until all listeners have been registered via {@link #addListener}.
     */
    public void init (File propdir)
    {
        _propdir = propdir;
        // TODO: read journal files, dispatch events, etc.
    }

    /**
     * Registers a listener to receive events as they are added to this journal.
     */
    public void addListener (Listener listener)
    {
        _listeners.put(listener.getId(), listener);
    }

    /**
     * Notifies the journal that an item has been added.
     */
    public void itemAdded (Item item)
    {
        dispatchEvent(Event.Type.ADD, item);
    }

    /**
     * Notifies the journal that an item has been updated.
     */
    public void itemUpdated (Item item)
    {
        dispatchEvent(Event.Type.UPDATE, item);
    }

    /**
     * Notifies the journal that an item has been deleted.
     */
    public void itemDeleted (Item item)
    {
        dispatchEvent(Event.Type.DELETE, item);
    }

    /**
     * Confirms that the specified event has been processed by the specified listener.
     */
    public void confirmEvent (UUID id, Listener listener)
    {
        _confirmations.put(id, listener.getId());
        if (_confirmations.get(id).equals(_listeners.keySet())) {
            Event event = _events.remove(id);
            log.info("Purging event", "id", id, "type", event.type,
                     "confirmed", _confirmations.get(id));
            _confirmations.removeAll(id);
        }
    }

    protected void dispatchEvent (Event.Type type, Item item)
    {
        Event event = new Event(UUID.randomUUID(), type, item);
        _events.put(event.id, event);
        // TODO: store the event persistently
        for (Listener listener : _listeners.values()) {
            listener.onEvent(event);
        }
    }

    protected File _propdir;
    protected Map<String, Listener> _listeners = Maps.newHashMap();
    protected Map<UUID, Event> _events = Maps.newHashMap();
    protected Multimap<UUID, String> _confirmations = HashMultimap.create();
}
