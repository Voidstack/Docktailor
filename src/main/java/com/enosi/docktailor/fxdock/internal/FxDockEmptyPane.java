package com.enosi.docktailor.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import lombok.NoArgsConstructor;

/**
 * FxDockEmptyPane.
 */
@NoArgsConstructor
public class FxDockEmptyPane extends BorderPane {
    protected final ReadOnlyObjectWrapper<Node> parent = new ReadOnlyObjectWrapper<>();
    public final ReadOnlyProperty<Node> dockParentProperty() {
        return parent.getReadOnlyProperty();
    }
}