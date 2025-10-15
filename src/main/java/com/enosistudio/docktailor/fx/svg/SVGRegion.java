package com.enosistudio.docktailor.fx.svg;

import com.enosistudio.docktailor.common.UtilColor;
import com.enosistudio.docktailor.common.Vector2;
import com.enosistudio.docktailor.common.XmlParser;
import com.enosistudio.docktailor.fx.svg.svginterface.ISVG;
import javafx.beans.NamedArg;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.regex.Pattern;

/**
 * A JavaFX Region component for displaying SVG graphics. This class provides functionality to load, resize, and display
 * SVG files. It supports only SVG files composed entirely of "path" elements. Note: This component does not handle
 * borders.
 */
@Getter
@SuppressWarnings("unused")
public class SVGRegion extends Region implements ISVG {
    /**
     * Pattern to quickly match URLs.
     */
    private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
    /**
     * Default size for the SVG region.
     */
    private static final int DEFAULT_SIZE = 20;
    /**
     * Default fill color for the SVG.
     */
    private static final Color DEFAULT_FILL_COLOR = Color.WHITE;
    /**
     * The SVGPath object used to render the SVG.
     */
    private final SVGPath svgPath;

    /**
     * The URL of the SVG file.
     */
    private String url;
    /**
     * The fill color of the SVG.
     */
    private Color colorFill;
    /**
     * The current transformation (width and height) of the SVG.
     */
    private final Vector2 transform = Vector2.ZERO;

    /**
     * Constructs an SVGRegion with the specified URL and default size and color.
     *
     * @param url the URL of the SVG file
     */
    public SVGRegion(@NamedArg("url") String url) {
        this(url, DEFAULT_SIZE, UtilColor.colorToHex(DEFAULT_FILL_COLOR));
    }

    /**
     * Constructs an SVGRegion with the specified URL and size, and default color.
     *
     * @param url  the URL of the SVG file
     * @param size the size of the SVG region
     */
    public SVGRegion(@NamedArg("url") String url, @NamedArg("size") int size) {
        this(url, size, UtilColor.colorToHex(DEFAULT_FILL_COLOR));
    }

    /**
     * Constructs an SVGRegion with the specified URL, size, and color.
     *
     * @param url   the URL of the SVG file
     * @param size  the size of the SVG region
     * @param color the fill color of the SVG
     */
    public SVGRegion(@NamedArg("url") String url, @NamedArg("size") int size, @NamedArg("color") String color) {
        this.svgPath = new SVGPath();
        setMouseTransparent(true);
        setTransform(size, size);
        setColorFill(color == null ? UtilColor.colorToHex(DEFAULT_FILL_COLOR) : color);
        setShape(svgPath);
        setSVGPathFromUrl(url);
    }

    /**
     * Updates the height and width of the SVG region.
     *
     * @param height the new height of the SVG region
     * @param width  the new width of the SVG region
     */
    public void setTransform(int height, int width) {
        setMaxSize(height, width);
        setMinSize(height, width);
        setPrefSize(height, width);
        svgPath.resize(height, width);
        resize(height, width);
        this.transform.set(width, height);
    }

    /**
     * Updates the height and width of the SVG region using a Vector2 object.
     *
     * @param vector2 the Vector2 object containing the new width and height
     */
    public void setTransform(Vector2 vector2) {
        setTransform((int) vector2.getX(), (int) vector2.getY());
    }

    /**
     * Sets the fill color of the SVG.
     *
     * @param colorFill the new fill color
     */
    @Override
    public void setColorFill(Color colorFill) {
        this.colorFill = colorFill;
    }

    /**
     * Not implemented as SVGRegion does not handle borders.
     *
     * @return nothing, as this method throws an UnsupportedOperationException
     * @throws UnsupportedOperationException always
     */
    @Override
    public Color getColorStroke() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented as SVGRegion does not handle borders.
     *
     * @param sizeStroke the size of the stroke
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setSizeStroke(int sizeStroke) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented as SVGRegion does not handle borders.
     *
     * @param colorStroke the color of the stroke
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setColorStroke(Color colorStroke) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the fill color of the SVG using a string representation of the color.
     *
     * @param color the string representation of the color
     */
    private void setColorFill(String color) {
        setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Updates the SVG content from the specified file URL.
     *
     * @param url the URL of the SVG file
     */
    public void setSVGPathFromUrl(String url) {
        this.url = ISVG.validateUrl(url);
        this.svgPath.setContent(extractPathFromSVGFile(this.url));
    }

    /**
     * Extracts the content of the 'd' attribute from all "path" elements in an SVG file.
     *
     * @param url the URL of the SVG file
     * @return the concatenated content of the 'd' attributes
     */
    private String extractPathFromSVGFile(@NonNull String url) {
        Document xmlDocument = XmlParser.fileToDocument(url);
        xmlDocument.getDocumentElement().normalize();

        // Get all "path" elements
        NodeList pathList = xmlDocument.getElementsByTagName("path");

        // Concatenate the 'd' attributes
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pathList.getLength(); i++) {
            Element pathElement = (Element) pathList.item(i);
            String d = pathElement.getAttribute("d");
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(d);
        }

        return sb.toString();
    }
}