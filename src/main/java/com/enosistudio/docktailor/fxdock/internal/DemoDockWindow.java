package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.common.Hex;
import com.enosistudio.docktailor.fx.*;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import com.enosistudio.docktailor.sample.mvc.MainApp;
import com.enosistudio.docktailor.sample.mvc.controller.PersonDockPane;
import com.enosistudio.docktailor.sample.mvc.controller.TestDockPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import net.yetihafen.javafx.customcaption.CaptionConfiguration;
import net.yetihafen.javafx.customcaption.CustomCaption;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Demo Window.
 */
@Slf4j
public class DemoDockWindow extends FxDockWindow {
    public final FxAction windowCheckAction = new FxAction();
    //private static int seq;

    public DemoDockWindow() {
        super("DemoWindow");

        getIcons().add(MainApp.IMAGE);

        // Creation de la barre supérieur.
        FxMenuBar fxMenuBar = createMenu();

        // largeur de la MenuBar = largeur de la fenêtre - 50
        getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            fxMenuBar.setMaxWidth(newVal.doubleValue() - 138);
        });

        setTop(fxMenuBar);

        setTitle(MainApp.TITLE);

        LocalSettings.get(this).add("CHECKBOX_MENU", windowCheckAction);

        Platform.runLater(() -> CustomCaption.useForStage(this, new CaptionConfiguration()
                .setCaptionDragRegion(fxMenuBar)
                .setControlBackgroundColor(Color.rgb(60, 63, 65 ))
        ));
    }

    protected static void c(StringBuilder sb) {
        int min = 100;
        int v = min + new Random().nextInt(255 - min);
        sb.append(Hex.toHexByte(v));
    }

    private static void loadDefaultAction() {
        log.info("Docktailor : Load default interface configuration");
        actionLoadSettings(GlobalSettings.getDEFAULT_FILE());
    }

    protected static void actionLoadSettings(String fileName) {
        log.info("Docktailor : Load default interface configuration : {}", fileName);
        FxFramework.openDockSystemConf(fileName);
        ServiceDocktailor.getInstance().setLastUIConfigUsed(fileName);
        ServiceDocktailor.getInstance().getConfigDocktailor().save();

        //AppConfigManager.getInstance().saveProperty(AppConfigManager.LAST_UI_CONFIG_SAVED, fileName);
    }

    /**
     * Use GlobalSettings.FILE...
     *
     * @param fileName :
     */
    protected static void actionSaveSettings(String fileName) {
        log.info("Docktailor : Save current interface configuration in {}", fileName);
        FxFramework.storeLayout(fileName);
        ServiceDocktailor.getInstance().setLastUIConfigUsed(fileName);
        ServiceDocktailor.getInstance().getConfigDocktailor().save();

        //AppConfigManager.getInstance().saveProperty(AppConfigManager.LAST_UI_CONFIG_SAVED, fileName);
    }

    protected FxMenuBar createMenu() {
        FxMenuBar fxMenuBar = new FxMenuBar();

        Menu menuApplication = new Menu("Application");

        fxMenuBar.add(menuApplication);

        // Ajout des différentes vue de tacp
        Platform.runLater(() -> {

            // Custom config
            menuApplication.getItems().add(addCustomConfiguration("Configuration #1", GlobalSettings.getFILE_1()));
            menuApplication.getItems().add(addCustomConfiguration("Configuration #2", GlobalSettings.getFILE_2()));
            menuApplication.getItems().add(addCustomConfiguration("Configuration #3", GlobalSettings.getFILE_3()));

            menuApplication.getItems().add(new SeparatorMenuItem());

            MenuItem menuItemDefaultConf = new MenuItem("Charger la configuration par défaut");
            menuItemDefaultConf.setOnAction(e -> DemoDockWindow.loadDefaultAction());
            menuApplication.getItems().add(menuItemDefaultConf);

            MenuItem menuLeaveApp = new MenuItem("Quitter l'application");
            menuLeaveApp.setOnAction(e -> FxFramework.exit());
            menuApplication.getItems().add(menuLeaveApp);
        });


        Menu menuWindows = new Menu("Windows");
        ServiceDocktailor.getInstance().setAll(PersonDockPane.class, TestDockPane.class);
        menuWindows.getItems().addAll(ServiceDocktailor.getInstance().createMenuItems(this));
        fxMenuBar.add(menuWindows);


        return fxMenuBar;
    }

    /**
     * Permet d'ajouter une Node pour un fxMenuBar.
     *
     * @param strLabel : Nom affiché
     * @param fileName : Nom du fichier
     * @return CustomMenuItem
     */
    private CustomMenuItem addCustomConfiguration(String strLabel, String fileName) {
        HBox hbox = new HBox();
        CustomMenuItem menuCustomSave1 = new CustomMenuItem(hbox);
        menuCustomSave1.setHideOnClick(false);

        hbox.setSpacing(3);
        hbox.setPadding(Insets.EMPTY);
        hbox.setAlignment(Pos.CENTER);
        Label label = new Label(strLabel);

        Region separator = new Region();
        HBox.setHgrow(separator, Priority.ALWAYS);

        hbox.getChildren().addAll(label, separator);

        Button btnSave = new Button("Sauvegarder");
        btnSave.setOnAction(event -> actionSaveSettings(fileName));
        hbox.getChildren().add(btnSave);

        if (Files.exists(Path.of(fileName))) {
            Button btnLoad = new Button("Charger");
            btnLoad.setOnAction(event -> actionLoadSettings(fileName));
            hbox.getChildren().add(btnLoad);
        }

        return menuCustomSave1;
    }
}