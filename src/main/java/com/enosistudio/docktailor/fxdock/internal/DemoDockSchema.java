package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.fxdock.FxDockPane;
import com.enosistudio.docktailor.fxdock.FxDockSchema;
import javafx.stage.Stage;

/**
 * Demo Schema creates custom dock windows and dock panes.
 */
public class DemoDockSchema extends FxDockSchema {
    public static final String BROWSER = "BROWSER";

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
        for (IDockPane newInstance : ServiceDocktailor.getInstance().getNewInstances()) {
            if (id.equals(newInstance.getTabName())) {
                return newInstance.createDockPane();
            }
        }

        throw new IllegalArgumentException("Le fichier de configuration pour docktailor est corrompue");
    }

    @Override
    public Stage createWindow(String name) {
        return new DemoWindow();
    }

    @Override
    public Stage createDefaultWindow() {
        return new DemoWindow();
    }
}
