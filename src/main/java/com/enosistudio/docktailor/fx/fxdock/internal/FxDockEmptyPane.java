package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FxDockEmptyPane.
 */
@NoArgsConstructor
public class FxDockEmptyPane extends BorderPane implements IFxDockPane {
    @Getter
    protected final ReadOnlyObjectWrapper<Node> dockParent = new ReadOnlyObjectWrapper<>();
}