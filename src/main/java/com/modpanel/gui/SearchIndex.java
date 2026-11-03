package com.modpanel.gui;

import com.modpanel.model.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

public class SearchIndex {
    private final List<ConfigEntry> entries = new ArrayList<>();

    public void rebuild(List<ConfigEntry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
    }

    public List<ConfigEntry> search(String query) {
        List<ConfigEntry> out = new ArrayList<>();
        if (query == null || query.isBlank()) return out;
        String q = query.toLowerCase();
        for (ConfigEntry e : entries) {
            if (e.getKey().toLowerCase().contains(q)
                    || (e.getMetadata().label() != null && e.getMetadata().label().toLowerCase().contains(q))) {
                out.add(e);
            }
        }
        return out;
    }
}