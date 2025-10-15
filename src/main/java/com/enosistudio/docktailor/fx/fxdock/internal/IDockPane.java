package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import javafx.geometry.Side;
import javafx.scene.Node;

/**
 * C'est le controller qui doit hériter de cette interface.
 */
public interface IDockPane {
    Side getDefaultSide();

    String getTabName();

    Node getTabIcon();

    /**
     * Get root node of the fxml.
     * @return : String
     */
    Node loadView();

    String getInformation();

    FxDockPane createDockPane();
}