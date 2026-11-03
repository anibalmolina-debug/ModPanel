package com.modpanel.model;

public record SettingMetadata(
        String label,
        String description,
        String category,
        String units,
        boolean restartRequired,
        boolean readOnly,
        boolean sensitive
) {
    public static SettingMetadata unknown(String key) {
        return new SettingMetadata(key, null, null, null, false, false, false);
    }
}