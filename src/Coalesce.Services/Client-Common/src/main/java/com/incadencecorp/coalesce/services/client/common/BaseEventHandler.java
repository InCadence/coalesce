/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.services.client.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.Vector;

import com.incadencecorp.coalesce.services.api.ICoalesceEvents;

/**
 * Base class which the different frameworks extend. This class implements the listener mechanism
 * and provides framework to fire events to the listeners.
 * 
 * @author n78554
 * @param <T>
 *            interface used for raising events.
 */
public class BaseEventHandler<T extends ICoalesceEvents> implements Closeable {

    private transient Vector<T> listeners;
    private boolean isClosing;

    /**
     * Default Constructor
     */
    public BaseEventHandler() {
        isClosing = false;
    }

    /**
     * Registers an event listener
     * 
     * @param listener
     */
    public synchronized void addListener(T listener) {

        if (!isClosing) {

            if (listeners == null) {
                listeners = new Vector<T>();
            }

            if (!listeners.contains(listener)) {
                listeners.addElement(listener);
            }

        }

    }

    /**
     * Unregisters an event listener
     * 
     * @param listener
     */
    public synchronized void removeListener(T listener) {

        if (!isClosing) {

            if (listeners == null) {
                listeners = new Vector<T>();
            }
            listeners.removeElement(listener);
        }

    }

    /**
     * @param listener
     * @return Returns <code>true</code> if already listening.
     */
    public synchronized boolean isListening(T listener) {

        boolean isListening = false;

        if (!isClosing) {
            isListening = listeners.contains(listener);
        }

        return isListening;

    }

    /**
     * @return Returns a clone of the list of listeners.
     */
    protected synchronized Vector<T> getListeners() {

        if (listeners == null) {
            listeners = new Vector<T>();
        }

        return (Vector<T>) listeners.clone();

    }

    @Override
    public void close() throws IOException {

        isClosing = true;

        if (listeners != null) {
            listeners.removeAllElements();
        }

    }
}
