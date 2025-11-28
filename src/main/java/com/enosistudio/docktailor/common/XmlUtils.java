package com.enosistudio.docktailor.common;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class XmlUtils {
    /**
     * Method that returns a Document object from a URL.
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
     * Returns an Element from the Document.
     * @param doc : org.w3c.dom.Document the XML document
     * @param tagName : the tag name
     * @param index : 0 by default, x returns the tag number x in the reading order of the XML document
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
     * Returns the value of an attribute from an XML element.
     * @param element : org.w3c.dom.Element
     * @param attribut : String
     * @return : String
     */
    public static String ValueFromAttribut(Element element, String attribut){
        String value = element.getAttribute(attribut);
        if(attribut.isEmpty()){
            throw new NullPointerException("Attribute '"+attribut+"' content is null.");
        }
        return value;
    }
}
