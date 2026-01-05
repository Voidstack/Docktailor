package com.enosistudio.docktailor.common;

import com.enosistudio.docktailor.DocktailorService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * Application-wide settings.
 */
// FIX remove unnecessary level of indirection
// Provider <- GlobalSettings <- instance <- ASettingsStore <- FxSettings
@Setter
@Slf4j
public final class GlobalSettings extends AGlobalSettings {
    private ASettingsProviderBase provider;

    /**
     * @param fileName : GlobalSettings.FILE_X
     */
    public void setFileProvider(String fileName) {
        File file;
        try {
            file = new File(fileName);
        } catch (NullPointerException e) {
            log.info("Docktailor: UI configuration file not found: {}", fileName);
            file = new File(DocktailorService.getInstance().getDefaultUiFile());
        }
        setFileProvider(file);
    }

    /**
     * a convenience shortcut to set file-based provider and load the settings
     */
    private void setFileProvider(File f) {
        FileASettingsProvider p = new FileASettingsProvider(f);
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
        // Protect default UI file from being overwritten
        if (isUsingDefaultFile()) {
            String firstConfigFile = getFirstPredefinedConfigFile();
            log.info("Docktailor: Default UI file is read-only. Redirecting save to: {}", firstConfigFile);

            // Switch to first predefined config file
            setFileProvider(firstConfigFile);

            // Update last used config
            DocktailorService.getInstance().setLastUIConfigUsed(firstConfigFile);
        }
        provider.save();
    }

    @Override
    public void save(String fileName) {
        // Protect default UI file from being overwritten
        if (isDefaultFile(fileName)) {
            String firstConfigFile = getFirstPredefinedConfigFile();
            log.warn("Docktailor: Cannot save to default UI file. Redirecting to: {}", firstConfigFile);
            provider.save(firstConfigFile);

            // Update last used config
            DocktailorService.getInstance().setLastUIConfigUsed(firstConfigFile);
            return;
        }
        provider.save(fileName);
    }

    /**
     * Check if currently using the default UI file
     */
    private boolean isUsingDefaultFile() {
        if (provider instanceof FileASettingsProvider fileProvider) {
            File currentFile = fileProvider.getFile();
            File defaultFile = new File(DocktailorService.getInstance().getDefaultUiFile());

            try {
                return currentFile.getCanonicalPath().equals(defaultFile.getCanonicalPath());
            } catch (Exception e) {
                log.error("Docktailor: Error comparing file paths", e);
                return false;
            }
        }
        return false;
    }

    /**
     * Check if the given filename is the default UI file
     */
    private boolean isDefaultFile(String fileName) {
        if(fileName == null) return true;
        try {
            File targetFile = new File(fileName);
            File defaultFile = new File(DocktailorService.getInstance().getDefaultUiFile());
            return targetFile.getCanonicalPath().equals(defaultFile.getCanonicalPath());
        } catch (Exception e) {
            log.error("Docktailor: Error comparing file paths", e);
            return false;
        }
    }

    /**
     * Get the first predefined config file
     */
    private String getFirstPredefinedConfigFile() {
        return DocktailorService.getInstance()
                .getPredefinedUiFiles()
                .values()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No predefined config files available"));
    }

    @Override
    public void resetRuntime() {
        provider.resetRuntime();
    }
}