package com.enosistudio.docktailor;

import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.common.Singleton;
import com.enosistudio.docktailor.fx.FxMenuItem;
import com.enosistudio.docktailor.fx.FxSettingsSchema;
import com.enosistudio.docktailor.fx.WindowMonitor;
import com.enosistudio.docktailor.fx.fxdock.FxDockSchema;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import com.enosistudio.docktailor.fx.fxdock.internal.ConfigDocktailor;
import com.enosistudio.docktailor.fx.fxdock.internal.IDockPane;
import com.enosistudio.docktailor.generated.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton service class for managing Docktailor's draggable tabs and configurations.
 * This class provides methods to handle configuration files, manage draggable tabs,
 * and create menu items for the Docktailor application.
 */
@Singleton
@Slf4j
public class DocktailorService {
    /**
     * The current FxSettingsSchema being used.
     */
    @Getter
    private static FxSettingsSchema schema;

    /**
     * The folder where Docktailor's configuration files are saved.
     */
    @Setter @Getter
    private static String docktailorSaveFolder = Path.of(System.getenv("APPDATA"), "enosistudio", "docktailor").toString();

    /**
     * The default configuration file for Docktailor.
     * This file is used if the user's configuration file does not exist.
     */
    @Setter @Getter
    private static String defaultUiFile = R.com.enosistudio.docktailor.docktailorDefaultUi.getURL().getFile();

    /**
     * The name of the configuration file that stores the last used configuration.
     */
    private static final String DOCKTAILOR_CONFIG_FILE = "docktailor.conf";

    @Getter
    private final GlobalSettings globalSettings = new GlobalSettings();

    /**
     * Flag indicating whether the application is in debug mode.
     */
    public static boolean isDebug = false;

    /**
     * Singleton instance of the DocktailorService.
     */
    private static DocktailorService instance;

    /**
     * Configuration object for managing Docktailor's settings.
     */
    @Getter
    private final ConfigDocktailor configDocktailor = new ConfigDocktailor(String.join(File.separator, docktailorSaveFolder, DOCKTAILOR_CONFIG_FILE));

    /**
     * List of draggable tab classes managed by this service.
     */
    @Delegate
    private final ObservableList<Class<? extends IDockPane>> draggableTabs = FXCollections.observableArrayList();

    @Getter
    private final Map<String, String> predefinedConfigFiles = new HashMap<>();

    public DocktailorService(){
        predefinedConfigFiles.put("Configuration #1", Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_1.ui").toString());
        predefinedConfigFiles.put("Configuration #2", Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_2.ui").toString());
        predefinedConfigFiles.put("Configuration #3", Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_3.ui").toString());
    }

    /**
     * Retrieves the singleton instance of DocktailorService.
     *
     * @return The singleton instance of DocktailorService.
     */
    public static DocktailorService getInstance() {
        if (instance == null) {
            instance = new DocktailorService();
        }
        return instance;
    }

    /**
     * Opens a new dock system configuration using the provided FxDockSchema.
     * If a layout is already open, it closes the current layout before opening the new one.
     *
     * @param fxDockSchema The FxDockSchema to be used for the new layout.
     */
    public static void openDockSystemConf(FxDockSchema fxDockSchema) {
        log.info("Docktailor: Opening UI configuration: {}", DocktailorService.getInstance().getLastUIConfigUsed());

        if (schema != null) {
            schema.closeLayout();
        }

        schema = fxDockSchema;
        schema.openLayout();
    }

    /**
     * Exits the application, ensuring the current layout is stored before exiting.
     */
    public static void exit() {
        schema.storeLayout();
        WindowMonitor.exit();
    }

    /**
     * Sets the name of the last used UI configuration file.
     *
     * @param file The name of the configuration file.
     */
    public void setLastUIConfigUsed(String file) {
        configDocktailor.getDataConfigDocktailor().setLastUIConfigUsed(file);
    }

    /**
     * Retrieves the name of the last used UI configuration file.
     *
     * @return The name of the last used configuration file.
     */
    public String getLastUIConfigUsed() {
        return configDocktailor.getDataConfigDocktailor().getLastUIConfigUsed();
    }

    /**
     * Creates new instances of all draggable tabs managed by this service.
     *
     * @return A list of new instances of draggable tabs.
     */
    public List<IDockPane> getNewInstances() {
        List<IDockPane> values = new ArrayList<>();
        for (Class<? extends IDockPane> draggableTab : this.draggableTabs) {
            try {
                values.add(draggableTab.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                log.error("ServiceDocktailor : ", e);
            }
        }
        return values;
    }

    /**
     * Creates menu items for all draggable tabs managed by this service.
     *
     * @param window The FxDockWindow to which the menu items will be linked.
     * @return A list of menu items for the draggable tabs.
     */
    public List<MenuItem> createMenuItems(FxDockWindow window) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (Class<? extends IDockPane> draggableTab : draggableTabs) {
            try {
                IDockPane instanceDraggable = draggableTab.getDeclaredConstructor().newInstance();
                FxMenuItem m = new FxMenuItem(instanceDraggable.getTabName(), () -> window.addDockPane(instanceDraggable.createDockPane()));
                m.setGraphic(instanceDraggable.getTabIcon());
                menuItems.add(m);
            } catch (Exception e) {
                log.error("ServiceDocktailor : ", e);
            }
        }
        return menuItems;
    }

}