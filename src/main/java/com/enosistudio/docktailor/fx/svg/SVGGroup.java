package com.enosistudio.docktailor.fx.svg;

import com.enosistudio.docktailor.common.UtilColor;
import com.enosistudio.docktailor.common.XmlParser;
import javafx.beans.NamedArg;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.regex.Pattern;

/**
 * Handles borders but complex to resize
 */
@Getter
public class SVGGroup extends Group implements ISVG {
    private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
    private static final String DEFAULT_PATH = "M10,16 10, 0 0,8z";

    private Color colorFill = Color.BLUE;
    private Color colorStroke = Color.TURQUOISE;
    private int strokeSize = 2;
    private String url;
    private final SVGPath currentSVGPath;

    /**
     * NoArgs
     */
    public SVGGroup() {
        this(DEFAULT_PATH, Color.BLUE, Color.TURQUOISE, 2);
    }

    /**
     * Constructeur via url
     *
     * @param url :
     */
    public SVGGroup(@NamedArg("url") String url) {
        setMouseTransparent(true);
        setTransform(100, 100);
        this.url = ISVG.validateUrl(url);
        this.currentSVGPath = createPath(extractPathFromSVGFile(this.url), UtilColor.colorToHex(this.colorFill), UtilColor.colorToHex(colorStroke));

        getChildren().add(currentSVGPath);
    }

    /**
     * Contructeur via path
     */
    public SVGGroup(@NamedArg("path") String path, @NamedArg("fill-color") Color colorFill, @NamedArg("stroke-color") Color colorStroke, @NamedArg("stroke-width") int size) {
        this.colorFill = colorFill;
        this.colorStroke = colorStroke;
        this.strokeSize = size;
        this.currentSVGPath = createPath(path, UtilColor.colorToHex(this.colorFill), UtilColor.colorToHex(this.colorStroke));

        setMouseTransparent(true);
        getChildren().add(currentSVGPath);
    }

    private String extractPathFromSVGFile(String url) {
        Document xmlDocument = XmlParser.fileToDocument(url);
        Element elementPath = XmlParser.elementFromDocument(xmlDocument, "path", 0);
        return XmlParser.ValueFromAttribut(elementPath, "d");
    }

    private SVGPath createPath(String d, String fill, String stroke) {
        SVGPath path = new SVGPath();
        path.setContent(d);
        //path.setStyle("-fill:" + fill + ";-hover-fill:" + hoverFill+";");
        path.setStyle(
                "-fx-stroke-width: " + strokeSize + ";" +
                        "-fx-stroke: " + stroke + ";" +
                        "-fx-fill: " + fill + ";");
        return path;
    }


    public static void main(String[] args) {
        System.out.println(Color.RED);
    }

    public void updatePath() {
        this.currentSVGPath.setStyle(
                "-fx-stroke-width: " + this.strokeSize + ";" +
                        "-fx-stroke: " + UtilColor.colorToHex(this.colorStroke) + ";" +
                        "-fx-fill: " + UtilColor.colorToHex(this.colorFill) + ";");
    }

    private static String constructDetailedExceptionMessage(String var0, Throwable var1) {
        if (var1 == null) {
            return var0;
        } else {
            String var2 = var1.getMessage();
            return constructDetailedExceptionMessage(var2 != null ? var0 + ": " + var2 : var0, var1.getCause());
        }
    }


    @Override
    public void setColorFill(Color colorFill) {
        this.colorFill = colorFill;
        updatePath();
    }

    @Override
    public void setSizeStroke(int sizeStroke) {
        this.strokeSize = sizeStroke;
        updatePath();
    }

    @Override
    public void setColorStroke(Color colorStroke) {
        this.colorStroke = colorStroke;
        updatePath();
    }

    @Override
    public void setTransform(int height, int width) {
        double originalWidth = currentSVGPath.prefWidth(-1);
        double originalHeight = currentSVGPath.prefHeight(originalWidth);

        double scaleX = width / originalWidth;
        double scaleY = height / originalHeight;

        currentSVGPath.setScaleX(scaleX);
        currentSVGPath.setScaleY(scaleY);
    }

    @Override
    public void setSVGPathFromUrl(String url) {
        this.url = ISVG.validateUrl(url);
        currentSVGPath.setContent(extractPathFromSVGFile(this.url));
    }
}
