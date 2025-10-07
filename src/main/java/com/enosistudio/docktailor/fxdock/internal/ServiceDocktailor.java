package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.RFile;
import com.enosistudio.docktailor.common.Singleton;
import com.enosistudio.docktailor.fx.FxMenuItem;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import com.enosistudio.generated.R;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Référence les différents IDraggableTab dans une liste via reflexion.
 */
@Singleton
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceDocktailor {
    @Setter @Getter
    private String docktailorFolder = "default";
    private static final String DOCKTAILOR_CONFIG_FILE = "docktailor.config";
    public static boolean IS_DEBUG = false;
    private static ServiceDocktailor instance;
    @Getter
    private final ConfigDocktailor configDocktailor = new ConfigDocktailor(String.join(File.separator, docktailorFolder, DOCKTAILOR_CONFIG_FILE));

    @Delegate
    private final ObservableList<Class<? extends IDockPane>> draggableTabs = FXCollections.observableArrayList();

    public static ServiceDocktailor getInstance() {
        if (instance == null) {
            instance = new ServiceDocktailor();
        }
        return instance;
    }

    public void setLastUIConfigUsed(String file){
        configDocktailor.getDataConfigDocktailor().setLastUIConfigUsed(file);
    }

    public String getLastUIConfigUsed(){
        return configDocktailor.getDataConfigDocktailor().getLastUIConfigUsed();
    }


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
     * Méthodes utilitaires pour récupérer les MenuItems de l'ensemble des dockPane référencé dans l'instance.
     * @param window : FxDockWindow
     * @return : List<MenuItem>
     */
    public List<MenuItem> createMenuItems(FxDockWindow window){
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

    /**
     * Permet de récupérer le CSS de docktailor.
     *
     * @return : String
     */
    public static RFile getDocktailorCss(){
        return R.com.enosistudio.docktailor.css.mainCss;
    }
}