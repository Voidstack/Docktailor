package com.enosi.docktailor.docktailor.fxdock.internal;

import com.enosi.docktailor.docktailor.fxdock.FxDockStyles;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * FxDockEmptyPane.
 */
public class FxDockEmptyPane
        extends BorderPane {
    protected final ReadOnlyObjectWrapper<Node> parent = new ReadOnlyObjectWrapper<Node>();

    public FxDockEmptyPane() {
        FxDockStyles.FX_EMPTY_PANE.set(this);
    }

    public final ReadOnlyProperty<Node> dockParentProperty() {
        return parent.getReadOnlyProperty();
    }
}