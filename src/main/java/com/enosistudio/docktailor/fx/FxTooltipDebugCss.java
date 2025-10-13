package com.enosistudio.docktailor.fx;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FxTooltipDebugCss {
    /*public static void install(Scene sc) {
        if(!ServiceDocktailor.IS_DEBUG)
            return;

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.INDEFINITE);

        // Écoute globale sur tous les survols
        sc.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, e -> {
            Node node = e.getPickResult().getIntersectedNode();
            if (node != null) {
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
                Tooltip.install(node, tooltip);
            }
        });
    }*/
}
