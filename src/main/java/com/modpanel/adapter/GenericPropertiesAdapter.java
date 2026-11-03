package com.modpanel.adapter;

import com.modpanel.model.ApplyResult;
import com.modpanel.model.ConfigCapabilities;
import com.modpanel.model.ConfigChange;
import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSnapshot;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;
import com.modpanel.model.ReloadRequirement;
import com.modpanel.model.ResetResult;
import com.modpanel.model.SettingMetadata;
import com.modpanel.model.SettingType;
import com.modpanel.validation.ValidationResult;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GenericPropertiesAdapter implements ConfigAdapter {
    @Override public int priority() { return 1; }

    @Override
    public AdapterDescriptor descriptor() {
        return new AdapterDescriptor("generic-properties", List.of(), List.of("properties"), null, null);
    }

    @Override
    public boolean supports(AdapterContext context) {
        Path p = Paths.get("config", context.getModId() + ".properties");
        return Files.exists(p);
    }

    @Override
    public DiscoveryResult discover(AdapterContext context) {
        return new DiscoveryResult(DiscoveryResult.Status.READ_ONLY, "Generic properties", new ArrayList<>());
    }

    @Override
    public ConfigSnapshot read(ConfigSource source) {
        Properties props = new Properties();
        Path path = Paths.get("config", source.getModId() + ".properties");
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                props.load(fis);
            } catch (Exception ignored) { }
        }
        List<ConfigEntry> entries = new ArrayList<>();
        for (String key : props.stringPropertyNames()) {
            String val = props.getProperty(key);
            SettingType type = inferType(val);
            Object current = parse(type, val);
            entries.add(new ConfigEntry(
                    source.getModId() + ":" + key,
                    source.getSourceId(),
                    source.getModId(),
                    key,
                    type,
                    current,
                    null,
                    new SettingMetadata(key, null, null, null, false, true, false),
                    null, null, null,
                    ConfigCapabilities.readOnly()
            ));
        }
        return new ConfigSnapshot(source, new ArrayList<>(), entries);
    }

    @Override
    public ValidationResult validate(ConfigEntry entry, Object value) {
        return ValidationResult.ok();
    }

    @Override
    public ApplyResult apply(ConfigSource source, List<ConfigChange> changes) {
        return new ApplyResult(false, "Read-only adapter");
    }

    @Override
    public ResetResult resetToDefault(ConfigSource source, List<String> entryIds) {
        return new ResetResult(false, "Read-only adapter");
    }

    @Override
    public ReloadRequirement reloadRequirement(ConfigSource source, List<ConfigChange> changes) {
        return ReloadRequirement.none();
    }

    private SettingType inferType(String val) {
        if ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) return SettingType.BOOLEAN;
        try { Integer.parseInt(val); return SettingType.INTEGER; } catch (Exception ignored) { }
        try { Double.parseDouble(val); return SettingType.DECIMAL; } catch (Exception ignored) { }
        return SettingType.STRING;
    }

    private Object parse(SettingType type, String val) {
        switch (type) {
            case BOOLEAN: return Boolean.parseBoolean(val);
            case INTEGER: try { return Integer.parseInt(val); } catch (Exception e) { return val; }
            case DECIMAL: try { return Double.parseDouble(val); } catch (Exception e) { return val; }
            default: return val;
        }
    }
}