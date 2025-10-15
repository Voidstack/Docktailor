package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.DocktailorUtility;
import com.enosistudio.docktailor.fx.fxdock.internal.DocktailorService;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PopupSaveUI extends HBox {
    private final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
    private final FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
    private final TranslateTransition tt = new TranslateTransition(Duration.millis(300), this);
    private final ParallelTransition inTransition = new ParallelTransition(fadeIn, tt);
    private static final List<PopupSaveUI> instances = new ArrayList<>();

    @Setter
    private EventHandler<ActionEvent> onSave = event -> {
        DocktailorUtility.storeLayout(DocktailorService.getInstance().getLastUIConfigUsed());
        DocktailorService.getInstance().getConfigDocktailor().save();
        hides();
    };

    public PopupSaveUI() {
        instances.add(this);

        this.setStyle("-fx-background-color: #333; -fx-background-radius:5; -fx-padding:2");
        this.setOpacity(0);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5);

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        tt.setFromY(-25);
        tt.setToY(0);
/*        tt.setFromX(100);
        tt.setToX(0);*/
        tt.setInterpolator(Interpolator.EASE_OUT);

        // ===== Button Save =====
        Button btnSave = new Button("\uD83D\uDCBE");
        btnSave.getStyleClass().add("buttonValid");
        btnSave.setTooltip(new Tooltip("Sauvegarder la configuration")); // tooltip
        btnSave.setMaxWidth(Double.MAX_VALUE); // prend toute la largeur
        btnSave.setMaxHeight(Double.MAX_VALUE);
        btnSave.setOnAction(onSave);

        this.getChildren().add(btnSave);
        HBox.setHgrow(btnSave, Priority.ALWAYS);

        // ===== Button Close =====
        Button btnClose = new Button("✖");
        btnClose.getStyleClass().add("buttonTransluent");
        btnClose.setTooltip(new Tooltip("Fermer cette notification")); // tooltip
        btnClose.setMaxWidth(Double.MAX_VALUE); // prend toute la largeur
        btnClose.setMaxHeight(Double.MAX_VALUE);
        btnClose.setOnAction(event -> hides());
        this.getChildren().add(btnClose);
        HBox.setHgrow(btnClose, Priority.ALWAYS);

        // Taille maximale de la HBox
        this.setMaxWidth(80); // ajustable selon besoin
        this.setMaxHeight(40);

        StackPane.setAlignment(this, Pos.TOP_RIGHT);
        Insets insets = new Insets(50, 10, 10, 10);
        StackPane.setMargin(this, insets);
    }

    private void hide() {
        this.setMouseTransparent(true);
        fadeOut.play();
    }

    public static void hides() {
        for (PopupSaveUI instance : instances) {
            instance.hide();
        }
    }

    public void show(StackPane parent) {
        if (parent.getChildren().contains(this)) return;

        parent.getChildren().add(this);
        this.setMouseTransparent(false);

        fadeOut.setOnFinished(e -> parent.getChildren().remove(this));

        inTransition.play();
    }
}
