package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.DocktailorUtility;
import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.FxTooltipDebugCss;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.fx.FxAction;
import com.enosistudio.docktailor.fx.FxMenuBar;
import com.enosistudio.docktailor.fx.LocalSettings;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import com.enosistudio.docktailor.sample.controller.PersonDockPane;
import com.enosistudio.docktailor.sample.controller.TestDockPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.yetihafen.javafx.customcaption.CaptionConfiguration;
import net.yetihafen.javafx.customcaption.CustomCaption;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo Window.
 */
@Slf4j
public class DemoDockWindow extends FxDockWindow {
    @Getter
    private static final String FILE_1 = Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_1.ui").toString();
    @Getter
    private static final String FILE_2 = Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_2.ui").toString();
    @Getter
    private static final String FILE_3 = Path.of(DocktailorService.getDocktailorSaveFolder(), "docktailor_3.ui").toString();

    public final FxAction windowCheckAction = new FxAction();
    //private static int seq;

    private static final List<DemoDockWindow> demoDockWindows = new ArrayList<>();

    private CustomMenuBar customMenuBar = null;

    public DemoDockWindow() {
        super("DemoWindow");
        demoDockWindows.add(this);

        getIcons().add(MainApp.IMAGE);

        // Creation de la barre supérieur.
        FxMenuBar fxMenuBar = createMenu();

        HBox hBox = new HBox(fxMenuBar);
        // largeur de la MenuBar = largeur de la fenêtre - 50
        getScene().widthProperty().addListener((obs, oldVal, newVal) ->
                hBox.setMaxWidth(newVal.doubleValue() - 138));
        setTop(hBox);

        setTitle(MainApp.TITLE);

        LocalSettings.get(this).add("CHECKBOX_MENU", windowCheckAction);

        // On a besoin d'avoir les bounds généré
        this.setOnShown(observable -> {
            this.customMenuBar = new CustomMenuBar(hBox, fxMenuBar);
            CaptionConfiguration cc = new CaptionConfiguration().setCaptionDragRegion(customMenuBar).setControlBackgroundColor(Color.rgb(60, 63, 65)).setCaptionHeight((int)fxMenuBar.getHeight());

            CustomCaption.useForStage(this, cc);
        });

        getOnDocktailorEvent().addListener(this::showPopup);

    }

    public void showPopup() {
        if (this.customMenuBar != null) {
            this.customMenuBar.displayBtnSave(true);
        }
    }

    private static void loadDefaultAction() {
        log.info("Docktailor : Load default interface configuration");
        actionLoadSettings(DocktailorService.getDefaultUiFile());
    }

    protected static void actionLoadSettings(String fileName) {
        log.info("Docktailor : Load default interface configuration : {}", fileName);

        GlobalSettings.getInstance().setFileProvider(fileName);
        AGlobalSettings store = GlobalSettings.getInstance();
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);

        DocktailorUtility.openDockSystemConf(demoDockSchema);

        DocktailorService.getInstance().setLastUIConfigUsed(fileName);
    }

    /**
     * Use GlobalSettings.FILE...
     *
     * @param fileName :
     */
    protected static void actionSaveSettings(String fileName) {
        log.info("Docktailor : Save current interface configuration in {}", fileName);
        DocktailorUtility.storeLayout(fileName);
        DocktailorService.getInstance().getConfigDocktailor().save();
        DocktailorService.getInstance().setLastUIConfigUsed(fileName);

        //AppConfigManager.getInstance().saveProperty(AppConfigManager.LAST_UI_CONFIG_SAVED, fileName);
    }

    protected FxMenuBar createMenu() {
        FxMenuBar fxMenuBar = new FxMenuBar();

        Menu menuApplication = new Menu("Application");

        fxMenuBar.add(menuApplication);

        Platform.runLater(() -> {

            // Custom config
            menuApplication.getItems().add(addCustomConfiguration("Configuration #1", getFILE_1()));
            menuApplication.getItems().add(addCustomConfiguration("Configuration #2", getFILE_2()));
            menuApplication.getItems().add(addCustomConfiguration("Configuration #3", getFILE_3()));

            menuApplication.getItems().add(new SeparatorMenuItem());

            MenuItem menuItemDefaultConf = new MenuItem("Charger la configuration par défaut");
            menuItemDefaultConf.setOnAction(e -> DemoDockWindow.loadDefaultAction());
            menuApplication.getItems().add(menuItemDefaultConf);

            MenuItem menuLeaveApp = new MenuItem("Quitter l'application");
            menuLeaveApp.setOnAction(e -> DocktailorUtility.exit());
            menuApplication.getItems().add(menuLeaveApp);

            MenuItem showPopup = new MenuItem("Show popup save");
            showPopup.setOnAction(e -> showPopup());
            menuApplication.getItems().add(showPopup);

            CheckMenuItem checkMenuItem = new CheckMenuItem("Debug css");

            checkMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue)
                    FxTooltipDebugCss.install(this.getScene());
                else
                    FxTooltipDebugCss.uninstall(this.getScene());
            });
            menuApplication.getItems().add(checkMenuItem);
        });

        Menu menuWindows = new Menu("Windows");
        DocktailorService.getInstance().setAll(PersonDockPane.class, TestDockPane.class);
        menuWindows.getItems().addAll(DocktailorService.getInstance().createMenuItems(this));
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