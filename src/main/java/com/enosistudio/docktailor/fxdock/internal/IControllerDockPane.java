package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.fxdock.FxDockPane;
import javafx.geometry.Side;
import javafx.scene.Node;

/**
 * C'est le controller qui doit hériter de cette interface.
 */
public interface IControllerDockPane {
    Side getDefaultSide();

    String getTabName();

    String getTabIcon();

    /**
     * Get root node of the fxml.
     *
     * @return : String
     */
    Node getView();

    String getInformation();

    FxDockPane createDockPane();
}