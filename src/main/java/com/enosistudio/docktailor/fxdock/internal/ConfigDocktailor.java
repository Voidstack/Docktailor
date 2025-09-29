package com.enosistudio.docktailor.fxdock.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j(topic = "ConfigDocktailor" )
final class ConfigDocktailor {
    @Getter
    private DataConfigDocktailor dataConfigDocktailor = new DataConfigDocktailor();
    private final File cacheFile;

    public ConfigDocktailor(String fileName) {
        String appData = System.getenv("APPDATA");
        if (appData == null) {
            // fallback si pas sur Windows (Linux/Mac)
            appData = System.getProperty("user.home");
        }

        this.cacheFile = new File(String.join(File.separator, appData, "Docktailor", fileName));
    }

    public Optional<DataConfigDocktailor> tryLoad() {
        if (!cacheFile.exists()) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            this.dataConfigDocktailor = ((DataConfigDocktailor) ois.readObject());

            log.info("Docktailor : Config loaded successfully from {}", cacheFile.getAbsolutePath());
            return Optional.of(this.dataConfigDocktailor);
        } catch (Exception e) {
            log.error("ConfigDocktailor : Error loading config : {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Saves the provided set of IControllerDockPane classes to the cache file. This method creates the parent directory
     * if it doesn't exist, then writes the data to the file using object serialization.
     */
    public void save() {
        try {
            // Crée le dossier parent si nécessaire
            File parent = cacheFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
                oos.writeObject(dataConfigDocktailor);
            }
            log.info("Docktailor : Config saved successfully at {}", cacheFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Docktailor : Error saving config: {}", e.getMessage());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class DataConfigDocktailor implements Serializable{
        @Serial
        private static final long serialVersionUID = 1L;

        // Nom du fichier de configuration utilisé pour la dernière fois.
        private String lastUIConfigUsed = "docktailor_default.conf";

        // List des différentes fenêtres de l'application trouvé par réflexion.
        private Set<Class<? extends IDockPane>> iControllerDockPane = new HashSet<>();
    }
}