package com.enosi.docktailor.docktailor.dock;

import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.docktailor.fx.*;
import com.enosi.docktailor.docktailor.fx.settings.LocalSettings;
import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import com.enosi.docktailor.docktailor.fxdock.FxDockStyles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

/**
 * Demo Browser.
 */
@Slf4j(topic = "DemoBrowser")
public class DemoBrowser extends FxDockPane {
    public final TextField addressField;
    public final WebView view;
    public final FxAction reloadAction = new FxAction(this::reload);
    public final Label statusField;

    public DemoBrowser() {
        super(DemoDockSchema.BROWSER);
        setTitle("Browser");

        addressField = new TextField();
        addressField.setOnAction((ev) ->
        {
            String url = addressField.getText();
            setUrl(url);
        });
        LocalSettings.get(this).add("URL", addressField.textProperty());

        view = new WebView();
        view.getEngine().setOnError(this::handleError);
        view.getEngine().setOnStatusChanged(this::handleStatusChange);
        Worker<Void> w = view.getEngine().getLoadWorker();
        w.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue v, Worker.State old, Worker.State cur) {
                log.debug(cur.toString());

                if (w.getException() != null && cur == State.FAILED) {
                    log.error("", w.getException());
                }
            }
        });

        statusField = new Label();

        CPane p = new CPane();
        p.setGaps(10, 5);
        p.setCenter(view);
        p.setBottom(statusField);
        setContent(p);

        FX.later(this::reload);
    }


    @Override
    public Node createToolBar(boolean tabMode) {
        HPane t = new HPane(5);
        t.setMaxWidth(Double.MAX_VALUE);
        t.setPadding(new Insets(2, 2, 2, 2));

        if (!tabMode) {
            Button b = new Button("x");
            FxDockStyles.TOOLBAR_CLOSE_BUTTON.set(b);
            closeAction.attach(b);

            t.add(titleField);
            t.add(b);
        }
        t.fill(addressField);
        t.add(new FxButton("Reload", reloadAction));
        return t;
    }


    public void reload() {
        String url = getUrl();
        if (CKit.isNotBlank(url)) {
            setUrl(url);
        }
    }


    protected void handleStatusChange(WebEvent<String> ev) {
        statusField.setText(ev.getData());
    }


    protected void handleError(WebErrorEvent ev) {
        log.error(ev.getMessage());
    }

    public String getUrl() {
        return addressField.getText();
    }

    public void setUrl(String url) {
        log.info(url);

        addressField.setText(url);
        view.getEngine().load(url);
    }
}
