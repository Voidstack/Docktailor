package com.enosistudio.docktailor.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DocktailorEvent {
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /** Adds a listener */
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /** Removes a listener */
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /** Triggers the event for all listeners */
    public void invoke() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /** Checks if a listener is already registered */
    public boolean hasListener(Runnable listener) {
        return listeners.contains(listener);
    }

    /** Removes all listeners */
    public void clear() {
        listeners.clear();
    }
}
