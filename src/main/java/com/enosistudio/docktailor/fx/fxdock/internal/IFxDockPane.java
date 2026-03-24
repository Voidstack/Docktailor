package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;

public interface IFxDockPane {
    ReadOnlyObjectProperty<Node> dockParentProperty();

    void setDockParent(Node parent);
}
