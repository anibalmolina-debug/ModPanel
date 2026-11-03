package com.modpanel.core;

import com.modpanel.model.ConfigChange;
import com.modpanel.model.ConfigEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeSet {
    private final String sourceId;
    private final Map<String, Object> proposed = new HashMap<>();
    private final Map<String, Object> original = new HashMap<>();
    private boolean dirty = false;

    public ChangeSet(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceId() { return sourceId; }

    public void stage(ConfigEntry entry, Object value) {
        if (!original.containsKey(entry.getId())) original.put(entry.getId(), entry.getCurrentValue());
        proposed.put(entry.getId(), value);
        dirty = true;
    }

    public Object getProposed(String entryId) { return proposed.get(entryId); }
    public boolean isDirty() { return dirty; }

    public List<ConfigChange> toChanges() {
        List<ConfigChange> out = new ArrayList<>();
        for (Map.Entry<String, Object> e : proposed.entrySet()) {
            out.add(new ConfigChange(e.getKey(), original.get(e.getKey()), e.getValue()));
        }
        return out;
    }

    public void clear() {
        proposed.clear();
        original.clear();
        dirty = false;
    }
}