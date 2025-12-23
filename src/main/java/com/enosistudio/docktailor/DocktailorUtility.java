package com.enosistudio.docktailor;

import com.enosistudio.RFile;
import com.enosistudio.docktailor.fx.FxSettingsSchema;
import com.enosistudio.docktailor.fx.WindowMonitor;
import com.enosistudio.docktailor.fx.fxdock.FxDockSchema;
import com.enosistudio.docktailor.generated.R;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for managing Docktailor's layout and window configurations.
 * This class provides methods to open, close, store, and restore layouts,
 * as well as create default windows and handle application exit.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocktailorUtility {

    /**
     * The current FxSettingsSchema being used.
     */
    @Getter
    private static FxSettingsSchema schema;

    /**
     * Opens a new dock system configuration using the provided FxDockSchema.
     * If a layout is already open, it closes the current layout before opening the new one.
     *
     * @param fxDockSchema The FxDockSchema to be used for the new layout.
     */
    public static void openDockSystemConf(FxDockSchema fxDockSchema) {
        log.info("Docktailor: Opening UI configuration: {}", DocktailorService.getInstance().getLastUIConfigUsed());

        if (schema != null) {
            DocktailorUtility.closeCurrentLayout();
        }

        schema = fxDockSchema;
        schema.openLayout();
    }

    /**
     * Closes the current FxSettingsSchema layout.
     * This method is private and is used internally to ensure proper cleanup.
     */
    private static void closeCurrentLayout() {
        // Close current layout
        schema.closeLayout();
    }

    /**
     * Creates and returns a default JavaFX Stage using the current FxSettingsSchema.
     *
     * @return A new JavaFX Stage.
     */
    public static Stage createDefaultWindow() {
        return schema.createDefaultWindow();
    }

    /**
     * Stores the current layout configuration.
     */
    public static void storeLayout() {
        schema.storeLayout();
    }

    /**
     * Stores the current layout configuration to a specific file.
     *
     * @param fileName The name of the file where the layout will be stored.
     */
    public static void storeLayout(String fileName) {
        schema.storeLayout(fileName);
    }

    /**
     * Stores the configuration of the specified JavaFX Window.
     *
     * @param w The JavaFX Window to be stored.
     */
    public static void store(Window w) {
        schema.storeWindow(w);
    }

    /**
     * Restores the configuration of the specified JavaFX Window.
     * If no schema is currently set, this method does nothing.
     *
     * @param w The JavaFX Window to be restored.
     */
    public static void restore(Window w) {
        if (schema != null) {
            schema.restoreWindow(w);
        }
    }

    /**
     * Exits the application, ensuring the current layout is stored before exiting.
     */
    public static void exit() {
        storeLayout();
        WindowMonitor.exit();
    }

    /**
     * Retrieves the CSS file used by Docktailor.
     *
     * @return The CSS file as an RFile object.
     */
    public static RFile getDocktailorCss() {
        return R.com.enosistudio.docktailor.css.mainCss;
    }
}