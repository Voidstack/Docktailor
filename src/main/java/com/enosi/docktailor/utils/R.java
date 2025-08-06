package com.enosi.docktailor.utils;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 *
 */
public class R {
    public static Image loadImage(String relativePath) {
        return new Image(Objects.requireNonNull(R.class.getResource("/" + relativePath)).toExternalForm());
    }

    public static String loadStringFromFile(String relativePath) {
        return Objects.requireNonNull(R.class.getResource(relativePath)).toExternalForm();
    }
}
