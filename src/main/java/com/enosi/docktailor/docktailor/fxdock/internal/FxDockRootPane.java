package com.enosi.docktailor.docktailor.fxdock.internal;

import com.enosi.docktailor.docktailor.fxdock.FxDockStyles;
import com.enosi.docktailor.docktailor.fxdock.FxDockWindow;
import javafx.scene.Node;


/**
 * FxDockRootPane.
 */
public class FxDockRootPane
        extends FxDockBorderPane {
    private final FxDockWindow window;


    public FxDockRootPane(FxDockWindow w) {
        this.window = w;

        FxDockStyles.FX_ROOT_PANE.set(this);
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