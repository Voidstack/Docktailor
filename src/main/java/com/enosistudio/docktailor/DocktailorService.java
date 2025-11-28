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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton service class for managing Docktailor's draggable tabs and configurations.
 * This class serves as the main entry point for the Docktailor framework and provides
 * management of draggable tab types registry, configuration file handling and persistence,
 * layout schema management, menu item creation for registered tabs, and global settings access.
 *
 * @see FxDockSchema
 * @see GlobalSettings
 * @see IDockPane
 */
@Singleton
@Slf4j
public class DocktailorService {
    /**
     * The current FxSettingsSchema being used for managing layouts.
     * This schema handles the serialization and deserialization of dock layouts.
     */
    @Getter
    private static FxSettingsSchema schema;

    /**
     * The folder where Docktailor's configuration files are saved.
     * Defaults to APPDATA/enosistudio/docktailor on Windows.
     * Can be changed using setSaveFolder method.
     */
    private final String docktailorDefaultSaveFolder = Path.of(System.getenv("APPDATA"), "enosistudio", "docktailor").toString();

    /**
     * The default configuration file for Docktailor UI layout.
     * This file is used as a fallback if the user's configuration file does not exist.
     * Can be changed using setDefaultUiFile method.
     */
    @Getter
    private String defaultUiFile = R.com.enosistudio.docktailor.docktailorDefaultUi.getURL().getFile();


    /**
     * The name of the configuration file that stores the last used configuration path.
     */
    @Getter
    private String configFile = Path.of(docktailorDefaultSaveFolder, "docktailor.conf").toString();

    /**
     * Global settings instance for application-wide configuration.
     * Provides file-based persistence for settings across application restarts.
     */
    @Getter
    private final GlobalSettings globalSettings = new GlobalSettings();

    /**
     * Flag indicating whether the application is in debug mode.
     * When true, additional logging and debug information may be displayed.
     */
    public static boolean isDebug = false;

    /**
     * Singleton instance of the DocktailorService.
     */
    private static DocktailorService instance;

    /**
     * Configuration object for managing Docktailor's persistent settings.
     * Stores the last used UI configuration file path.
     */
    @Getter
    private ConfigDocktailor configDocktailor = new ConfigDocktailor(configFile);

    /**
     * Observable list of registered draggable tab classes managed by this service.
     * These classes must implement IDockPane and have a no-arg constructor.
     * Used to dynamically create tab instances and menu items.
     */
    @Getter
    private final ObservableList<Class<? extends IDockPane>> draggableTabs = FXCollections.observableArrayList();

    /**
     * Map of predefined UI configuration files.
     * Keys are display names (e.g., "Configuration #1"), values are file paths.
     * Can be customized using setPredefinedUiFiles method.
     */
    @Getter
    private final Map<String, String> predefinedUiFiles = new HashMap<>();

    /**
     * Private constructor initializing the service with default predefined UI configurations.
     * Creates three default configuration file paths in the save folder.
     */
    public DocktailorService() {
        predefinedUiFiles.put("Configuration #1", Path.of(docktailorDefaultSaveFolder, "docktailor_1.ui").toString());
        predefinedUiFiles.put("Configuration #2", Path.of(docktailorDefaultSaveFolder, "docktailor_2.ui").toString());
        predefinedUiFiles.put("Configuration #3", Path.of(docktailorDefaultSaveFolder, "docktailor_3.ui").toString());
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
     * Creates new instances of all registered draggable tabs using reflection.
     * Each tab class must have a no-argument constructor.
     * Failed instantiations are logged but do not stop the process.
     *
     * @return A list of new instances of draggable tabs, excluding any that failed to instantiate.
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
     * Creates menu items for all registered draggable tabs.
     * Each menu item is linked to the specified window and will add a new instance
     * of the corresponding tab when clicked. The menu items include the tab's icon
     * and name as defined by the IDockPane implementation.
     *
     * @param window The FxDockWindow to which the tabs will be added when menu items are clicked.
     * @return A list of menu items for the draggable tabs, excluding any that failed to instantiate.
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

    /**
     * Sets the default UI configuration file to be used as fallback.
     * This method supports fluent configuration.
     *
     * @param defaultUiFile The path to the default UI file.
     * @return This DocktailorService instance for method chaining.
     */
    public DocktailorService setDefaultUiFile(String defaultUiFile) {
        this.defaultUiFile = defaultUiFile;
        return this;
    }

    public DocktailorService setConfigFile(String configFile) {
        this.configFile = configFile;
        this.configDocktailor = new ConfigDocktailor(configFile);
        return this;
    }

    /**
     * Sets the draggable tab classes that will be managed by this service.
     * Each class must implement IDockPane and have a no-argument constructor.
     * This method supports fluent configuration.
     *
     * @param draggableTabs The classes of draggable tabs to register.
     * @return This DocktailorService instance for method chaining.
     */
    @SafeVarargs
    public final DocktailorService setDraggableTab(Class<? extends IDockPane>... draggableTabs) {
        this.draggableTabs.setAll(draggableTabs);
        return this;
    }

    /**
     * Sets the predefined UI configuration files.
     * Clears any existing predefined files and replaces them with the provided map.
     * This method supports fluent configuration.
     *
     * @param predefinedConfigFiles A map where keys are display names and values are file paths.
     * @return This DocktailorService instance for method chaining.
     */
    public DocktailorService setPredefinedUiFiles(Map<String, String> predefinedConfigFiles) {
        this.predefinedUiFiles.clear();
        this.predefinedUiFiles.putAll(predefinedConfigFiles);
        return this;
    }

    /**
     * Initializes the global settings with the last used UI configuration file.
     * This method should be called after all configuration has been set up.
     *
     * @return The initialized GlobalSettings instance.
     */
    public GlobalSettings init() {
        this.getGlobalSettings().setFileProvider(this.getLastUIConfigUsed());
        return this.getGlobalSettings();
    }
}