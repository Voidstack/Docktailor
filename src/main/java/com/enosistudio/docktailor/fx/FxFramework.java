package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.fxdock.internal.DemoDockSchema;
import com.enosistudio.docktailor.fxdock.internal.ServiceDocktailor;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * FX Application Framework.
 */
// TODO shorter name?
@Slf4j
public class FxFramework {
    @Getter
    private static FxSettingsSchema schema;

    private FxFramework() {
    }

    public static void openDockSystemConf(String fileName) {
        log.info("Docktailor : Ouverture de la configuration d'interface, {}", ServiceDocktailor.getInstance().getLastUIConfigUsed());
        GlobalSettings.getInstance().setFileProvider(fileName);
        AGlobalSettings store = GlobalSettings.getInstance();
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);

        if (schema != null) {
            FxFramework.closeCurrentLayout();
        }

        schema = demoDockSchema;
        schema.openLayout();
    }

    /**
     * Permet de réouvrir le current FxSettingsSchema.
     */
    private static void closeCurrentLayout() {
        // fermeture
        schema.closeLayout();
    }

    public static Stage createDefaultWindow() {
        return schema.createDefaultWindow();
    }

    public static void storeLayout() {
        schema.storeLayout();
    }

    public static void storeLayout(String fileName) {
        schema.storeLayout(fileName);
    }

    public static void store(Window w) {
        schema.storeWindow(w);
    }

    public static void restore(Window w) {
        if (schema != null) {
            schema.restoreWindow(w);
        }
    }

    public static void exit() {
        storeLayout();
        WindowMonitor.exit();
    }
}
