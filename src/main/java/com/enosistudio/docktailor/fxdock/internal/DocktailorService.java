package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.common.Singleton;
import com.enosistudio.docktailor.fx.FxMenuItem;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
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
import java.util.List;

/**
 * Singleton service class for managing Docktailor's draggable tabs and configurations.
 * This class provides methods to handle configuration files, manage draggable tabs,
 * and create menu items for the Docktailor application.
 */
@Singleton
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocktailorService {

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
    private static String defaultUiFile = DocktailorService.class.getResource("/com/enosistudio/docktailor/docktailor_default.ui").getFile();

    /**
     * The name of the configuration file that stores the last used configuration.
     */
    private static final String DOCKTAILOR_CONFIG_FILE = "docktailor.conf";

    /**
     * Flag indicating whether the application is in debug mode.
     */
    public static boolean IS_DEBUG = false;

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
                menuItems.add(m);
            } catch (Exception e) {
                log.error("ServiceDocktailor : ", e);
            }
        }
        return menuItems;
    }

}