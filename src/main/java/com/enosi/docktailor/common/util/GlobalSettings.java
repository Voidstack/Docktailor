package com.enosi.docktailor.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;


/**
 * Application-wide settings.
 */
// FIX remove unnecessary level of indirection
// Provider <- GlobalSettings <- instance <- ASettingsStore <- FxSettings
@Slf4j
public class GlobalSettings {
    @Getter
    private static final String DEFAULT_FILE = "docktailor_default.conf";
    @Getter
    private static final String FILE_1 = "docktailor_1.conf";
    @Getter
    private static final String FILE_2 = "docktailor_2.conf";
    @Getter
    private static final String FILE_3 = "docktailor_3.conf";
    @Setter
    private static GlobalSettingsProvider provider;
    private static ASettingsStore store;

    public static void setDefaultFileProvider() {
        setFileProvider(new File(DEFAULT_FILE));
    }

    /**
     * @param fileName : GlobalSettings.FILE_X
     */
    public static void setFileProvider(String fileName) {
        File file;
        try {
            file = new File(fileName);
        } catch (NullPointerException e) {
            log.info("Docktailor : Le fichier le configuration d'interface n'a pas été trouvé : {}", fileName);
            file = new File(DEFAULT_FILE);
        }
        setFileProvider(file);
    }

    /**
     * a convenience shortcut to set file-based provider and load the settings
     */
    private static void setFileProvider(File f) {
        FileSettingsProvider p = new FileSettingsProvider(f);
        p.loadQuiet();
        setProvider(p);
    }

    private static GlobalSettingsProvider provider() {
        if (provider == null) {
            throw new NullPointerException("GlobalSettings.setProvider()");
        }
        return provider;
    }


    @Deprecated // FIX move to ASettingsStore
    public static void save() {
        provider().save();
    }

    @Deprecated
    public static void save(String fileName) {
        provider().save(fileName);
    }

    @Deprecated
    public static void resetRuntime() {
        provider().resetRuntime();
    }


    @Deprecated // FIX move to ASettingsStore
    public static List<String> getKeys() {
        return provider().getKeys();
    }


    @Deprecated // FIX move to ASettingsStore
    public static String getString(String key) {
        return provider().getString(key);
    }


    @Deprecated // FIX move to ASettingsStore
    public static void setString(String key, String val) {
        set(key, val);
    }


    @Deprecated // FIX remove
    private static void set(String key, Object val) {
        String s = val == null ? null : val.toString();
        provider().setString(key, s);
    }

    @Deprecated // FIX move to ASettingsStore
    public static SStream getStream(String key) {
        return provider().getStream(key);
    }

    @Deprecated // FIX move to ASettingsStore
    public static void setStream(String key, SStream s) {
        provider().setStream(key, s);
    }

    public static ASettingsStore getASettingsStore() {
        if (store == null) {
            synchronized (GlobalSettings.class) {
                if (store == null) {
                    store = new ASettingsStore() {
                        @Override
                        public void setString(String key, String val) {
                            GlobalSettings.setString(key, val);
                        }

                        @Override
                        public void setStream(String key, SStream stream) {
                            GlobalSettings.setStream(key, stream);
                        }

                        @Override
                        public void save() {
                            GlobalSettings.save();
                        }

                        public void save(String fileName) {
                            GlobalSettings.save(fileName);
                        }

                        @Override
                        public void resetRuntime() {
                            GlobalSettings.resetRuntime();
                        }

                        @Override
                        public String getString(String key) {
                            return GlobalSettings.getString(key);
                        }

                        @Override
                        public SStream getStream(String key) {
                            return GlobalSettings.getStream(key);
                        }
                    };
                }
            }
        }
        return store;
    }
}
