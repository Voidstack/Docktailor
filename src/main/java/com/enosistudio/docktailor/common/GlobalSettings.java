package com.enosistudio.docktailor.common;

import com.enosistudio.docktailor.fxdock.internal.ServiceDocktailor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * Application-wide settings.
 */
// FIX remove unnecessary level of indirection
// Provider <- GlobalSettings <- instance <- ASettingsStore <- FxSettings
@Slf4j
@Singleton
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class GlobalSettings extends AGlobalSettings {
    @Setter
    private SettingsProviderBase provider;

    private static GlobalSettings instance;

    public static GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return GlobalSettings.instance;
    }

    public void setDefaultFileProvider() {
        setFileProvider(new File(ServiceDocktailor.getDefaultUiFile()));
    }

    /**
     * @param fileName : GlobalSettings.FILE_X
     */
    public void setFileProvider(String fileName) {
        File file;
        try {
            file = new File(fileName);
        } catch (NullPointerException e) {
            log.info("Docktailor : Le fichier le configuration d'interface n'a pas été trouvé : {}", fileName);
            file = new File(ServiceDocktailor.getDefaultUiFile());
        }
        setFileProvider(file);
    }

    /**
     * a convenience shortcut to set file-based provider and load the settings
     */
    private void setFileProvider(File f) {
        FileSettingsProvider p = new FileSettingsProvider(f);
        p.loadQuiet();
        setProvider(p);
    }

    @Override
    public void setStream(String key, SStream stream) {
        provider.setStream(key, stream);
    }

    @Override
    public SStream getStream(String key) {
        return provider.getStream(key);
    }

    @Override
    public void setString(String key, String val) {
        provider.setString(key, val);
    }

    @Override
    public String getString(String key) {
        return provider.getString(key);
    }

    @Override
    public void save() {
        provider.save();
    }

    @Override
    public void save(String fileName) {
        provider.save(fileName);
    }

    @Override
    public void resetRuntime() {
        provider.resetRuntime();
    }
}