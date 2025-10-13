package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.SStream;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Shape;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Stores and restores the UI state.
 */
// TODO consider making it generic <ActualWindow>
@Slf4j(topic = "FxSettingsSchema")
public abstract class FxSettingsSchema {
    protected static final String FX_PREFIX = "FX.";
    private static final String SFX_WINDOWS = "WINDOWS";
    private static final String SFX_DIVIDERS = ".DIVS";
    private static final String SFX_EXPANDED = ".EXP";
    private static final String SFX_SELECTION = ".SEL";
    private static final String SFX_SETTINGS = ".SETTINGS";
    private static final String SORT_ASCENDING = "A";
    private static final String SORT_DESCENDING = "D";
    private static final String SORT_NONE = "N";
    private static final String WINDOW_FULLSCREEN = "F";
    private static final String WINDOW_MAXIMIZED = "X";
    private static final String WINDOW_ICONIFIED = "I";
    private static final String WINDOW_NORMAL = "N";
    private final AGlobalSettings globalSettings;

    protected FxSettingsSchema(AGlobalSettings globalSettings) {
        this.globalSettings = globalSettings;
    }

    public abstract FxDockWindow createDefaultWindow();

    protected abstract FxDockWindow createWindow(String name);

    protected void loadWindowContent(WindowMonitor m, Stage w) {
        // :) content
    }

    public void storeWindow(Window w) {
        log.debug(FxTools.describe(w));

        if (FX.isSkipSettings(w)) {
            return;
        }

        WindowMonitor m = WindowMonitor.forWindow(w);
        if (m != null) {
            storeWindowLocal(w, m);
        }
    }


    protected void storeWindowLocal(Window w, WindowMonitor m) {
        double x = m.getX();
        double y = m.getY();
        double width = m.getW();
        double height = m.getH();

        SStream ss = new SStream();
        ss.add(x);
        ss.add(y);
        ss.add(width);
        ss.add(height);

        if (w instanceof Stage s) {
            if (s.isFullScreen()) {
                ss.add(WINDOW_FULLSCREEN);
            } else if (s.isMaximized()) {
                ss.add(WINDOW_MAXIMIZED);
            } else if (s.isIconified()) {
                ss.add(WINDOW_ICONIFIED);
            } else {
                ss.add(WINDOW_NORMAL);
            }
        }

        globalSettings.setStream(FX_PREFIX + m.getId(), ss);

        LocalSettings s = LocalSettings.getOrNull(w);
        if (s != null) {
            String k = FX_PREFIX + m.getId() + SFX_SETTINGS;
            s.saveValues(k, globalSettings);
        }

        Node n = w.getScene().getRoot();
        storeNode(n);
    }


    public void restoreWindow(Window w) {
        log.debug(FxTools.describe(w));

        if (w instanceof PopupWindow) {
            return;
        }

        if (FX.isSkipSettings(w)) {
            return;
        } else if (w instanceof Stage s) {
            if (s.getModality() != Modality.NONE) {
                return;
            }
        }

        WindowMonitor m = WindowMonitor.forWindow(w);
        if (m != null) {
            restoreWindowLocal(w, m);

            LocalSettings s = LocalSettings.getOrNull(w);
            if (s != null) {
                String k = FX_PREFIX + m.getId() + SFX_SETTINGS;
                s.loadValues(k, globalSettings);
            }

            Node n = w.getScene().getRoot();
        }
    }


