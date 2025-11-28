package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import com.enosistudio.docktailor.fx.fxdock.FxDockSchema;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import com.enosistudio.docktailor.fx.fxdock.internal.IDockPane;

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

        throw new IllegalArgumentException("Docktailor configuration file is corrupted");
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
