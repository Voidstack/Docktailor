package com.enosi.docktailor.docktailor.dock;

import com.enosi.docktailor.docktailor.fx.FxMenu;
import com.enosi.docktailor.docktailor.fx.FxMenuBar;
import com.enosi.docktailor.docktailor.fxdock.FxDockWindow;
import com.enosi.docktailor.utils.Lazy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.inject.Singleton;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Référence les différents IDraggableTab dans une liste via reflexion.
 */
@Singleton
@Slf4j
public class ServiceDocktailor {
    @Setter @Getter
    private String docktailorFolder = "default";
    private static final String DOCKTAILOR_CONFIG_FILE = "docktailor.config";
    public static boolean IS_DEBUG = false;
    private static ServiceDocktailor instance;
    @Getter
    private final ConfigDocktailor configDocktailor = new ConfigDocktailor(String.join(File.separator, docktailorFolder, DOCKTAILOR_CONFIG_FILE));
    private Set<Class<? extends IControllerDockPane>> draggableTabs;

    @Getter
    private final Lazy<Set<Class<? extends IControllerDockPane>>> filteredDraggableTabsLazy = new Lazy<>(this::loadFilteredDraggableTabs);

    private Set<Class<? extends IControllerDockPane>> loadFilteredDraggableTabs() {
        // setup(referenceClass);
        return setupFilteredDraggableTabs(this.draggableTabs);
    }

    private Class<?> referenceClass = null;

    private ServiceDocktailor() {}

    public static ServiceDocktailor getInstance() {
        if (instance == null) {
            instance = new ServiceDocktailor();
        }
        return instance;
    }

    /**
     * Scan les classes qui implémentent IControllerDockPane dans le projet en cours.
     * @param referenceClass Main.class ou autre class de VOTRE projet, sert de référence pour scanner le code du projet et pas l'intégralité du projet.
     *
     * Essai de récupérer les classes depuis la config sinon rescan le projet complet puis save la config.
     */
    public void setup(Class<?> referenceClass) {
        this.referenceClass = referenceClass;
        this.draggableTabs = configDocktailor.tryLoad().orElseGet(() -> {
            Set<Class<? extends IControllerDockPane>> scannedClasses = scanProjectWithReflection(referenceClass);
            configDocktailor.getDataConfigDocktailor().setIControllerDockPane(scannedClasses);
            configDocktailor.save();
            return configDocktailor.getDataConfigDocktailor();
        }).getIControllerDockPane();
        setupFilteredDraggableTabs(this.draggableTabs);
    }

    /**
     * Dans le cas ou la config est corrupted ou incomplète, force le rescan de toutes les classes.
     */
    public void forceRescan() {
        this.draggableTabs = scanProjectWithReflection(referenceClass);
        configDocktailor.getDataConfigDocktailor().setIControllerDockPane(draggableTabs);
        configDocktailor.save();
    }

    public void setLastUIConfigUsed(String file){
        configDocktailor.getDataConfigDocktailor().setLastUIConfigUsed(file);
    }

    public String getLastUIConfigUsed(){
        return configDocktailor.getDataConfigDocktailor().getLastUIConfigUsed();
    }

    /**
     * Scan les classes qui implémentent IControllerDockPane dans le projet en cours.
     * @param referenceClass Main.class ou autre class de VOTRE projet, sert de référence pour scanner le code du projet et pas l'intégralité du projet.
     * @return Set<Class<? extends IControllerDockPane>>
     */
    private Set<Class<? extends IControllerDockPane>> scanProjectWithReflection(Class<?> referenceClass) {
        ConfigurationBuilder configurationBuilder;

        if (referenceClass == null) {
            configurationBuilder = new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forJavaClassPath())
                    .setScanners(Scanners.SubTypes); // false = pas inclure Object.class
        } else {
            URL projectClassesUrl = referenceClass.getProtectionDomain().getCodeSource().getLocation();

            configurationBuilder = new ConfigurationBuilder()
                    .setUrls(projectClassesUrl) // uniquement le code appelant
                    .setScanners(Scanners.SubTypes);
        }

        Reflections reflections = new Reflections(configurationBuilder);

        return reflections.getSubTypesOf(IControllerDockPane.class)
                .stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .collect(Collectors.toSet());
    }

    /**
     * Permet de setup les IDraggableTab qui doivent être filtrés par rapport à certaines conditions.
     */
    private Set<Class<? extends IControllerDockPane>> setupFilteredDraggableTabs( Set<Class<? extends IControllerDockPane>> draggableTabs) {
        Set<Class<? extends IControllerDockPane>> filteredDraggableTabs = new HashSet<>(draggableTabs);

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

        return filteredDraggableTabs;
    }

    public List<IControllerDockPane> getNewInstances() {
        List<IControllerDockPane> values = new ArrayList<>();
        for (Class<? extends IControllerDockPane> draggableTab : this.draggableTabs) {
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
        for (Class<? extends IControllerDockPane> draggableTab : ServiceDocktailor.getInstance().getFilteredDraggableTabsLazy().get()) {
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
