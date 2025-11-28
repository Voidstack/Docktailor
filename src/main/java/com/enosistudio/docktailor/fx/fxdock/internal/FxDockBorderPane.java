package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import lombok.Getter;


/**
 * FxDockRootPane.
 */
public class FxDockBorderPane extends BorderPane implements IFxDockPane {
    @Getter
    protected final ReadOnlyObjectWrapper<Node> dockParent = new ReadOnlyObjectWrapper<>();

    public FxDockBorderPane() {
        setCenter(new FxDockEmptyPane());
    }
}
