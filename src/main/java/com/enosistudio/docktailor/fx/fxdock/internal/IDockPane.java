package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import javafx.geometry.Side;
import javafx.scene.Node;

/**
 * Interface that must be implemented by controllers to create dockable panes.
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

    /**
     * Executed when the tab is discard throught the tab or the window parent, for whatever reason.
     */
    void onClose();

    /**
     * Executed when the tab is dragged out from his window parent.
     */
    void onDragOutFromWindow();
}