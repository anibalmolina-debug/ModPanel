package com.modpanel.adapter;

import com.modpanel.compatibility.VersionMatcher;
import com.modpanel.model.ApplyResult;
import com.modpanel.model.ConfigCapabilities;
import com.modpanel.model.ConfigChange;
import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSection;
import com.modpanel.model.ConfigSnapshot;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;
import com.modpanel.model.ReloadRequirement;
import com.modpanel.model.ResetResult;
import com.modpanel.model.SettingMetadata;
import com.modpanel.model.SettingType;
import com.modpanel.validation.ValidationResult;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LithiumAdapter implements ConfigAdapter {
    @Override
    public int priority() { return 100; }

    private static final String CONFIG_PATH = "config/lithium.properties";
    private long lastReadMillis = 0L;
    private static final VersionMatcher versionMatcher = new VersionMatcher();

    @Override
    public AdapterDescriptor descriptor() {
        return new AdapterDescriptor("lithium", List.of("lithium"), List.of("properties"), "0.0.0", null);
    }

    @Override
    public boolean supports(AdapterContext context) {
        return "lithium".equals(context.getModId()) && versionMatcher.matches(context.getModVersion(), "0.0.0", null);
    }

    @Override
    public DiscoveryResult discover(AdapterContext context) {
        return new DiscoveryResult(DiscoveryResult.Status.SUPPORTED, "OK", new ArrayList<>());
    }

    @Override
    public ConfigSnapshot read(ConfigSource source) {
        Properties defaults = new Properties();
        try (InputStream in = LithiumAdapter.class.getResourceAsStream("/assets/modpanel/lithium-defaults.properties")) {
            if (in != null) defaults.load(in);
        } catch (Exception ignored) { }

        Properties user = new Properties();
        Path path = Paths.get(CONFIG_PATH);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                user.load(fis);
            } catch (Exception ignored) { }
            try { lastReadMillis = Files.getLastModifiedTime(path).toMillis(); } catch (Exception ignored) { }
        } else {
            lastReadMillis = 0L;
        }

        List<ConfigEntry> entries = new ArrayList<>();
        // defaults
        for (String key : defaults.stringPropertyNames()) {
            String defaultVal = defaults.getProperty(key, "true");
            String currentVal = user.getProperty(key, defaultVal);
            boolean bool = "true".equalsIgnoreCase(currentVal);
            ConfigCapabilities caps = source.getCapabilities();
            entries.add(new ConfigEntry(
                    "lithium:" + key,
                    source.getSourceId(),
                    source.getModId(),
                    key,
                    SettingType.BOOLEAN,
                    bool,
                    "true".equalsIgnoreCase(defaultVal),
                    new SettingMetadata(key, null, key.split("\\.")[0], null, true, false, false),
                    null,
                    null,
                    null,
                    caps
            ));
        }
        // extra user keys
        for (String key : user.stringPropertyNames()) {
            if (defaults.containsKey(key)) continue;
            String val = user.getProperty(key);
            SettingType type = ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) ? SettingType.BOOLEAN : SettingType.STRING;
            Object current = type == SettingType.BOOLEAN ? Boolean.parseBoolean(val) : val;
            ConfigCapabilities caps = source.getCapabilities();
            entries.add(new ConfigEntry(
                    "lithium:" + key,
                    source.getSourceId(),
                    source.getModId(),
                    key,
                    type,
                    current,
                    null,
                    new SettingMetadata(key, null, key.split("\\.")[0], null, true, false, false),
                    null,
                    null,
                    null,
                    caps
            ));
        }

        return new ConfigSnapshot(source, new ArrayList<>(), entries);
    }

    @Override
    public ValidationResult validate(ConfigEntry entry, Object value) {
        if (entry.getType() == SettingType.BOOLEAN) {
            if (!(value instanceof Boolean)) return ValidationResult.fail("Expected boolean");
            return ValidationResult.ok();
        }
        return ValidationResult.ok();
    }

    @Override
    public ApplyResult apply(ConfigSource source, List<ConfigChange> changes) {
        Path path = Paths.get(CONFIG_PATH);
        try {
            // conflict detection
            if (Files.exists(path)) {
                long now = Files.getLastModifiedTime(path).toMillis();
                if (lastReadMillis > 0 && now > lastReadMillis) {
                    return new ApplyResult(false, "Conflict: file changed externally since read");
                }
            }
            Files.createDirectories(path.getParent());
            Properties props = new Properties();
            if (Files.exists(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    props.load(fis);
                }
            }
            for (ConfigChange c : changes) {
                String key = c.entryId();
                if (key.startsWith("lithium:")) key = key.substring("lithium:".length());
                props.setProperty(key, String.valueOf(c.proposedValue()));
            }
            // backup
            new com.modpanel.persistence.BackupService().backup(source.getSourceId(), path);
            // atomic write
            Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            try (FileOutputStream fos = new FileOutputStream(tmp.toFile())) {
                props.store(fos, "ModPanel generated overrides");
            }
            try {
                Files.move(tmp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception e) {
                Files.move(tmp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            try { lastReadMillis = Files.getLastModifiedTime(path).toMillis(); } catch (Exception ignored) { }
            return new ApplyResult(true, "Saved");
        } catch (Exception e) {
            return new ApplyResult(false, e.getMessage());
        }
    }

    @Override
    public ResetResult resetToDefault(ConfigSource source, List<String> entryIds) {
        Path path = Paths.get(CONFIG_PATH);
        try {
            Properties props = new Properties();
            if (Files.exists(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    props.load(fis);
                }
            }
            for (String id : entryIds) {
                String key = id.startsWith("lithium:") ? id.substring("lithium:".length()) : id;
                props.remove(key);
            }
            Files.createDirectories(path.getParent());
            Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            try (FileOutputStream fos = new FileOutputStream(tmp.toFile())) {
                props.store(fos, "ModPanel generated overrides");
            }
            try {
                Files.move(tmp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception e) {
                Files.move(tmp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            try { lastReadMillis = Files.getLastModifiedTime(path).toMillis(); } catch (Exception ignored) { }
            return new ResetResult(true, "Reset to default");
        } catch (Exception e) {
            return new ResetResult(false, e.getMessage());
        }
    }

    @Override
    public ReloadRequirement reloadRequirement(ConfigSource source, List<ConfigChange> changes) {
        return new ReloadRequirement(true, "Lithium settings require a game restart");
    }
}