package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;

public interface IFxDockPane {
    ReadOnlyObjectWrapper<Node> getDockParent();
}
