package com.enosistudio.docktailor.fx.svg;

import javafx.scene.paint.Color;
import lombok.NonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface ISVG {
    String getUrl();

    Color getColorFill();

    void setColorFill(Color colorFill);

    Color getColorStroke();

    void setSizeStroke(int sizeStroke);

    void setColorStroke(Color colorStroke);

    void setTransform(int height, int width);

    void setSVGPathFromUrl(String s);

    public static String validateUrl(@NonNull String strUrl) {
        strUrl = strUrl.trim();

        if (strUrl.startsWith("/")) {
            strUrl = strUrl.replaceFirst("/", "");
        }

        if (strUrl.isEmpty()) {
            throw new IllegalArgumentException("URL must not be empty");
        }

        // Check if it's a URL
        try {
            return new URL(strUrl).toString();
        } catch (MalformedURLException ignored) {
        }

        // Check if it's a local file
        File file = new File(strUrl);
        if (file.exists()) return file.getAbsolutePath();

        // Check if it's a classpath resource
        String path = strUrl.startsWith("/") ? strUrl : "/" + strUrl;
        if (SVGGroup.class.getResource(path) != null) return strUrl;

        throw new IllegalArgumentException("Invalid URL, file, or classpath resource: " + strUrl);
    }
}
