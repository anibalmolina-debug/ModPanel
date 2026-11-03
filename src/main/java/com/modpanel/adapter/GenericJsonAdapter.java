package com.modpanel.adapter;

import com.modpanel.model.*;
import com.modpanel.validation.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericJsonAdapter implements ConfigAdapter {
    @Override public int priority() { return 1; }
    @Override public AdapterDescriptor descriptor() {
        return new AdapterDescriptor("generic-json", List.of(), List.of("json"), null, null);
    }
    @Override public boolean supports(AdapterContext context) {
        return Files.exists(Paths.get("config", context.getModId() + ".json"));
    }
    @Override public DiscoveryResult discover(AdapterContext context) {
        return new DiscoveryResult(DiscoveryResult.Status.READ_ONLY, "Generic json", new ArrayList<>());
    }
    @Override public ConfigSnapshot read(ConfigSource source) {
        List<ConfigEntry> entries = new ArrayList<>();
        Path path = Paths.get("config", source.getModId() + ".json");
        String text = "";
        try { text = Files.readString(path); } catch (IOException ignored) { }
        Pattern p = Pattern.compile("\"([A-Za-z0-9_.-]+)\"\\s*:\\s*(true|false|null|-?\\d+(?:\\.\\d+)?|\"(?:[^\"\\\\]|\\\\.)*\")");
        Matcher m = p.matcher(text);
        while (m.find()) {
            String key = m.group(1);
            String raw = m.group(2);
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
        switch (type) {
            case BOOLEAN: return Boolean.parseBoolean(raw);
            case INTEGER: try { return Integer.parseInt(raw); } catch (Exception e) { return raw; }
            case DECIMAL: try { return Double.parseDouble(raw); } catch (Exception e) { return raw; }
            default: return raw.replaceAll("^\"|\"$", "");
        }
    }
}