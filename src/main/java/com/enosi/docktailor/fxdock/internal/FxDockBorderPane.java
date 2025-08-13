package com.enosi.docktailor.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;


/**
 * FxDockRootPane.
 */
public class FxDockBorderPane extends BorderPane {
    protected final ReadOnlyObjectWrapper<Node> parent = new ReadOnlyObjectWrapper<>();

    public FxDockBorderPane() {
        setCenter(new FxDockEmptyPane());
    }

    public final ReadOnlyProperty<Node> dockParentProperty() {
        return parent.getReadOnlyProperty();
    }
}
