package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.FxTooltipDebugCss;
import com.enosistudio.docktailor.fx.FxMenuBar;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import net.yetihafen.javafx.customcaption.CaptionConfiguration;
import net.yetihafen.javafx.customcaption.CustomCaption;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demo Window.
 */
@Slf4j
public class DemoDockWindow extends FxDockWindow {
    private CustomMenuBar customMenuBar = null;

    public DemoDockWindow() {
        super("DemoWindow");

        getIcons().add(MainApp.IMAGE);

        // Create the top menu bar
        FxMenuBar fxMenuBar = createMenu();

        HBox hBox = new HBox(fxMenuBar);
        // MenuBar width = window width - 138
        getScene().widthProperty().addListener((obs, oldVal, newVal) ->
                hBox.setMaxWidth(newVal.doubleValue() - 138));
        setTop(hBox);

        setTitle(MainApp.TITLE);

        // Need to have the bounds generated
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
        actionLoadSettings(DocktailorService.getInstance().getDefaultUiFile());
    }

    protected static void actionLoadSettings(String fileName) {
        log.info("Docktailor : Load default interface configuration : {}", fileName);

        DocktailorService.getInstance().getGlobalSettings().setFileProvider(fileName);
        AGlobalSettings store = DocktailorService.getInstance().getGlobalSettings();
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);

        DocktailorService.openDockSystemConf(demoDockSchema);

        DocktailorService.getInstance().setLastUIConfigUsed(fileName);
    }

    /**
     * Use GlobalSettings.FILE...
     *
     * @param fileName :
     */
    protected static void actionSaveSettings(String fileName) {
        log.info("Docktailor : Save current interface configuration in {}", fileName);
        DocktailorService.getSchema().storeLayout(fileName);
        DocktailorService.getInstance().getConfigDocktailor().save();
        DocktailorService.getInstance().setLastUIConfigUsed(fileName);

        //AppConfigManager.getInstance().saveProperty(AppConfigManager.LAST_UI_CONFIG_SAVED, fileName);
    }

    protected FxMenuBar createMenu() {
        FxMenuBar fxMenuBar = new FxMenuBar();

        Menu menuApplication = new Menu("Application");

        fxMenuBar.add(menuApplication);

        Platform.runLater(() -> {

            DocktailorService.getInstance().getPredefinedUiFiles().forEach((s, s2) ->
                    menuApplication.getItems().add(addCustomConfiguration(s, s2)));

            menuApplication.getItems().add(new SeparatorMenuItem());

            MenuItem menuOpenSaveFolder = new MenuItem("Open save folder");
            menuOpenSaveFolder.setOnAction(e -> {
                try {
                    File configFile = new File(DocktailorService.getInstance().getConfigFile());
                    Desktop.getDesktop().open(configFile.getParentFile());
                } catch (IOException ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
            menuApplication.getItems().add(menuOpenSaveFolder);

            MenuItem menuItemDefaultConf = new MenuItem("Load default configuration");
            menuItemDefaultConf.setOnAction(e -> DemoDockWindow.loadDefaultAction());
            menuApplication.getItems().add(menuItemDefaultConf);

            MenuItem menuLeaveApp = new MenuItem("Exit application");
            menuLeaveApp.setOnAction(e -> DocktailorService.exit());
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
        menuWindows.getItems().addAll(DocktailorService.getInstance().createMenuItems(this));
        fxMenuBar.add(menuWindows);

        return fxMenuBar;
    }

    /**
     * Adds a Node to an FxMenuBar.
     *
     * @param strLabel : Displayed name
     * @param fileName : File name
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

        Button btnSave = new Button("Save");
        btnSave.setOnAction(event -> actionSaveSettings(fileName));
        hbox.getChildren().add(btnSave);

        if (Files.exists(Path.of(fileName))) {
            Button btnLoad = new Button("Load");
            btnLoad.setOnAction(event -> actionLoadSettings(fileName));
            hbox.getChildren().add(btnLoad);
        }

        return menuCustomSave1;
    }
}