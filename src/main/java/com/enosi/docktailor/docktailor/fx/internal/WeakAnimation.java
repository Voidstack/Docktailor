package org.esioc.docktailor.fx.internal;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.lang.ref.WeakReference;


/**
 * Simple animation helper designed to prevent memory leaks.
 */
public abstract class WeakAnimation<T> {
    private final WeakReference<T> ref;

    //
    private final Timeline timeline;
    public WeakAnimation(T parent, Duration period) {
        ref = new WeakReference<>(parent);

        timeline = new Timeline(new KeyFrame(period, (ev) -> handleFramePrivate()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    protected abstract void handleFrame(T parent);

    protected void handleFramePrivate() {
        T parent = ref.get();
        if (parent == null) {
            stop();
        } else {
            handleFrame(parent);
        }
    }


    public void stop() {
        timeline.stop();
    }
}
