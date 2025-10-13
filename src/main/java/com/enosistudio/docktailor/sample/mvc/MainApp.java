package com.enosistudio.docktailor.sample.mvc;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.GlobalSettings;
import com.enosistudio.docktailor.fxdock.internal.DemoDockSchema;
import com.enosistudio.docktailor.fxdock.internal.ServiceDocktailor;
import com.enosistudio.docktailor.fx.FxFramework;
//import com.enosistudio.docktailor.fx.R;
import com.enosistudio.generated.R;
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

//    public static final Image IMAGE = R.loadImage("docktailor/icons/logo.png");
    public static final String TITLE = "DockTailor example";

    public static void main(String[] args) {
        ARGS.addAll(Arrays.stream(args).toList());
        ServiceDocktailor.IS_DEBUG = ARGS.contains("-debug");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("MainApp : Application start");
        Application.setUserAgentStylesheet(ServiceDocktailor.getDocktailorCss().getAbsoluteURL().toExternalForm());

        GlobalSettings.getInstance().setFileProvider(ServiceDocktailor.getInstance().getLastUIConfigUsed());
        AGlobalSettings store = GlobalSettings.getInstance();
        DemoDockSchema demoDockSchema = new DemoDockSchema(store);
        FxFramework.openDockSystemConf(demoDockSchema);
    }
}
