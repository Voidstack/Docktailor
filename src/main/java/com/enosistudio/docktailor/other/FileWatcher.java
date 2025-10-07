package com.enosistudio.docktailor.other;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FileWatcher {
    public static void watchFile(Path filePath, Runnable onChange) throws IOException {
        Path dir = filePath.getParent();
        String fileName = filePath.getFileName().toString();

        WatchService watchService = FileSystems.getDefault().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        AtomicLong lastTrigger = new AtomicLong(0);
        long debounceDelayMs = 300; // temps minimal entre 2 déclenchements

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed.getFileName().toString().equals(fileName)) {
                            long now = Instant.now().toEpochMilli();
                            if (now - lastTrigger.get() > debounceDelayMs) {
                                lastTrigger.set(now);
                                onChange.run();
                            }
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException ignored) {
            }
        }, "FileWatcherThread");

        thread.setDaemon(true);
        thread.start();
    }
}
