package com.enosistudio.docktailor.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DocktailorEvent {
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /** Ajoute un listener */
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /** Supprime un listener */
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /** Déclenche l’événement pour tous les listeners */
    public void invoke() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /** Vérifie si un listener est déjà enregistré */
    public boolean hasListener(Runnable listener) {
        return listeners.contains(listener);
    }

    /** Supprime tous les listeners */
    public void clear() {
        listeners.clear();
    }
}
