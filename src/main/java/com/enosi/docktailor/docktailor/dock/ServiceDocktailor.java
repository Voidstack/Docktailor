package com.enosi.docktailor.docktailor.dock;

import com.enosi.docktailor.docktailor.fx.FxMenu;
import com.enosi.docktailor.docktailor.fx.FxMenuBar;
import com.enosi.docktailor.docktailor.fxdock.FxDockWindow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Référence les différents IDraggableTab dans une liste via reflexion. Le IDraggableTab doit obligatoirement être dans
 * un sous package à celui dans lequel celle classe est stocké.
 */
@Getter
@Singleton
@Slf4j
public class ServiceDocktailor {
    public static boolean IS_DEBUG = false;

    private static ServiceDocktailor instance;

    private final Set<Class<? extends IControllerDockPane>> draggableTabs;

    // Util pour les IDockableCondition, certaine fenêtre doivent exister que sous certaines conditions.
    private final Set<Class<? extends IControllerDockPane>> filteredDraggableTabs = new HashSet<>();

    private ServiceDocktailor() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.SubTypes) // false = pas inclure Object.class
        );

        // Récupère les IDockable.class sans les abstract (uniquement les implémentations concrètes)
        draggableTabs = reflections.getSubTypesOf(IControllerDockPane.class)
                .stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .collect(Collectors.toSet());

        filteredDraggableTabs.addAll(draggableTabs);

        draggableTabs.forEach(clazz -> {
            if (IControllerDockCondition.class.isAssignableFrom(clazz)) {
                try {
                    if (!((IControllerDockCondition) clazz.getDeclaredConstructor().newInstance()).isExistDansLeContext()) {
                        filteredDraggableTabs.remove(clazz);
                    }
                } catch (Exception e) {
                    log.error("Impossible de ségréger sur la condition d'existance de la fenêtre : {}", clazz.getName(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static ServiceDocktailor getInstance() {
        if (instance == null) {
            instance = new ServiceDocktailor();
        }
        return instance;
    }

    public List<IControllerDockPane> getNewInstances() {
        List<IControllerDockPane> values = new ArrayList<>();
        for (Class<? extends IControllerDockPane> draggableTab : getDraggableTabs()) {
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
        for (Class<? extends IControllerDockPane> draggableTab : ServiceDocktailor.getInstance().getFilteredDraggableTabs()) {
            try {
                IControllerDockPane instanceDraggable = draggableTab.getDeclaredConstructor().newInstance();
//                String checkItemName = instanceDraggable.getTabName();

                fxMenu.item(instanceDraggable, () -> fxDockWindow.addDockPane(instanceDraggable.createDockPane()));
            } catch (Exception e) {
                throw new RuntimeException("Impossible de récupérer l'instance du DraggableTab, " + e);
            }
        }
    }
}
