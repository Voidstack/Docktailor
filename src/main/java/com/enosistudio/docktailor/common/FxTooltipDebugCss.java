package com.enosistudio.docktailor.common;

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
                Tooltip.uninstall(lastNode, tooltip); // Remove from the old node
            }
            lastNode = node;
            updateTooltipText(node);
            Tooltip.install(node, tooltip); // Install on the new node
        } else {
            // Refresh the text even if it's the same node
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
            Tooltip.uninstall(lastNode, tooltip); // Clean up the last node
            lastNode = null;
        }
        // Clear the text (optional), execute on UI thread
        Platform.runLater(() -> tooltip.setText(""));
    }
}
