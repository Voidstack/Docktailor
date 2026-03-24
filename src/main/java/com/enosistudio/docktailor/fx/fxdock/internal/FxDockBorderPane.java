package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;


/**
 * FxDockRootPane.
 */
public class FxDockBorderPane extends BorderPane implements IFxDockPane {
    protected final ReadOnlyObjectWrapper<Node> dockParent = new ReadOnlyObjectWrapper<>();

    public FxDockBorderPane() {
        setCenter(new FxDockEmptyPane());
    }

    @Override
    public ReadOnlyObjectProperty<Node> dockParentProperty() {
        return dockParent.getReadOnlyProperty();
    }

    @Override
    public void setDockParent(Node parent) {
        dockParent.set(parent);
    }
}
