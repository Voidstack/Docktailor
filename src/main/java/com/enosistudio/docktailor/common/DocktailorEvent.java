package com.enosistudio.docktailor.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class DocktailorEvent<T> {
    private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

    /** Adds a listener */
    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    /** Removes a listener */
    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    /** Triggers the event for all listeners */
    public void invoke(T value) {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    /** Checks if a listener is already registered */
    public boolean hasListener(Consumer<T> listener) {
        return listeners.contains(listener);
    }

    /** Removes all listeners */
    public void clear() {
        listeners.clear();
    }
}
