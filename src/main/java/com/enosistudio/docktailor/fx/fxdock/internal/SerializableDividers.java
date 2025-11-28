package com.enosistudio.docktailor.fx.fxdock.internal;

import javafx.scene.control.SplitPane;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente de manière sérialisable et lisible les positions des dividers d'un SplitPane.
 * Immutable pour garantir l'intégrité.
 */
@Getter
public final class SerializableDividers implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * -- GETTER --
     * Retourne les positions
     */
    private final List<Double> positions;

    private SerializableDividers(List<Double> positions) {
        this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
    }

    /** Crée à partir d'une liste de SplitPane.Divider */
    public static SerializableDividers ofDividers(List<SplitPane.Divider> dividers) {
        List<Double> pos = new ArrayList<>();
        dividers.forEach(d -> pos.add(d.getPosition()));
        return new SerializableDividers(pos);
    }

    /** Chaîne réutilisable */
    @Override
    public String toString() {
        return positions.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "DividerData[", "]"));
    }

    /** Reconstitue depuis la chaîne */
    public static SerializableDividers fromString(String str) {
        if (!str.startsWith("DividerData[") || !str.endsWith("]")) {
            throw new IllegalArgumentException("Format invalide : " + str);
        }
        String content = str.substring("DividerData[".length(), str.length() - 1);
        List<Double> pos = new ArrayList<>();
        if (!content.isEmpty()) {
            for (String s : content.split(",")) {
                pos.add(Double.parseDouble(s));
            }
        }
        return new SerializableDividers(pos);
    }
}
