package com.enosistudio.docktailor.svg;

import com.enosistudio.docktailor.other.UtilColor;
import com.enosistudio.docktailor.other.Vector2;
import com.enosistudio.docktailor.other.XmlParser;
import com.enosistudio.docktailor.svg.svginterface.ISVG;
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
 * Composent fxml pour afficher un svg.
 * Ne gère pas les bordures
 * Facile à résize
 * Ne gère que les fichiers svg entièrement constitué "<path>xxx</path>"
 */
@Getter
public class SVGRegion extends Region implements ISVG {
    private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
    private static final int DEFAULT_SIZE = 20;
    private static final Color DEFAULT_FILL_COLOR = Color.WHITE;
    private final SVGPath svgPath;

    private String url;
    private Color colorFill;
    private final Vector2 transform = Vector2.ZERO;

    public SVGRegion(@NamedArg("url") String url) {
        this(url, DEFAULT_SIZE, UtilColor.colorToHex(DEFAULT_FILL_COLOR));
    }

    public SVGRegion(@NamedArg("url") String url, @NamedArg("size") int size){
        this(url, size, UtilColor.colorToHex(DEFAULT_FILL_COLOR));
    }

    /**
     * Constructeur pour le fxml.
     *
     * @param url : url du fichier svg
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
     * Change la height et width du svg.
     *
     * @param height : int
     * @param width  : int
     */
    public void setTransform(int height, int width) {
        setMaxSize(height, width);
        setMinSize(height, width);
        setPrefSize(height, width);
        svgPath.resize(height, width);
        resize(height, width);
        this.transform.set(width, height);
    }

    public void setTransform(Vector2 vector2) {
        setTransform((int) vector2.getX(), (int) vector2.getY());
    }

    @Override
    public void setColorFill(Color colorFill) {
        this.colorFill = colorFill;
    }

    /**
     * Méthode non impl car SVGRegion ne gère pas les bordures
     *
     * @return Color : couleur de la ligne
     */
    @Override
    public Color getColorStroke() {
        throw new UnsupportedOperationException();
    }

    /**
     * Méthode non impl car SVGRegion ne gère pas les bordures.
     *
     * @param sizeStroke :
     */
    @Override
    public void setSizeStroke(int sizeStroke) {
        throw new UnsupportedOperationException();
    }

    /**
     * Méthode non impl car SVGRegion ne gère pas les bordures.
     *
     * @param colorStroke :
     */
    @Override
    public void setColorStroke(Color colorStroke) {
        throw new UnsupportedOperationException();
    }

    private void setColorFill(String color) {
        setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Permet de mettre à jour le SVG depuis un fichier
     *
     * @param url : url du fichier svg
     */
    public void setSVGPathFromUrl(String url) {
        this.url = ISVG.validateUrl(url);
        this.svgPath.setContent(extractPathFromSVGFile(this.url));
    }

    /**
     * Méthode qui retourne le contenue de l'attribut 'd' de la balise "path" d'un fichier.
     *
     * @param url : fichier
     * @return : contenue de l'attribut 'd'
     */
    private String extractPathFromSVGFile(@NonNull String url) {
        Document xmlDocument = XmlParser.fileToDocument(url);
        xmlDocument.getDocumentElement().normalize();

        // Obtenir tous les éléments
        NodeList pathList = xmlDocument.getElementsByTagName("path");

        // Fusionner les chemins
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
