package com.enosi.docktailor.docktailor.dock;

import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import com.enosi.docktailor.docktailor.fxdock.FxSimpleDockPane;

/**
 * abstract qui permet de créer une classe Dockable.
 */
public abstract class AControllerDockPane implements IControllerDockPane {
    @Override
    public FxDockPane createDockPane() {
        FxDockPane fxDockPane = new FxSimpleDockPane(this.getTabName());
        fxDockPane.setTitle(this.getTabName());
        fxDockPane.setContent(this.getView());

        return fxDockPane;
    }
}
