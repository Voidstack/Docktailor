// Copyright © 2024-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx.input;

import com.enosi.docktailor.common.util.CMap;
import com.enosi.docktailor.docktailor.fx.input.internal.EHandlers;
import com.enosi.docktailor.docktailor.fx.input.internal.HPriority;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;

import java.util.function.BooleanSupplier;

/**
 * Input Map class serves as a repository of event handlers and key mappings, arbitrating the event processing between
 * application and the skin.
 */
public class InputMap {
    // Func -> Runnable
    // KB -> Func or Runnable
    // EventType -> EHandlers, or null for key binding handler
    static final Object TYPES = new Object();
    private final EventTarget eventTarget;
    private final CMap<Object, Object> map = new CMap<>(16);

    private final EventHandler<Event> eventHandler = this::handleEvent;


    public InputMap(EventTarget c) {
        this.eventTarget = c;
    }


    public void regKey(KB k, Runnable r) {
        map.put(k, r);
        EventType<KeyEvent> t = k.getEventType();
        addHandler(t, HPriority.USER_KB, null);
    }


    public void regKey(KB k, Func f) {
        map.put(k, f);
        EventType<KeyEvent> t = k.getEventType();
        addHandler(t, HPriority.USER_KB, null);
    }


    public void regFunc(Func f, Runnable r) {
        map.put(f, r);
    }


    /**
     * Adds a user event handler which is guaranteed to be called before any of the skin's event handlers.
     */
    public <T extends Event> void addHandler(EventType<T> type, EventHandler<T> h) {
        addHandler(type, HPriority.USER_EH, h);
    }


    private void handleEvent(Event ev) {
        if (ev.isConsumed()) {
            // why is fx dispatching consumed events?
            return;
        }

        EventType<?> t = ev.getEventType();
        Object x = map.get(t);
        if (x instanceof EHandlers hs) {
            hs.forEachHandler((pri, h) ->
            {
                if (h == null) {
                    handleKeyBindingEvent(ev);
                } else {
                    h.handle(ev);
                }
                return !ev.isConsumed();
            });
        }
    }


    private void handleKeyBindingEvent(Event ev) {
        KB k = KB.fromKeyEvent((KeyEvent) ev);
        if (k != null) {
            boolean consume = handleKeyBinding(k);
            if (consume) {
                ev.consume();
            }
        }
    }


    // returns true if the event must be consumed
    private boolean handleKeyBinding(KB k) {
        Object v = map.get(k);

        if (v != null) {
            if (v instanceof Func f) {
                return exec(f);
            } else if (v instanceof Runnable r) {
                r.run();
                return true;
            } else if (v instanceof BooleanSupplier h) {
                return h.getAsBoolean();
            }
        }
        return false;
    }


    private boolean execFunc(Func f) {
        Object v = map.get(f);
        if (v instanceof Runnable r) {
            r.run();
            return true;
        } else {
            return false;
        }
    }

    private <T extends Event> void addHandler(EventType<T> t, HPriority pri, EventHandler<T> handler) {
        Object v = map.get(t);
        EHandlers hs;
        if (v instanceof EHandlers h) {
            hs = h;
        } else {
            hs = new EHandlers();
            map.put(t, hs);
            eventTarget.addEventHandler(t, eventHandler); // TODO if handler == null, then key bindings eH
        }
        hs.add(pri, handler);
    }

    // returns true if the event must be consumed
    public boolean exec(Func f) {
        Object v = map.get(f);
        if (v instanceof Runnable r) {
            r.run();
            return true;
        }
        return false;
    }
}