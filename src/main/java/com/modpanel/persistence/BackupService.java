package com.modpanel.persistence;

import com.modpanel.ModPanel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class BackupService {
    private static final int RETENTION = 5;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public Path backup(String sourceId, Path sourcePath) {
        try {
            if (sourcePath == null || !Files.exists(sourcePath)) return null;
            Path dir = Paths.get("config", "modpanel-backups");
            Files.createDirectories(dir);
            String safe = sourceId.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path backup = dir.resolve(safe + "_" + LocalDateTime.now().format(FMT) + ".bak");
            Files.copy(sourcePath, backup, StandardCopyOption.REPLACE_EXISTING);
            prune(dir, safe);
            return backup;
        } catch (IOException e) {
            ModPanel.LOGGER.warn("Backup failed for {}", sourceId, e);
            return null;
        }
    }

    public void backup(String sourceId) {
        // default path guess
        backup(sourceId, Paths.get("config", sourceId + ".properties"));
    }

    private void prune(Path dir, String safe) {
        try (Stream<Path> s = Files.list(dir)) {
            List<Path> list = s.filter(p -> p.getFileName().toString().startsWith(safe + "_"))
                    .sorted(Comparator.comparing(Path::toString).reversed())
                    .toList();
            for (int i = RETENTION; i < list.size(); i++) {
                Files.deleteIfExists(list.get(i));
            }
        } catch (IOException ignored) { }
    }
}