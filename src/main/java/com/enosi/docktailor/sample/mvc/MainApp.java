package com.enosi.docktailor.sample.mvc;

import com.enosi.docktailor.fxdock.internal.ServiceDocktailor;
import com.enosi.docktailor.fx.FxFramework;
import com.enosi.docktailor.fx.R;
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
    public static final Image IMAGE = R.loadImage("images/icons/logo.png");
    public static final String TITLE = "DockTailor example";

    public static void main(String[] args) {
        ARGS.addAll(Arrays.stream(args).toList());
        ServiceDocktailor.IS_DEBUG = ARGS.contains("-debug");
        ServiceDocktailor.getInstance().setup(MainApp.class);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("MainApp : Application start");
        FxFramework.openDockSystemConf(ServiceDocktailor.getInstance().getLastUIConfigUsed());
    }
}
