package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class that allows creating a dockable class.
 */
@Slf4j
public abstract class ADockPane implements IDockPane {
    @Override
    public FxDockPane createDockPane() {
        FxDockPane fxDockPane = new FxDockPane(this.getTabName()) {
        };

        fxDockPane.setTitle(this.getTabName());
        fxDockPane.setContent(this.loadView());
        fxDockPane.getTitleField().setGraphic(this.getTabIcon());

        return fxDockPane;
    }
}
