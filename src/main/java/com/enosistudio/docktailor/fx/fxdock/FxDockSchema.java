package com.enosistudio.docktailor.fx.fxdock;

import com.enosistudio.docktailor.common.AGlobalSettings;
import com.enosistudio.docktailor.common.SStream;
import com.enosistudio.docktailor.fx.FxSettingsSchema;
import com.enosistudio.docktailor.fx.WindowMonitor;
import com.enosistudio.docktailor.fx.fxdock.internal.*;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

/**
 * FxDock framework schema for the layout storage. Provides methods for saving, loading, and managing the layout of
 * dockable panes and windows.
 */
@Slf4j(topic = "FxDockSchema")
public abstract class FxDockSchema extends FxSettingsSchema {
    private static final String NAME_PANE = ".P";
    private static final String NAME_TAB = ".T";
    private static final String NAME_SPLIT = ".S";

    private static final String SUFFIX_CONTENT = ".layout";
    private static final String SUFFIX_SELECTED_TAB = ".tab";
    private static final String SUFFIX_SPLITS = ".splits";
    private static final String TYPE_EMPTY = "E";
    private static final String TYPE_PANE = "P";
    private static final String TYPE_HSPLIT = "H";
    private static final String TYPE_VSPLIT = "V";
    private static final String TYPE_TAB = "T";

    /**
     * Constructs an FxDockSchema with the specified global settings.
     *
     * @param s the global settings to use
     */
    protected FxDockSchema(AGlobalSettings s) {
        super(s);
    }

    /**
     * Creates a new FxDockWindow with the specified name.
     *
     * @param name the name of the window
     * @return the created FxDockWindow
     */
    @Override
    public abstract FxDockWindow createWindow(String name);

    /**
     * Creates a default FxDockWindow.
     *
     * @return the created default FxDockWindow
     */
    @Override
    public abstract FxDockWindow createDefaultWindow();

    /**
     * Creates a new FxDockPane with the specified type.
     *
     * @param type the type of the pane
     * @return the created FxDockPane
     */
    public abstract FxDockPane createPane(String type);

    /**
     * Restores the local settings of a window, including its content and layout.
     *
     * @param w the window to restore
     * @param m the window monitor associated with the window
     */
    @Override
    protected void restoreWindowLocal(Window w, WindowMonitor m) {
        super.restoreWindowLocal(w, m);

        if (w instanceof FxDockWindow dw) {
            String prefix = FX_PREFIX + m.getId();
            Node n = loadDockWindowContent(prefix);
            if (n != null) {
                dw.setContent(n);
                restoreContent(prefix, n);
            }
        }
    }

    /**
     * Stores the local settings of a window, including its content and layout.
     *
     * @param w the window to store
     * @param m the window monitor associated with the window
     */
    @Override
    protected void storeWindowLocal(Window w, WindowMonitor m) {
        super.storeWindowLocal(w, m);

        if (w instanceof FxDockWindow dw) {
            String prefix = FX_PREFIX + m.getId();
            Node n = dw.getContent();
            saveDockWindowContent(prefix, n);
            storeContent(prefix, n);
        }
    }

    /**
     * Loads the content of a dock window from the storage.
     *
     * @param prefix the prefix used to identify the content in storage
     * @return the loaded content as a Node
     */
    protected Node loadDockWindowContent(String prefix) {
        SStream s = store().getStream(prefix + SUFFIX_CONTENT);
        return loadContentRecursively(s);
    }

    /**
     * Saves the content of a dock window to the storage.
     *
     * @param prefix the prefix used to identify the content in storage
     * @param n      the content to save
     */
    protected void saveDockWindowContent(String prefix, Node n) {
        SStream s = new SStream();
        saveContentRecursively(s, n);
        store().setStream(prefix + SUFFIX_CONTENT, s);
    }

    /**
     * Loads a split pane from the storage.
     *
     * @param s  the stream containing the split pane data
     * @param or the orientation of the split pane
     * @return the loaded FxDockSplitPane
     */
    protected FxDockSplitPane loadSplit(SStream s, Orientation or) {
        FxDockSplitPane sp = new FxDockSplitPane();
        sp.setOrientation(or);
        int sz = s.nextInt();
        for (int i = 0; i < sz; i++) {
            Node ch = loadContentRecursively(s);
            sp.addPane(ch);
        }
        return sp;
    }

    /**
     * Recursively loads content from the storage.
     *
     * @param s the stream containing the content data
     * @return the loaded content as a Node
     */
    protected Node loadContentRecursively(SStream s) {
        String t = s.nextString();
        if (t == null) {
            return null;
        } else if (TYPE_PANE.equals(t)) {
            String type = s.nextString();
            return createPane(type);
        } else if (TYPE_HSPLIT.equals(t)) {
            return loadSplit(s, Orientation.HORIZONTAL);
        } else if (TYPE_VSPLIT.equals(t)) {
            return loadSplit(s, Orientation.VERTICAL);
        } else if (TYPE_TAB.equals(t)) {
            FxDockTabPane tp = new FxDockTabPane();
            int sz = s.nextInt();
            for (int i = 0; i < sz; i++) {
                Node ch = loadContentRecursively(s);
                tp.addTab(ch);
            }
            return tp;
        } else if (TYPE_EMPTY.equals(t)) {
            return new FxDockEmptyPane();
        } else {
            return null;
        }
    }

