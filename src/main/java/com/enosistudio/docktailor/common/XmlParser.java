package com.enosistudio.docktailor.common;

import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class XmlParser {
    /**
     * Méthode qui retourne un object Document depuis un url.
     * @param url : {resources}doc/test.xml
     * @return : org.w3c.dom.Document;
     */
    public static Document fileToDocument(String url) {
        try(InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(url)){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        }catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retourne un Element du Document.
     * @param doc : org.w3c.dom.Document le doc xml
     * @param tagName : le nom de la balise
     * @param index : 0 par défault, x retourne la balise numéro x dans l'ordre de lecture du doc xml
     * @return : org.w3c.dom.Element
     */
    public static Element elementFromDocument(Document doc, String tagName, int index){
        try{
            Node node = doc.getElementsByTagName(tagName).item(index);
            return (Element) node;
        }catch (ClassCastException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Retourne la valeur de l'attribut d'un élément xml.
     * @param element : org.w3c.dom.Element
     * @param attribut : String
     * @return : String
     */
    public static String ValueFromAttribut(Element element, String attribut){
        String value = element.getAttribute(attribut);
        if(attribut.isEmpty()){
            throw new NullPointerException("Contenue de l'attribut '"+attribut+" null.");
        }
        return value;
    }
}
