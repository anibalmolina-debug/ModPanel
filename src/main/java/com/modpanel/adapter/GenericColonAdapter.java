package com.modpanel.adapter;

import com.modpanel.model.*;
import com.modpanel.validation.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericColonAdapter implements ConfigAdapter {
    @Override public int priority() { return 1; }
    @Override public AdapterDescriptor descriptor() {
        return new AdapterDescriptor("generic-colon", List.of(), List.of("txt"), null, null);
    }
    @Override public boolean supports(AdapterContext context) {
        return Files.exists(Paths.get("config", context.getModId() + ".txt"));
    }
    @Override public DiscoveryResult discover(AdapterContext context) {
        return new DiscoveryResult(DiscoveryResult.Status.READ_ONLY, "Generic colon", new ArrayList<>());
    }
    @Override public ConfigSnapshot read(ConfigSource source) {
        List<ConfigEntry> entries = new ArrayList<>();
        Path path = Paths.get("config", source.getModId() + ".txt");
        List<String> lines = new ArrayList<>();
        try { lines = Files.readAllLines(path); } catch (IOException ignored) { }
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("#")) continue;
            int idx = t.indexOf(':');
            if (idx < 0) continue;
            String key = t.substring(0, idx).trim();
            String raw = t.substring(idx + 1).trim();
            SettingType type = infer(raw);
            Object val = parse(type, raw);
            entries.add(new ConfigEntry(source.getModId() + ":" + key, source.getSourceId(), source.getModId(), key,
                    type, val, null, new SettingMetadata(key, null, null, null, true, true, false),
                    null, null, null, ConfigCapabilities.readOnly()));
        }
        return new ConfigSnapshot(source, new ArrayList<>(), entries);
    }
    @Override public ValidationResult validate(ConfigEntry e, Object v) { return ValidationResult.ok(); }
    @Override public ApplyResult apply(ConfigSource s, List<ConfigChange> c) { return new ApplyResult(false, "Read-only adapter"); }
    @Override public ResetResult resetToDefault(ConfigSource s, List<String> ids) { return new ResetResult(false, "Read-only adapter"); }
    @Override public ReloadRequirement reloadRequirement(ConfigSource s, List<ConfigChange> c) { return new ReloadRequirement(true, "Restart required"); }

    private SettingType infer(String raw) {
        if ("true".equalsIgnoreCase(raw) || "false".equalsIgnoreCase(raw)) return SettingType.BOOLEAN;
        if (raw.contains(".")) { try { Double.parseDouble(raw); return SettingType.DECIMAL; } catch (Exception ignored) {} }
        try { Integer.parseInt(raw); return SettingType.INTEGER; } catch (Exception ignored) {}
        return SettingType.STRING;
    }
    private Object parse(SettingType type, String raw) {
        switch (type) {
            case BOOLEAN: return Boolean.parseBoolean(raw);
            case INTEGER: try { return Integer.parseInt(raw); } catch (Exception e) { return raw; }
            case DECIMAL: try { return Double.parseDouble(raw); } catch (Exception e) { return raw; }
            default: return raw;
        }
    }
}