    /**
     * Recursively saves content to the storage.
     *
     * @param s the stream to save the content to
     * @param n the content to save
     */
    protected void saveContentRecursively(SStream s, Node n) {
        if (n == null) {
            // Do nothing for null nodes
        } else if (n instanceof FxDockPane p) {
            String type = p.getDockPaneType();
            s.add(TYPE_PANE);
            s.add(type);
        } else if (n instanceof FxDockSplitPane p) {
            int ct = p.getPaneCount();
            Orientation or = p.getOrientation();
            s.add(or == Orientation.HORIZONTAL ? TYPE_HSPLIT : TYPE_VSPLIT);
            s.add(ct);
            for (Node ch : p.getPanes()) {
                saveContentRecursively(s, ch);
            }
        } else if (n instanceof FxDockTabPane p) {
            int ct = p.getTabCount();
            s.add(TYPE_TAB);
            s.add(ct);
            for (Node ch : p.getPanes()) {
                saveContentRecursively(s, ch);
            }
        } else if (n instanceof FxDockEmptyPane) {
            s.add(TYPE_EMPTY);
        } else {
            throw new IllegalArgumentException("?" + n);
        }
    }

    /**
     * Constructs the path for a node based on its hierarchy.
     *
     * @param prefix the prefix for the path
     * @param n      the node to construct the path for
     * @param suffix the suffix for the path
     * @return the constructed path as a String
     */
    protected String getPath(String prefix, Node n, String suffix) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);
        getPathRecursive(sb, n);
        if (suffix != null) {
            sb.append(suffix);
        }
        return sb.toString();
    }

    /**
     * Recursively constructs the path for a node based on its hierarchy.
     *
     * @param sb   the StringBuilder to append the path to
     * @param node the node to construct the path for
     */
    protected void getPathRecursive(StringBuilder sb, Node node) {
        Node n = DockTools.getParent(node);
        if (n != null) {
            getPathRecursive(sb, n);
            if (n instanceof FxDockSplitPane p) {
                int ix = p.indexOfPane(node);
                sb.append('.');
                sb.append(ix);
            } else if (n instanceof FxDockTabPane p) {
                int ix = p.indexOfTab(node);
                sb.append('.');
                sb.append(ix);
            }
        }
        if (node instanceof FxDockRootPane) {
            // Root pane does not append to the path
        } else if (node instanceof FxDockSplitPane) {
            sb.append(NAME_SPLIT);
        } else if (node instanceof FxDockTabPane) {
            sb.append(NAME_TAB);
        } else if (node instanceof FxDockPane) {
            sb.append(NAME_PANE);
        } else {
            throw new IllegalArgumentException("?" + node);
        }
    }

    /**
     * Restores the content settings for a node, including split positions and bindings.
     *
     * @param prefix the prefix for the content settings
     * @param n      the node to restore settings for
     */
    protected void restoreContent(String prefix, Node n) {
        if (n != null) {
            if (n instanceof FxDockPane) {
                // Load pane-specific settings
            } else if (n instanceof FxDockSplitPane p) {
                for (Node ch : p.getPanes()) {
                    restoreContent(prefix, ch);
                }
                Platform.runLater(() -> loadSplitPaneSettings(prefix, p));
            } else if (n instanceof FxDockTabPane p) {
                loadTabPaneSettings(prefix, p);
                for (Node ch : p.getPanes()) {
                    restoreContent(prefix, ch);
                }
            }
        }
    }

    /**
     * Stores the content settings for a node, including split positions and bindings.
     *
     * @param prefix the prefix for the content settings
     * @param n      the node to store settings for
     */
    protected void storeContent(String prefix, Node n) {
        if (n != null) {
            storeNode(n);
            if (n instanceof FxDockPane) {
                // Save pane-specific settings
            } else if (n instanceof FxDockSplitPane p) {
                saveSplitPaneSettings(prefix, p);
                for (Node ch : p.getPanes()) {
                    storeContent(prefix, ch);
                }
            } else if (n instanceof FxDockTabPane p) {
                saveTabPaneSettings(prefix, p);
                for (Node ch : p.getPanes()) {
                    storeContent(prefix, ch);
                }
            }
        }
    }

    /**
     * Saves the divider positions of a split pane to the storage.
     *
     * @param prefix the prefix for the split pane settings
     * @param p      the split pane to save settings for
     */
    protected void saveSplitPaneSettings(String prefix, FxDockSplitPane p) {
        double[] divs = p.getDividerPositions();
        SStream s = new SStream();
        s.addAll(divs);
        String k = getPath(prefix, p, SUFFIX_SPLITS);
        store().setStream(k, s);
    }

    /**
     * Loads the divider positions of a split pane from the storage.
     *
     * @param prefix the prefix for the split pane settings
     * @param p      the split pane to load settings for
     */
    protected void loadSplitPaneSettings(String prefix, FxDockSplitPane p) {
        String k = getPath(prefix, p, SUFFIX_SPLITS);
        SStream s = store().getStream(k);
        int ct = s.size();
        if (p.getDividers().size() == ct) {
            for (int i = 0; i < ct; i++) {
                double pos = s.nextDouble();
                p.setDividerPosition(i, pos);
            }
        }
    }

    /**
     * Saves the selected tab index of a tab pane to the storage.
     *
     * @param prefix the prefix for the tab pane settings
     * @param p      the tab pane to save settings for
     */
    protected void saveTabPaneSettings(String prefix, FxDockTabPane p) {
        int ix = p.getSelectedTabIndex();
        String k = getPath(prefix, p, SUFFIX_SELECTED_TAB);
        store().setInt(k, ix);
    }

    /**
     * Loads the selected tab index of a tab pane from the storage.
     *
     * @param prefix the prefix for the tab pane settings
     * @param p      the tab pane to load settings for
     */
    protected void loadTabPaneSettings(String prefix, FxDockTabPane p) {
        String k = getPath(prefix, p, SUFFIX_SELECTED_TAB);
        int ix = store().getInt(k, 0);
        p.select(ix);
    }
}