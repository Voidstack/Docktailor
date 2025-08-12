package com.enosi.docktailor.common.util;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;


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
            log.error("FileSettingsProvider : ", e);
        }
    }

    /**
     * ATTENTION : la méthode ne reset pas la save mais simplement les données au runtime. Important pour éviter des
     * bugs avec l'ouverture de fenêtre.
     */
    @Override
    protected void resetRuntimeSettings() {
        data.clear();
    }

    public void load() throws Exception {
        try {
            String s = CKit.readString(file);
            loadFromString(s);
        } catch (FileNotFoundException ignore) {
        }
    }


    public void loadQuiet() {
        try {
            load();
        } catch (Exception e) {
            log.error("FileSettingsProvider : ", e);
        }
    }


    public void load(File f) throws Exception {
        setFile(f);
        load();
    }


    public void loadQuiet(File f) {
        try {
            load(f);
        } catch (Exception e) {
            log.error("FileSettingsProvider : ", e);
        }
    }
}
