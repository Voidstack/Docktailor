package com.enosistudio.docktailor.svg.svginterface;

import com.enosistudio.docktailor.svg.SVGGroup;
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
        if (strUrl.isEmpty()) {
            throw new IllegalArgumentException("URL must not be empty");
        }

        // Vérifie si c'est une URL
        try {
            return new URL(strUrl).toString();
        } catch (MalformedURLException ignored) {
        }

        // Vérifie si c'est un fichier local
        File file = new File(strUrl);
        if (file.exists()) return file.getAbsolutePath();

        // Vérifie si c'est une ressource classpath
        String path = strUrl.startsWith("/") ? strUrl : "/" + strUrl;
        if (SVGGroup.class.getResource(path) != null) return strUrl;

        throw new IllegalArgumentException("Invalid URL, file, or classpath resource: " + strUrl);
    }
}
