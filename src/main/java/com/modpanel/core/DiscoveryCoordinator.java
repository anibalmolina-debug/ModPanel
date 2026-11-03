package com.modpanel.core;

import com.modpanel.discovery.DiscoveryProvider;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryCoordinator {
    private final List<DiscoveryProvider> providers = new ArrayList<>();
    private final ModRegistry modRegistry = new ModRegistry();
    private final java.util.Map<String, List<ConfigSource>> cache = new java.util.HashMap<>();

    public void registerProvider(DiscoveryProvider provider) {
        providers.add(provider);
    }

    public List<ConfigSource> discoverAll() {
        List<ConfigSource> out = new ArrayList<>();
        for (ModRegistry.ModInfo mod : modRegistry.getLoadedMods()) {
            out.addAll(discoverMod(mod.id(), mod.version()));
        }
        return out;
    }

    public List<ConfigSource> discoverMod(String modId, String version) {
        String key = modId + "@" + version;
        if (cache.containsKey(key)) return new ArrayList<>(cache.get(key));
        List<ConfigSource> out = new ArrayList<>();
        for (DiscoveryProvider p : providers) {
            if (p.supports(modId)) {
                out.addAll(p.discover(modId, version));
            }
        }
        cache.put(key, new ArrayList<>(out));
        return out;
    }

    public void invalidate(String modId) {
        cache.keySet().removeIf(k -> k.startsWith(modId + "@"));
    }

    public DiscoveryResult probe(String modId, String version) {
        for (DiscoveryProvider p : providers) {
            if (p.supports(modId)) {
                return p.probe(modId, version);
            }
        }
        return new DiscoveryResult(DiscoveryResult.Status.UNSUPPORTED, "No provider", new ArrayList<>());
    }
}