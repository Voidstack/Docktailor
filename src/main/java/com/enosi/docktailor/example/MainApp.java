package com.enosi.docktailor.example;

import com.enosi.docktailor.common.util.GlobalSettings;
import com.enosi.docktailor.docktailor.fx.FxFramework;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainApp extends Application {
    public final static List<String> ARGS = new ArrayList<>();
    public static final Image IMAGE = new Image("images/icons/logo.png");
    public static final String TITLE = "DockTailor example";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxFramework.openDockSystemConf(GlobalSettings.getDEFAULT_FILE());
    }

    public static void main(String[] args) {
        ARGS.addAll(Arrays.stream(args).toList());
        launch(args);
    }
}
