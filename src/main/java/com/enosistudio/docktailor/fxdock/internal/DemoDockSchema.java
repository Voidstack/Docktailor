package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.fxdock.FxDockPane;
import com.enosistudio.docktailor.fxdock.FxDockSchema;
import com.enosistudio.docktailor.fxdock.FxDockWindow;

/**
 * Demo Schema creates custom dock windows and dock panes.
 */
public class DemoDockSchema extends FxDockSchema {
    public DemoDockSchema(AGlobalSettings store) {
        super(store);
    }

    /**
     * Creates a new dock pane based on the given id.
     * @param id the id of the dock pane
     * @return the dock pane for the given id
     * @throws IllegalArgumentException if the id does not match any known dockable pane
     */
    @Override
    public FxDockPane createPane(String id) throws IllegalArgumentException {
        for (IDockPane newInstance : DocktailorService.getInstance().getNewInstances()) {
            if (id.equals(newInstance.getTabName())) {
                return newInstance.createDockPane();
            }
        }

        throw new IllegalArgumentException("Le fichier de configuration pour docktailor est corrompue");
    }

    @Override
    public FxDockWindow createWindow(String name) {
        return new DemoDockWindow();
    }

    @Override
    public FxDockWindow createDefaultWindow() {
        return new DemoDockWindow();
    }
}
