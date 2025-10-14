package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.common.Hex;
import com.enosistudio.docktailor.fx.FxAction;
import com.enosistudio.docktailor.fx.FxFramework;
import com.enosistudio.docktailor.fx.FxMenuBar;
import com.enosistudio.docktailor.fx.LocalSettings;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import com.enosistudio.docktailor.other.PopupSaveUI;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.yetihafen.javafx.customcaption.CaptionConfiguration;
import net.yetihafen.javafx.customcaption.CustomCaption;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Demo Window.
 */
@Slf4j
public class DemoDockWindow extends FxDockWindow {
    @Getter
    private static final String FILE_1 = Path.of(ServiceDocktailor.getDocktailorSaveFolder(), "docktailor_1.ui").toString();
    @Getter
    private static final String FILE_2 = Path.of(ServiceDocktailor.getDocktailorSaveFolder(), "docktailor_2.ui").toString();
    @Getter
    private static final String FILE_3 = Path.of(ServiceDocktailor.getDocktailorSaveFolder(), "docktailor_3.ui").toString();

    public final FxAction windowCheckAction = new FxAction();
    //private static int seq;

    private static final List<DemoDockWindow> demoDockWindows = new ArrayList<>();

    public DemoDockWindow() {
        super("DemoWindow");
        demoDockWindows.add(this);

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

        this.setOnShown(observable -> {
            CustomCaption.useForStage(this, new CaptionConfiguration().setCaptionDragRegion(fxMenuBar).setControlBackgroundColor(Color.rgb(60, 63, 65)));
        });

        this.widthProperty().addListener((obs, oldVal, newVal) -> showPopup());
        this.heightProperty().addListener((obs, oldVal, newVal) -> showPopup());
        this.xProperty().addListener((obs, oldVal, newVal) -> showPopup());
        this.yProperty().addListener((obs, oldVal, newVal) -> showPopup());
    }

    @Getter
    private final PopupSaveUI popup = new PopupSaveUI();
    public static void showPopup(){
        for (DemoDockWindow demoDockWindow : demoDockWindows) {
            demoDockWindow.getPopup().show(demoDockWindow.getParentStackPane());
        }
    }

    protected static void c(StringBuilder sb) {
        int min = 100;
        int v = min + new Random().nextInt(255 - min);
        sb.append(Hex.toHexByte(v));
    }

    private static void loadDefaultAction() {
        log.info("Docktailor : Load default interface configuration");
        actionLoadSettings(ServiceDocktailor.getDefaultUiFile());
    }

    protected static void actionLoadSettings(String fileName) {
        log.info("Docktailor : Load default interface configuration : {}", fileName);

        GlobalSettings.getInstance().setFileProvider(fileName);
        AGlobalSettings store = GlobalSettings.getInstance();
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);

        FxFramework.openDockSystemConf(demoDockSchema);

        ServiceDocktailor.getInstance().setLastUIConfigUsed(fileName);
    }

    /**
     * Use GlobalSettings.FILE...
     *
     * @param fileName :
     */
    protected static void actionSaveSettings(String fileName) {
        log.info("Docktailor : Save current interface configuration in {}", fileName);
        FxFramework.storeLayout(fileName);
        ServiceDocktailor.getInstance().getConfigDocktailor().save();
        ServiceDocktailor.getInstance().setLastUIConfigUsed(fileName);

        //AppConfigManager.getInstance().saveProperty(AppConfigManager.LAST_UI_CONFIG_SAVED, fileName);
    }

    protected FxMenuBar createMenu() {
        FxMenuBar fxMenuBar = new FxMenuBar();

        Menu menuApplication = new Menu("Application");

        fxMenuBar.add(menuApplication);

        // Ajout des différentes vue de tacp
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
            menuLeaveApp.setOnAction(e -> FxFramework.exit());
            menuApplication.getItems().add(menuLeaveApp);

            MenuItem showPopup= new MenuItem("Show popup save");
            showPopup.setOnAction(e -> this.showPopup());
            menuApplication.getItems().add(showPopup);
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