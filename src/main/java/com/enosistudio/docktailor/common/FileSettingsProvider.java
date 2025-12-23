package com.enosistudio.docktailor.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * File-based Settings Provider.
 */
@Setter @Slf4j(topic = "FileSettingsProvider")
public class FileSettingsProvider extends SettingsProviderBase {
    private File file;

    public FileSettingsProvider(File f) {
        setFile(f);
    }

    @Override
    protected void saveSettings() {
        try {
            String s = asString();
            CKit.write(file, s);
        } catch (Exception e) {
            log.error("FileSettingsProvider : ", e);
        }
    }

    @Override
    protected void saveSettings(String fileName) {
        try {
            String s = asString();

            File target = (new File(fileName));
/*			FileSettingsProvider p = new FileSettingsProvider(target);
			p.loadQuiet();*/
            CKit.write(target, s);
        } catch (Exception e) {
            log.error("FileSettingsProvider : {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * WARNING: this method does not reset the save but only the runtime data. Important to avoid
     * bugs with window opening.
     */
    @Override
    protected void resetRuntimeSettings() {
        data.clear();
    }

    public void load() throws IOException {
        try {
            String s = CKit.readString(file);
            loadFromString(s);
        } catch (FileNotFoundException e) {
            log.error("FileSettingsProvider : {}", file.getAbsolutePath(), e);
        }
    }

    public void loadQuiet() {
        try {
            load();
        } catch (Exception e) {
            log.error("FileSettingsProvider : ", e);
        }
    }
}
