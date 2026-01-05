package com.enosistudio.docktailor.fx.fxdock.internal;

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
public class ConfigDocktailor {
    @Getter
    private DataConfigDocktailor dataConfigDocktailor = new DataConfigDocktailor();
    private final File cacheFile;

    public ConfigDocktailor(String fileName) {
        this.cacheFile = new File(fileName);

        tryLoad().ifPresent(dataConfigDocktailor1 -> this.dataConfigDocktailor = dataConfigDocktailor1);
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
            // Create parent directory if necessary
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

        // Name of the configuration file used last time.
        private String lastUIConfigUsed = null;

        // List of different application windows found by reflection.
        private Set<Class<? extends IDockPane>> iControllerDockPane = new HashSet<>();
    }
}