package com.enosi.docktailor.dock;

import lombok.extern.slf4j.Slf4j;

import javafx.geometry.Side;
import lombok.Getter;
import org.reflections.Reflections;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Référence les différents IDraggableTab dans une liste via reflexion.
 * Le IDraggableTab doit obligatoirement être dans un sous package à celui dans lequel celle classe
 * est stocké.
 */
@Getter
@Singleton
@Slf4j
public class ServiceDockableTab {

    private final Map<Side, TabPaneDraggable> tabPanes = new HashMap<>();

    private static ServiceDockableTab instance;

    private final Set<Class<? extends IControllerDockablePane>> draggableTabs;

    // Util pour les IDockableCondition, certaine fenêtre doivent exister que sous certaines conditions.
    private final Set<Class<? extends IControllerDockablePane>> filteredDraggableTabs = new HashSet<>();

    private ServiceDockableTab() {
        Package myPackage = ServiceDockableTab.class.getPackage();
        Reflections reflections = new Reflections(myPackage.getName());

        // Récupère les IDockable.class sans les abstract (uniquement les implémentations concrètes)
        draggableTabs = reflections.getSubTypesOf(IControllerDockablePane.class).stream().filter(c -> !Modifier.isAbstract(c.getModifiers())).collect(Collectors.toSet());

        filteredDraggableTabs.addAll(draggableTabs);
        draggableTabs.forEach(clazz -> {
            if (IDockableCondition.class.isAssignableFrom(clazz)) {
                try {
                    if (!((IDockableCondition) clazz.getDeclaredConstructor().newInstance()).isExistDansLeContext()) {
                        filteredDraggableTabs.remove(clazz);
                    }
                } catch (Exception e) {
                    log.error("Impossible de ségréger sur la condition d'existance de la fenêtre : " + clazz.getName(), e);
                    throw new RuntimeException(e);
                }
            }
        });

        // draggableTabs = reflections.getSubTypesOf(IDockablePane.class);
    }

    public static ServiceDockableTab getInstance() {
        if (instance == null) {
            instance = new ServiceDockableTab();
        }
        return instance;
    }

    public List<IControllerDockablePane<?>> getNewInstances() {
        List<IControllerDockablePane<?>> values = new ArrayList<>();
        for (Class<? extends IControllerDockablePane> draggableTab : getDraggableTabs()) {
            try {
                values.add(draggableTab.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return values;
    }

    /**
     * Platform.runLater obligatoire.
     *
     * @param fxMenuBar : FxMenuBar
     */
    public void initFXMenuBar(FxMenuBar fxMenuBar, FxDockWindow fxDockWindow) {
        FxMenu fxMenu = fxMenuBar.menu("Affichages");

        // Je récupère la liste de tout le DraggablePane Possible.
        for (Class<? extends IControllerDockablePane> draggableTab : ServiceDockableTab.getInstance().getFilteredDraggableTabs()) {
            try {
                IControllerDockablePane instanceDraggable = draggableTab.getDeclaredConstructor().newInstance();
//                String checkItemName = instanceDraggable.getTabName();

                fxMenu.item(instanceDraggable, () -> fxDockWindow.addDockPane(instanceDraggable.createDockPane()));
            } catch (Exception e) {
                throw new RuntimeException("Impossible de récupérer l'instance du DraggableTab, " + e);
            }
        }
    }
}
