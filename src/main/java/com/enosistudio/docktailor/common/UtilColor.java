package com.enosistudio.docktailor.common;

import java.awt.*;

@SuppressWarnings("unused")
public class UtilColor {

    public static final javafx.scene.paint.Color FX_GREEN = new javafx.scene.paint.Color(.09f, .65f, .45f, .8f);
    public static final Color AWT_GREEN = new Color(.09f, .65f, .45f, .8f);

    /**
     * Color -> #ffffff.
     * @param color : Color
     * @return : hex format #ffffff
     */
    public static String colorToHex(Color color) {
        return String.format("#%06x", color.getRGB() & 0x00FFFFFF);
    }

    public static Color hexToColor(String hex){
        if(!hex.matches("^0x[A-Fa-f0-9]{8}$")){
            throw new IllegalArgumentException("Invalid color format");
        }
        int red = Integer.parseInt(hex.substring(2, 4), 16);
        int green = Integer.parseInt(hex.substring(4, 6), 16);
        int blue = Integer.parseInt(hex.substring(6, 8), 16);
        return new Color(red, green, blue);
    }

    public static String colorToHex(javafx.scene.paint.Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    /**
     * Returns Black or White to best contrast with the provided color
     * @param color :
     * @return : BLACK/WHITE
     */
    public static javafx.scene.paint.Color getContrastingColor(javafx.scene.paint.Color color) {
        double luminance = (0.299*color.getRed() + 0.587*color.getGreen() + 0.114*color.getBlue());
        return luminance > .5 ? javafx.scene.paint.Color.BLACK : javafx.scene.paint.Color.WHITE;
    }

    public static Color getContrastingColor(Color color) {
        double luminance = color.getRed() + color.getGreen() + color.getBlue();
        return luminance < 100f ? Color.BLACK : Color.WHITE;
    }

    public static String getContrastingTextColor(javafx.scene.paint.Color color) {
        double luminance = (0.299*color.getRed() + 0.587*color.getGreen() + 0.114*color.getBlue());
        return luminance > .5 ? "black" : "white";
    }

    public static Color toAwtColor(javafx.scene.paint.Color fxColor){
        return new Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue(), (float) fxColor.getOpacity());
    }

    public static javafx.scene.paint.Color toFxColor(Color fxColor) {
        return javafx.scene.paint.Color.rgb(fxColor.getRed(), fxColor.getGreen(), fxColor.getBlue(), fxColor.getAlpha() / 255.0);
    }
}