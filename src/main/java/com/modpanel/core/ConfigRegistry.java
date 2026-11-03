package com.modpanel.core;

import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSection;
import com.modpanel.model.ConfigSnapshot;
import com.modpanel.model.ConfigSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigRegistry {
    private final Map<String, ConfigSource> sources = new HashMap<>();
    private final Map<String, ConfigSnapshot> snapshots = new HashMap<>();
    private final Map<String, List<ConfigEntry>> entriesBySource = new HashMap<>();

    public void register(ConfigSnapshot snapshot) {
        ConfigSource s = snapshot.source();
        sources.put(s.getSourceId(), s);
        snapshots.put(s.getSourceId(), snapshot);
        entriesBySource.put(s.getSourceId(), new ArrayList<>(snapshot.entries()));
    }

    public List<ConfigSource> getSources() {
        return new ArrayList<>(sources.values());
    }

    public ConfigSnapshot getSnapshot(String sourceId) {
        return snapshots.get(sourceId);
    }

    public List<ConfigEntry> getEntries(String sourceId) {
        return entriesBySource.getOrDefault(sourceId, new ArrayList<>());
    }

    public List<ConfigEntry> getAllEntries() {
        List<ConfigEntry> out = new ArrayList<>();
        for (List<ConfigEntry> l : entriesBySource.values()) out.addAll(l);
        return out;
    }
}