// Copyright © 2024-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.common.util.ASettingsStore;
import com.enosi.docktailor.common.util.GlobalSettings;
import com.enosi.docktailor.docktailor.dock.DemoDockSchema;
import com.enosi.docktailor.docktailor.dock.ServiceDocktailor;
import com.enosi.docktailor.docktailor.fx.settings.FxSettingsSchema;
import com.enosi.docktailor.docktailor.fx.settings.WindowMonitor;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * FX Application Framework.
 */
// TODO shorter name?
@Slf4j
public class FxFramework {
    @Getter
    private static FxSettingsSchema schema;

    /**
     * Opens application windows stored in the global settings. If no settings are stored, invokes the generator with a
     * null name to open the main window.<p> The generator must return a default window when supplied with a null name.
     * To ensure the right settings are loaded, the newly created window must remain hidden.
     */
    public static int openLayout(FxSettingsSchema s) throws Exception {
        if (schema == null) {
            schema = s;
        } else if (schema != s) {
            throw new Exception("schema already set");
        }

        return schema.openLayout();
    }

    public static void openDockSystemConf(String fileName) {
        // ServiceDocktailor.getInstance().setLastUIConfigUsed(fileName);
        log.info("Docktailor : Ouverture de la configuration d'interface, {}", ServiceDocktailor.getInstance().getLastUIConfigUsed());
        GlobalSettings.setFileProvider(fileName);
        ASettingsStore store = GlobalSettings.getASettingsStore();
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

    /**
     * Permet de fermer un FxSettingsSchéma.
     */
    public static void closeLayout(FxSettingsSchema s) {
        s.closeLayout();
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


    public static void store(Node n) {
        if (n != null) {
            schema.storeNode(n);
            //schema.save();
        }
    }


    public static void restore(Node n) {
        if (n != null) {
            schema.restoreNode(n);
        }
    }


    public static void store(Window w) {
        schema.storeWindow(w);
        //schema.save();
    }


    public static void restore(Window w) {
        if (schema != null) {
            schema.restoreWindow(w);
        }
    }


    public static void save() {
        schema.save();
    }


    public static void exit() {
        storeLayout();
        WindowMonitor.exit();
    }
}
