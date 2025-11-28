package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.generated.R;
import com.enosistudio.docktailor.sample.controller.*;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class MainApp extends Application {
    public static final List<String> ARGS = new ArrayList<>();

    public static final Image IMAGE = new Image(R.com.enosistudio.docktailor.sample.icons.logoPng.getResourcePath());
    public static final String TITLE = "DockTailor example";

    private final String saveFolder = Path.of(System.getenv("APPDATA"), "enosistudio", "docktailor").toString();

    private final Map<String, String> predefinedUiFiles = Map.of(
            "Configuration #1", Path.of(saveFolder, "docktailor_1.ui").toString(),
            "Configuration #2", Path.of(saveFolder, "docktailor_2.ui").toString(),
            "Configuration #3", Path.of(saveFolder, "docktailor_3.ui").toString()
    );

    public static void main(String[] args) {
        ARGS.addAll(Arrays.stream(args).toList());
        DocktailorService.isDebug = ARGS.contains("-debug");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("MainApp : Application start");
        Application.setUserAgentStylesheet(R.com.enosistudio.docktailor.sample.css.modena.mainCss.toExternalForm());

        GlobalSettings docktailorSettings = DocktailorService.getInstance()
                .setConfigFile(Path.of(saveFolder, "docktailor.conf").toString())
                .setDefaultUiFile(R.com.enosistudio.docktailor.docktailorDefaultUi.getURL().getFile())
                .setPredefinedUiFiles(predefinedUiFiles)
                .setDraggableTab(PersonDockPane.class, TestDockPane.class, RedDockPane.class, BlueDockPane.class, GreenDockPane.class)
                .init();
        DemoDockSchema demoDockSchema = new DemoDockSchema(docktailorSettings);
        DocktailorService.openDockSystemConf(demoDockSchema);
    }
}
