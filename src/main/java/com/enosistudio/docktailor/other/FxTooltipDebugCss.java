package com.enosistudio.docktailor.other;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FxTooltipDebugCss {
    private static final Tooltip tooltip = new Tooltip();
    private static Node lastNode;
    private static final EventHandler<MouseEvent> handler = e -> {
        Node node = e.getPickResult().getIntersectedNode();
        if (node == null) return;

        if (node != lastNode) {
            if (lastNode != null) {
                Tooltip.uninstall(lastNode, tooltip); // retire de l'ancien noeud
            }
            lastNode = node;
            updateTooltipText(node);
            Tooltip.install(node, tooltip); // installe sur le nouveau noeud
        } else {
            // si on veut rafraîchir le texte même si c'est le même node
            updateTooltipText(node);
        }
    };

    private static void updateTooltipText(Node node) {
        StringBuilder sb = new StringBuilder();
        Node current = node;
        while (current != null) {
            sb.append(current.getClass().getSimpleName())
                    .append(" : ")
                    .append(String.join(", ", current.getStyleClass()))
                    .append("\n");
            current = current.getParent();
        }
        tooltip.setText(sb.toString());
    }

    public static void install(Scene sc) {
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.INDEFINITE);
        sc.addEventFilter(MouseEvent.MOUSE_MOVED, handler);
    }

    public static void uninstall(Scene sc) {
        sc.removeEventFilter(MouseEvent.MOUSE_MOVED, handler);
        if (lastNode != null) {
            Tooltip.uninstall(lastNode, tooltip); // nettoie le dernier noeud
            lastNode = null;
        }
        // vide le texte (optionnel), exécution sur le thread UI
        Platform.runLater(() -> tooltip.setText(""));
    }
}
