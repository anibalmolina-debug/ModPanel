package com.modpanel.adapter;

import com.modpanel.model.*;
import com.modpanel.validation.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericTomlAdapter implements ConfigAdapter {
    @Override public int priority() { return 1; }
    @Override public AdapterDescriptor descriptor() {
        return new AdapterDescriptor("generic-toml", List.of(), List.of("toml"), null, null);
    }
    @Override public boolean supports(AdapterContext context) {
        return Files.exists(Paths.get("config", context.getModId() + ".toml"));
    }
    @Override public DiscoveryResult discover(AdapterContext context) {
        return new DiscoveryResult(DiscoveryResult.Status.READ_ONLY, "Generic toml", new ArrayList<>());
    }
    @Override public ConfigSnapshot read(ConfigSource source) {
        List<ConfigEntry> entries = new ArrayList<>();
        Path path = Paths.get("config", source.getModId() + ".toml");
        List<String> lines = new ArrayList<>();
        try { lines = Files.readAllLines(path); } catch (IOException ignored) { }
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("#") || t.startsWith("[")) continue;
            int eq = t.indexOf('=');
            if (eq < 0) continue;
            String key = t.substring(0, eq).trim();
            String raw = t.substring(eq + 1).trim();
            // strip quotes
            SettingType type = infer(raw);
            Object val = parse(type, raw);
            entries.add(new ConfigEntry(source.getModId() + ":" + key, source.getSourceId(), source.getModId(), key,
                    type, val, null, new SettingMetadata(key, null, null, null, false, true, false),
                    null, null, null, ConfigCapabilities.readOnly()));
        }
        return new ConfigSnapshot(source, new ArrayList<>(), entries);
    }
    @Override public ValidationResult validate(ConfigEntry e, Object v) { return ValidationResult.ok(); }
    @Override public ApplyResult apply(ConfigSource s, List<ConfigChange> c) { return new ApplyResult(false, "Read-only adapter"); }
    @Override public ResetResult resetToDefault(ConfigSource s, List<String> ids) { return new ResetResult(false, "Read-only adapter"); }
    @Override public ReloadRequirement reloadRequirement(ConfigSource s, List<ConfigChange> c) { return ReloadRequirement.none(); }

    private SettingType infer(String raw) {
        if ("true".equalsIgnoreCase(raw) || "false".equalsIgnoreCase(raw)) return SettingType.BOOLEAN;
        if (raw.startsWith("\"")) return SettingType.STRING;
        if (raw.contains(".")) return SettingType.DECIMAL;
        return SettingType.INTEGER;
    }
    private Object parse(SettingType type, String raw) {
        String v = raw.replaceAll("^\"|\"$", "");
        switch (type) {
            case BOOLEAN: return Boolean.parseBoolean(raw);
            case INTEGER: try { return Integer.parseInt(raw); } catch (Exception e) { return v; }
            case DECIMAL: try { return Double.parseDouble(raw); } catch (Exception e) { return v; }
            default: return v;
        }
    }
}