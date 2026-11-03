package com.modpanel.model;

import java.util.List;

public class ConfigEntry {
    private final String id;
    private final String sourceId;
    private final String modId;
    private final String key;
    private final SettingType type;
    private Object currentValue;
    private Object defaultValue;
    private final SettingMetadata metadata;
    private final Object minValue;
    private final Object maxValue;
    private final List<Object> choices;
    private final ConfigCapabilities capabilities;

    public ConfigEntry(String id, String sourceId, String modId, String key, SettingType type,
                       Object currentValue, Object defaultValue, SettingMetadata metadata,
                       Object minValue, Object maxValue, List<Object> choices, ConfigCapabilities capabilities) {
        this.id = id;
        this.sourceId = sourceId;
        this.modId = modId;
        this.key = key;
        this.type = type;
        this.currentValue = currentValue;
        this.defaultValue = defaultValue;
        this.metadata = metadata == null ? SettingMetadata.unknown(key) : metadata;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.choices = choices;
        this.capabilities = capabilities == null ? ConfigCapabilities.readOnly() : capabilities;
    }

    public String getId() { return id; }
    public String getSourceId() { return sourceId; }
    public String getModId() { return modId; }
    public String getKey() { return key; }
    public SettingType getType() { return type; }
    public Object getCurrentValue() { return currentValue; }
    public void setCurrentValue(Object v) { this.currentValue = v; }
    public Object getDefaultValue() { return defaultValue; }
    public void setDefaultValue(Object v) { this.defaultValue = v; }
    public SettingMetadata getMetadata() { return metadata; }
    public Object getMinValue() { return minValue; }
    public Object getMaxValue() { return maxValue; }
    public List<Object> getChoices() { return choices; }
    public ConfigCapabilities getCapabilities() { return capabilities; }
}