    protected void restoreWindowLocal(Window w, WindowMonitor m) {
        SStream ss = globalSettings.getStream(FX_PREFIX + m.getId());
        if (ss != null) {
            double x = ss.nextDouble(-1);
            double y = ss.nextDouble(-1);
            double width = ss.nextDouble(-1);
            double height = ss.nextDouble(-1);
            String state = ss.nextString(WINDOW_NORMAL);

            if ((width > 0) && (height > 0)) {
                if (FX.isValidCoordinates(x, y)) {
                    // iconified windows have (x,y) of -32000 for some reason
                    // their coordinates are essentially lost (unless there is a way to get them in FX)
                    w.setX(x);
                    w.setY(y);
                }

                if (w instanceof Stage s) {
                    if (s.isResizable()) {
                        w.setWidth(width);
                        w.setHeight(height);
                    } else {
                        width = w.getWidth();
                        height = w.getHeight();
                    }

                    switch (state) {
                        // Platform.runLater obligatoire sinon bug
                        // surtout pour "Maximized"
                        case WINDOW_FULLSCREEN:
                            Platform.runLater(() -> s.setFullScreen(true));
                            break;
                        case WINDOW_MAXIMIZED:
                            Platform.runLater(() -> s.setMaximized(true));
                            break;
                        case WINDOW_ICONIFIED:
                            Platform.runLater(() -> s.setIconified(true));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }


    public void storeNode(Node n) {
        if (n == null) {
            return;
        }

        if (FX.isSkipSettings(n)) {
            return;
        }

        String name = computeName(n);
        if (name == null) {
            return;
        }

        LocalSettings s = LocalSettings.getOrNull(n);
        if (s != null) {
            String k = name + SFX_SETTINGS;
            s.saveValues(k, globalSettings);
        }

        if (n instanceof CheckBox cb) {
            storeCheckBox(cb, name);
        } else if (n instanceof ComboBox cb) {
            storeComboBox(cb, name);
        } else if (n instanceof ListView v) {
            storeListView(v, name);
        } else if (n instanceof SplitPane sp) {
            storeSplitPane(sp, name);
        } else if (n instanceof ScrollPane sp) {
            storeNode(sp.getContent());
        } else if (n instanceof TitledPane tp) {
            storeTitledPane(tp, name);
        } else if (n instanceof TableView t) {
            // storeTableView(t, name);
        } else if (n instanceof TabPane t) {
            storeTabPane(t, name);
        } else {
            List<Node> nodes = listNodes(n);
            if (nodes != null) {
                for (Node ch : nodes) {
                    storeNode(ch);
                }
            }
        }
    }

    private List<Node> listNodes(Node n) {
        if (n instanceof ToolBar p) {
            return p.getItems();
        } else if (n instanceof Parent p) {
            return p.getChildrenUnmodifiable();
        }
        return null;
    }


    protected boolean handleNullScene(Node node) {
        if (node == null) {
            return true;
        } else if (node.getScene() == null) {
            node.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> src, Scene old, Scene sc) {
                    if (sc != null) {
                        Window w = sc.getWindow();
                        if (w != null) {
                            node.sceneProperty().removeListener(this);
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }


    protected String computeName(Node n) {
        WindowMonitor m = WindowMonitor.forNode(n);
        if (m != null) {
            StringBuilder sb = new StringBuilder();
            if (collectNames(sb, n)) {
                String id = m.getId();
                return id + sb;
            }
        }
        return null;
    }


    // returns false if Node should be ignored
    // n is not null
    protected boolean collectNames(StringBuilder sb, Node n) {
        if (n instanceof MenuBar || n instanceof Shape || n instanceof ImageView) {
            return false;
        }

        Parent p = n.getParent();
        if (p != null && !collectNames(sb, p)) {
            return false;
        }

        String name = getNodeName(n);
        if (name == null) {
            return false;
        }

        sb.append('.');
        sb.append(name);
        return true;
    }


    protected String getNodeName(Node n) {
        String name = FX.getName(n);
        if (name != null) {
            return name;
        }

        if (n instanceof Pane) {
            if (n instanceof AnchorPane) {
                return "AnchorPane";
            } else if (n instanceof BorderPane) {
                return "BorderPane";
            } else if (n instanceof DialogPane) {
                return "DialogPane";
            } else if (n instanceof FlowPane) {
                return "FlowPane";
            } else if (n instanceof GridPane) {
                return "GridPane";
            } else if (n instanceof HBox) {
                return "HBox";
            } else if (n instanceof StackPane) {
                return "StackPane";
            } else if (n instanceof TextFlow) {
                return null;
            } else if (n instanceof TilePane) {
                return "TilePane";
            } else if (n instanceof VBox) {
                return "VBox";
            } else {
                return "Pane";
            }
        } else if (n instanceof Control) {
            return n.getClass().getSimpleName();
        } else if (n instanceof Group) {
            return "Group";
        } else if (n instanceof Region) {
            return "Region";
        }
        return null;
    }


    protected void storeCheckBox(CheckBox n, String name) {
        boolean sel = n.isSelected();
        globalSettings.setBoolean(FX_PREFIX + name, sel);
    }


    protected void restoreCheckBox(CheckBox n, String name) {
        Boolean sel = globalSettings.getBoolean(FX_PREFIX + name);
        if (sel != null) {
            if (!n.selectedProperty().isBound()) {
                n.setSelected(sel);
            }
        }
    }


    protected void storeComboBox(ComboBox n, String name) {
        if (n.getSelectionModel() != null) {
            int ix = n.getSelectionModel().getSelectedIndex();
            if (ix >= 0) {
                globalSettings.setInt(FX_PREFIX + name, ix);
            }
        }
    }


    protected void restoreComboBox(ComboBox n, String name) {
        if (n.getSelectionModel() != null) {
            int ix = globalSettings.getInt(FX_PREFIX + name, -1);
            if ((ix >= 0) && (ix < n.getItems().size())) {
                n.getSelectionModel().select(ix);
            }
        }
    }


    protected void storeListView(ListView n, String name) {
        if (n.getSelectionModel() != null) {
            int ix = n.getSelectionModel().getSelectedIndex();
            if (ix >= 0) {
                globalSettings.setInt(FX_PREFIX + name, ix);
            }
        }
    }


    protected void restoreListView(ListView n, String name) {
        if (n.getSelectionModel() != null) {
            int ix = globalSettings.getInt(FX_PREFIX + name, -1);
            if ((ix >= 0) && (ix < n.getItems().size())) {
                n.getSelectionModel().select(ix);
            }
        }
    }


    protected void storeSplitPane(SplitPane sp, String name) {
        double[] div = sp.getDividerPositions();
        SStream ss = new SStream();
        ss.add(div.length);
        ss.addAll(div);
        globalSettings.setStream(FX_PREFIX + name + SFX_DIVIDERS, ss);

        for (Node ch : sp.getItems()) {
            storeNode(ch);
        }
    }


    protected void restoreSplitPane(SplitPane sp, String name) {
        SStream ss = globalSettings.getStream(FX_PREFIX + name + SFX_DIVIDERS);
        if (ss != null) {
            int sz = ss.nextInt(-1);
            if (sz > 0) {
                double[] divs = new double[sz];
                for (int i = 0; i < sz; i++) {
                    double v = ss.nextDouble(-1);
                    if (v < 0) {
                        return;
                    }
                    divs[i] = v;
                }
                // FIX must run later because of FX split pane inability to set divider positions exactly
                // it's likely a bug in SplitPane
                sp.setDividerPositions(divs);
                FX.later(() -> {
                    sp.setDividerPositions(divs);
                });
            }
        }

        for (Node ch : sp.getItems()) {
        }
    }

    protected void storeTabPane(TabPane p, String name) {
        // selection
        int ix = p.getSelectionModel().getSelectedIndex();
        globalSettings.setInt(FX_PREFIX + name + SFX_SELECTION, ix);

        // content
        var sm = p.getSelectionModel();
        if (sm != null) {
            var item = sm.getSelectedItem();
            if (item != null) {
                storeNode(item.getContent());
            }
        }
    }

    protected void restoreTabPane(TabPane p, String name) {
        // selection
        int ix = globalSettings.getInt(FX_PREFIX + name + SFX_SELECTION, -1);
        if (ix >= 0) {
            if (ix < p.getTabs().size()) {
                p.getSelectionModel().select(ix);
            }
        }

        // content
        var sm = p.getSelectionModel();
        if (sm != null) {
            var item = sm.getSelectedItem();
        }
    }


    protected void storeTitledPane(TitledPane p, String name) {
        globalSettings.setBoolean(FX_PREFIX + name + SFX_EXPANDED, p.isExpanded());

        storeNode(p.getContent());
    }


    protected void restoreTitledPane(TitledPane p, String name) {
        boolean expanded = globalSettings.getBoolean(FX_PREFIX + name + SFX_EXPANDED, true);
        p.setExpanded(expanded);
    }


    /**
     * Opens all previously opened windows using the specified generator. Open a default window when no windows has been
     * opened from the settings.
     */
    public int openLayout() {
        // ensure WinMonitor is initialized
        WindowMonitor.forWindow(null);

        // numEntries,name,id,... in reverse order
        SStream st = globalSettings.getStream(FX_PREFIX + SFX_WINDOWS);
        int count = 0;

        int numEntries = st.nextInt(-1);
        if (numEntries > 0) {
            for (int i = 0; i < numEntries; i++) {
                String name = st.nextString();
                String id = st.nextString();
                FxDockWindow w = createWindow(name);
                if (w != null) {
                    // ensure that the window monitor is created with the right id
                    WindowMonitor m = WindowMonitor.forWindow(w, id);

                    loadWindowContent(m, w);

                    if (!w.isShowing()) {
                        w.open();
                    }

                    count++;
                }
            }
        }

        if (count == 0) {
            FxDockWindow w = createDefaultWindow();
            if (!w.isShowing()) {
                w.open();
            }
            count++;
        }

        // #001 : Fixed
        FxFramework.getSchema().resetRuntime();

        return count;
    }

    /**
     * WindowMonitor.getWindowStack().forEach(Window::hide);
     */
    public void closeLayout() {
        WindowMonitor.getWindowStack().forEach(Window::hide);
    }

    public void storeLayout(String fileName) {
        SStream ss = new SStream();
        List<Window> ws = WindowMonitor.getWindowStack();

        int sz = ws.size();
        ss.add(sz);

        for (int i = 0; i < sz; i++) {
            Window w = ws.get(i);
            FxFramework.store(w);

            String name = FX.getName(w);
            String id = WindowMonitor.forWindow(w).getIDPart();

            ss.add(name);
            ss.add(id);
        }

        globalSettings.setStream(FX_PREFIX + SFX_WINDOWS, ss);

        globalSettings.save(fileName);
    }

    public void storeLayout() {
        SStream ss = new SStream();
        List<Window> ws = WindowMonitor.getWindowStack();

        int sz = ws.size();
        ss.add(sz);

        for (int i = 0; i < sz; i++) {
            Window w = ws.get(i);
            FxFramework.store(w);

            String name = FX.getName(w);
            String id = WindowMonitor.forWindow(w).getIDPart();

            ss.add(name);
            ss.add(id);
        }

        globalSettings.setStream(FX_PREFIX + SFX_WINDOWS, ss);

        globalSettings.save();
    }

    public void save() {
        globalSettings.save();
    }

    public void resetRuntime() {
        globalSettings.resetRuntime();
    }

    protected AGlobalSettings store() {
        return globalSettings;
    }
}