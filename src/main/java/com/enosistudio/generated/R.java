package com.enosistudio.generated;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;

import com.enosistudio.RFile;
import com.enosistudio.RFolder;
import org.jetbrains.annotations.Contract;

/**
 * Generated resource constants class.
 * Contains hierarchical access to all resource files and folders.
 */
@SuppressWarnings({"java:S101", "unused"})
public final class R {
    private R() {} // Utility class

    public static final RFile log4j2Xml = new RFile("log4j2.xml");
    
    public static final class css extends RFolder {
        public static final RFolder _self = new css();
        private css() { super("css", "css"); }
        public static final RFile mainCss = new RFile("css/main.css");
    }
    
    public static final class fxml extends RFolder {
        public static final RFolder _self = new fxml();
        private fxml() { super("fxml", "fxml"); }
        public static final RFile personneFxml = new RFile("fxml/personne.fxml");
    }
    
    public static final class images extends RFolder {
        public static final RFolder _self = new images();
        private images() { super("images", "images"); }
        
        public static final class icons extends RFolder {
            public static final RFolder _self = new icons();
            private icons() { super("icons", "images/icons"); }
            public static final RFile logoPng = new RFile("images/icons/logo.png");
        }
    }
}
