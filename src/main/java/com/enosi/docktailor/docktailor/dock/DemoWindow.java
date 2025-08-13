package com.enosi.docktailor.docktailor.dock;

import com.enosi.docktailor.common.util.GlobalSettings;
import com.enosi.docktailor.common.util.Hex;
import com.enosi.docktailor.common.util.SB;
import com.enosi.docktailor.docktailor.fx.*;
import com.enosi.docktailor.docktailor.fx.settings.LocalSettings;
import com.enosi.docktailor.docktailor.fxdock.FxDockWindow;
import com.enosi.docktailor.sample.mvc.MainApp;
import com.enosi.docktailor.utils.R;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Demo Window.
 */
@Slf4j
public class DemoWindow extends FxDockWindow {

    private static final GlobalBooleanProperty showCloseDialogProperty = new GlobalBooleanProperty("show.close.dialog", true);
    public final FxAction windowCheckAction = new FxAction();
    public final Label statusField = new Label();
    //private static int seq;


    public DemoWindow() {
        super("DemoWindow");

        String cssFile = R.loadStringFromFile("/css/main.css");
        getScene().getStylesheets().add(cssFile);

        getIcons().add(MainApp.IMAGE);

        // Creation de la barre supérieur.
        setTop(createMenu());

        setTitle(MainApp.TITLE);

        LocalSettings.get(this).add("CHECKBOX_MENU", windowCheckAction);
    }

    protected static void c(SB sb) {
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

    protected static Object addButton(Dialog<?> d, String text, ButtonBar.ButtonData type) {
        ButtonType b = new ButtonType(text, type);
        d.getDialogPane().getButtonTypes().add(b);
        return b;
    }

    /**
     * Méthode qui permet de mettre la window en fullscreen.
     */
    private void onBtnFullScreen() {
        this.setFullScreen(!this.isFullScreen());
    }

    /**
     * Interogation pour la sauvegarde lors de la fermeture de la fenêtre.
     */
    private void initClosingWindowOperation() {
        // this method illustrates how to handle closing window request
        setClosingWindowOperation((exiting, multiple, choice) ->
        {
            if (!showCloseDialogProperty.get()) {
                return ShutdownChoice.CONTINUE;
            }

            switch (choice) {
                case CANCEL: // won't happen
                case CONTINUE: // won't happen
                case DISCARD_ALL: // should not call
                    return ShutdownChoice.DISCARD_ALL;
                case SAVE_ALL:
                    save();
                    return ShutdownChoice.SAVE_ALL;
            }

            toFront();

            // FIX switch to FxDialog
            Dialog<?> d = new Dialog<>();
            d.initOwner(this);
            d.setTitle("Save Changes?");
            d.setContentText("This is an example of a dialog shown when closing a window.");

            Object save = addButton(d, "Save", ButtonBar.ButtonData.OTHER);
            Object saveAll = null;
            if (multiple) {
                saveAll = addButton(d, "Save All", ButtonBar.ButtonData.OTHER);
            }
            addButton(d, "Discard", ButtonBar.ButtonData.OTHER);
            Object discardAll = null;
            if (multiple) {
                discardAll = addButton(d, "Discard All", ButtonBar.ButtonData.OTHER);
            }
            Object cancel = addButton(d, "Cancel", ButtonBar.ButtonData.APPLY);

            d.showAndWait();
            Object rv = d.getResult();

            if (rv == cancel) {
                return ShutdownChoice.CANCEL;
            } else if (rv == save) {
                FxFramework.storeLayout();
            } else if (rv == saveAll) {
                FxFramework.storeLayout();
                return ShutdownChoice.SAVE_ALL;
            } else if (rv == discardAll) {
                return ShutdownChoice.DISCARD_ALL;
            }
            return ShutdownChoice.CONTINUE;
        });
    }

    protected FxMenuBar createMenu() {
        FxMenuBar fxMenuBar = new FxMenuBar();

        // Ajout des différentes vue de tacp
        Platform.runLater(() -> {
            ServiceDocktailor.getInstance().initFXMenuBar(fxMenuBar, this);
            fxMenuBar.separator();

            // Custom config
            fxMenuBar.item(addCustomConfiguration("Configuration #1", GlobalSettings.getFILE_1()));
            fxMenuBar.item(addCustomConfiguration("Configuration #2", GlobalSettings.getFILE_2()));
            fxMenuBar.item(addCustomConfiguration("Configuration #3", GlobalSettings.getFILE_3()));

            fxMenuBar.separator();
            fxMenuBar.item("Charger la configuration par défaut", new FxAction(DemoWindow::loadDefaultAction));
        });

        // file
        fxMenuBar.menu("Application");
        // m.item("Save Settings", saveSettingsAction);
        // m.separator();
        fxMenuBar.item("Quitter l'application", FxFramework::exit);
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
        Separator separator = new Separator();
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

    protected Node createStatusBar() {
        BorderPane p = new BorderPane();
        p.setLeft(statusField);
        // p.setRight(FX.label(MainApp.COPYRIGHT, new Insets(1, 20, 1, 10)));
        return p;
    }

    public void save() {
        // indicates saving the changes
        //D.print("save");
    }
}