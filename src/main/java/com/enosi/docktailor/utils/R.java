package com.enosi.docktailor.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Slf4j(topic = "R")
public class R {
    /**
     * Load an image.
     *
     * @param relativePath The path to the image
     * @return The image
     */
    public static Image loadImage(String relativePath) {
        log.debug("R : Attempting to load image from path: /{}", relativePath);
        try {
            Image image = new Image(Objects.requireNonNull(
                    R.class.getResource("/" + relativePath),
                    "R : Image resource not found: " + relativePath
            ).toExternalForm());
            log.info("R : Successfully loaded image: {}", relativePath);
            return image;
        } catch (NullPointerException e) {
            log.error("R : Failed to load image: {} - Resource not found", relativePath, e);
            throw e;
        } catch (Exception e) {
            log.error("R : Unexpected error while loading image: {}", relativePath, e);
            throw e;
        }
    }

    /**
     * Load a parent from an FXML file.
     *
     * @param relativePath fxml/myFile.fxml
     * @return The parent node
     */
    public static Parent loadParentFromFxml(String relativePath) {
        try {
            URL fxmlUrl = R.class.getResource("/" + relativePath);
            if (fxmlUrl == null) {
                log.error("R : Failed to load string file: {} - Resource not found", relativePath);
                return null;
            }
            return FXMLLoader.load(fxmlUrl);
        } catch (IOException e) {
            log.error("R : Failed to load string file: {} - IOException", relativePath, e);
            return null;
        }
    }

    /**
     * Load a string from a file.
     *
     * @param relativePath css/main.css
     * @return The string
     */
    public static String loadStringFromFile(String relativePath) {
        log.debug("R : Attempting to load string from file: {}", relativePath);
        try {
            String filePath = Objects.requireNonNull(
                    R.class.getResource(relativePath),
                    "R : File resource not found: " + relativePath
            ).toExternalForm();
            log.info("R : Successfully loaded string file: {}", relativePath);
            return filePath;
        } catch (NullPointerException e) {
            log.error("R : Failed to load string file: {} - Resource not found", relativePath, e);
            throw e;
        } catch (Exception e) {
            log.error("R : Unexpected error while loading string file: {}", relativePath, e);
            throw e;
        }
    }
}