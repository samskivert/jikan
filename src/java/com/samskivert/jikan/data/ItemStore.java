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

import java.util.Iterator;

import com.samskivert.util.Interval;

/**
 * Defines an interface to a persistent store of item data for Jikan.
 */
public abstract class ItemStore
{
    /** An interface used to inform users of a store when a category has
     * been updated asynchronously. See {@link #setStoreListener}. */
    public interface StoreListener
    {
        /**
         * Called when a category has been changed by an external
         * process. Note that this method will be called on a timer thread
         * and because access to the store is not thread safe, this
         * callback should simply notify the main thread that accesses the
         * store to reload the data in the specified category.
         */
        public void categoryUpdated (Category category);
    }

    /**
     * Returns the names of all categories in this store.
     */
    public abstract Iterator<Category> getCategories ();

    /**
     * Returns a list of {@link Item} instances for the specified
     * category.
     */
    public abstract Iterator<Item> getItems (Category category);

    /**
     * Adds the supplied item to the appropriate category. The repository
     * should automatically queue up a flush.
     */
    public abstract void addItem (Item item);

    /**
     * Removes the specified item from the appropriate category. The
     * repository should automatically queue up a flush.
     */
    public abstract void deleteItem (Item item);

    /**
     * Called by an item when it is modified. The repository should mark
     * the appropriate category as modified and queue up a flush.
     */
    public abstract void itemModified (Item item);

    /**
     * Writes all modified categories to the persistent store.
     * <em>Note:</em> this may run asynchronously with normal item store
     * methods, so it should take care to isolate data to be flushed in a
     * synchronized block.
     */
    public abstract void flushModified ();

    /**
     * Queues up a request to call {@link #flushModified}.
     */
    public synchronized void queueFlush ()
    {
        if (_flusher == null) {
            _flusher = new Interval() {
                public void expired () {
                    flushModified();
                    synchronized (ItemStore.this) {
                        _flusher = null;
                    }
                }
            };
            _flusher.schedule(5000L);
        }
    }

    /**
     * Cancels any pending flush and immediately flushes all
     * modifications.
     */
    public synchronized void shutdown ()
    {
        if (_flusher != null) {
            _flusher.cancel();
            _flusher = null;
            flushModified();
        }
    }

    /**
     * Configures a listener that will be notified when any category is
     * changed externally by some other process.
     */
    public void setStoreListener (StoreListener listener)
    {
        _listener = listener;
    }

    /**
     * Notifies any registered listener that the specified category has
     * been updated.
     */
    protected void categoryUpdated (Category category)
    {
        if (_listener != null) {
            _listener.categoryUpdated(category);
        }
    }

    protected StoreListener _listener;
    protected Interval _flusher;
}
