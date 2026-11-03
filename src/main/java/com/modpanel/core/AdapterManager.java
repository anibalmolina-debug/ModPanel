package com.modpanel.core;

import com.modpanel.adapter.AdapterContext;
import com.modpanel.adapter.AdapterDescriptor;
import com.modpanel.adapter.ConfigAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdapterManager {
    private final List<ConfigAdapter> adapters = new ArrayList<>();

    public void register(ConfigAdapter adapter) {
        adapters.add(adapter);
    }

    public List<ConfigAdapter> getAdapters() {
        return new ArrayList<>(adapters);
    }

    public ConfigAdapter select(AdapterContext context) {
        List<ConfigAdapter> candidates = new ArrayList<>();
        for (ConfigAdapter a : adapters) {
            if (a.supports(context)) candidates.add(a);
        }
        candidates.sort(Comparator.comparingInt(ConfigAdapter::priority).reversed());
        return candidates.isEmpty() ? null : candidates.get(0);
    }
}