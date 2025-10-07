package com.enosistudio.generated;

import com.enosistudio.RFile;
import com.enosistudio.RFolder;

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
                
                public static final class css extends RFolder {
                    public static final RFolder _self = new css();
                    private css() { super("css", "com/enosistudio/docktailor/css"); }
                    public static final RFile mainCss = new RFile("com/enosistudio/docktailor/css/main.css");
                    public static final RFile modernaCss = new RFile("com/enosistudio/docktailor/css/modena.css");
                    
                    public static final class component extends RFolder {
                        public static final RFolder _self = new component();
                        private component() { super("component", "com/enosistudio/docktailor/css/component"); }
                        public static final RFile buttonCss = new RFile("com/enosistudio/docktailor/css/component/button.css");
                        public static final RFile menuCss = new RFile("com/enosistudio/docktailor/css/component/menu.css");
                        public static final RFile scrollBarCss = new RFile("com/enosistudio/docktailor/css/component/scroll-bar.css");
                        public static final RFile tabCss = new RFile("com/enosistudio/docktailor/css/component/tab.css");
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
                
                public static final class images extends RFolder {
                    public static final RFolder _self = new images();
                    private images() { super("images", "com/enosistudio/docktailor/images"); }
                }
                
                public static final class svg extends RFolder {
                    public static final RFolder _self = new svg();
                    private svg() { super("svg", "com/enosistudio/docktailor/svg"); }
                }
            }
        }
    }
}
