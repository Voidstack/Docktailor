package com.enosi.docktailor.fxdock.internal;

import com.enosi.docktailor.fxdock.FxDockWindow;
import javafx.scene.Node;


/**
 * FxDockRootPane.
 */
public class FxDockRootPane extends FxDockBorderPane {
    private final FxDockWindow window;

    public FxDockRootPane(FxDockWindow w) {
        this.window = w;

        setContent(new FxDockEmptyPane());
    }

    public final Node getContent() {
        return getCenter();
    }

    public final void setContent(Node n) {
        if (n == null) {
            n = new FxDockEmptyPane();
        }
        setCenter(n);
        DockTools.setParent(this, n);
    }
}