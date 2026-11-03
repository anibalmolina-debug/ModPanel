package com.modpanel.model;

import java.util.List;

public record ConfigSnapshot(
        ConfigSource source,
        List<ConfigSection> sections,
        List<ConfigEntry> entries
) {
}