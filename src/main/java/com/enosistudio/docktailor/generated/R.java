package com.enosistudio.docktailor.generated;

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
    
    public static final class com extends RFolder {
        public static final RFolder _self = new com();
        private com() { super("com", "com"); }
        
        public static final class enosistudio extends RFolder {
            public static final RFolder _self = new enosistudio();
            private enosistudio() { super("enosistudio", "com/enosistudio"); }
            
            public static final class docktailor extends RFolder {
                public static final RFolder _self = new docktailor();
                private docktailor() { super("docktailor", "com/enosistudio/docktailor"); }
                public static final RFile docktailorDefaultUi = new RFile("com/enosistudio/docktailor/docktailor_default.ui");
                
                public static final class css extends RFolder {
                    public static final RFolder _self = new css();
                    private css() { super("css", "com/enosistudio/docktailor/css"); }
                    public static final RFile mainCss = new RFile("com/enosistudio/docktailor/css/main.css");
                    public static final RFile modenaCss = new RFile("com/enosistudio/docktailor/css/modena.css");
                    
                    public static final class component extends RFolder {
                        public static final RFolder _self = new component();
                        private component() { super("component", "com/enosistudio/docktailor/css/component"); }
                        public static final RFile betterradiobuttonCss = new RFile("com/enosistudio/docktailor/css/component/betterRadioButton.css");
                        public static final RFile buttonCss = new RFile("com/enosistudio/docktailor/css/component/button.css");
                        public static final RFile cartotooltipCss = new RFile("com/enosistudio/docktailor/css/component/cartoTooltip.css");
                        public static final RFile checkboxCss = new RFile("com/enosistudio/docktailor/css/component/checkbox.css");
                        public static final RFile colorpickerCss = new RFile("com/enosistudio/docktailor/css/component/colorPicker.css");
                        public static final RFile listviewCss = new RFile("com/enosistudio/docktailor/css/component/listView.css");
                        public static final RFile menuCss = new RFile("com/enosistudio/docktailor/css/component/menu.css");
                        public static final RFile scrollBarCss = new RFile("com/enosistudio/docktailor/css/component/scroll-bar.css");
                        public static final RFile separatorCss = new RFile("com/enosistudio/docktailor/css/component/separator.css");
                        public static final RFile splitPaneCss = new RFile("com/enosistudio/docktailor/css/component/split-pane.css");
                        public static final RFile tabCss = new RFile("com/enosistudio/docktailor/css/component/tab.css");
                        public static final RFile tableviewCss = new RFile("com/enosistudio/docktailor/css/component/tableView.css");
                        public static final RFile toogleCss = new RFile("com/enosistudio/docktailor/css/component/toogle.css");
                        public static final RFile toolbarCss = new RFile("com/enosistudio/docktailor/css/component/toolbar.css");
                        public static final RFile treeviewCss = new RFile("com/enosistudio/docktailor/css/component/treeView.css");
                    }
                }
                
                public static final class fontawesome extends RFolder {
                    public static final RFolder _self = new fontawesome();
                    private fontawesome() { super("fontawesome", "com/enosistudio/docktailor/fontawesome"); }
                    public static final RFile addressBookRegularSvg = new RFile("com/enosistudio/docktailor/fontawesome/address-book-regular.svg");
                    public static final RFile barsSolidSvg = new RFile("com/enosistudio/docktailor/fontawesome/bars-solid.svg");
                    public static final RFile circleInfoSvg = new RFile("com/enosistudio/docktailor/fontawesome/circle-info.svg");
                    public static final RFile layerGroupSolidSvg = new RFile("com/enosistudio/docktailor/fontawesome/layer-group-solid.svg");
                    public static final RFile magnifyingGlassSvg = new RFile("com/enosistudio/docktailor/fontawesome/magnifying-glass.svg");
                    public static final RFile mountainSolidSvg = new RFile("com/enosistudio/docktailor/fontawesome/mountain-solid.svg");
                    public static final RFile terminalSolidSvg = new RFile("com/enosistudio/docktailor/fontawesome/terminal-solid.svg");
                }
                
                public static final class fxml extends RFolder {
                    public static final RFolder _self = new fxml();
                    private fxml() { super("fxml", "com/enosistudio/docktailor/fxml"); }
                    public static final RFile personneFxml = new RFile("com/enosistudio/docktailor/fxml/personne.fxml");
                }
                
                public static final class icons extends RFolder {
                    public static final RFolder _self = new icons();
                    private icons() { super("icons", "com/enosistudio/docktailor/icons"); }
                    public static final RFile logoPng = new RFile("com/enosistudio/docktailor/icons/logo.png");
                }
            }
        }
    }
}
