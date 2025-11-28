package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.generated.R;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MainApp extends Application {
    public static final List<String> ARGS = new ArrayList<>();

    public static final Image IMAGE = new Image(R.com.enosistudio.docktailor.icons.logoPng.getResourcePath());
    public static final String TITLE = "DockTailor example";

    public static void main(String[] args) {
        ARGS.addAll(Arrays.stream(args).toList());
        DocktailorService.isDebug = ARGS.contains("-debug");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("MainApp : Application start");
        Application.setUserAgentStylesheet(R.com.enosistudio.docktailor.css.modena.mainCss.toExternalForm());

        GlobalSettings store = DocktailorService.getInstance().getGlobalSettings();
        store.setFileProvider(DocktailorService.getInstance().getLastUIConfigUsed());
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);
        DocktailorService.openDockSystemConf(demoDockSchema);
    }
}
