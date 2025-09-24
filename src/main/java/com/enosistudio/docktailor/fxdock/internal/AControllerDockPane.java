package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.fxdock.FxDockPane;
import lombok.extern.slf4j.Slf4j;

/**
 * abstract qui permet de créer une classe Dockable.
 */
@Slf4j
public abstract class AControllerDockPane implements IControllerDockPane {
    @Override
    public FxDockPane createDockPane() {
        FxDockPane fxDockPane = new FxDockPane(this.getTabName()) {};

        fxDockPane.setTitle(this.getTabName());
        fxDockPane.setContent(this.getView());

        return fxDockPane;
    }
}
