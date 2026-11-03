package com.modpanel.discovery;

import com.modpanel.model.ConfigCapabilities;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericPropertiesProvider implements DiscoveryProvider {
    @Override public String id() { return "generic-properties"; }
    @Override public int priority() { return 5; }
    @Override
    public boolean supports(String modId) {
        return Files.exists(Paths.get("config", modId + ".properties"));
    }
    @Override
    public List<ConfigSource> discover(String modId, String version) {
        List<ConfigSource> out = new ArrayList<>();
        if (supports(modId)) {
            ConfigCapabilities caps = ConfigCapabilities.readOnly();
            out.add(new ConfigSource(modId, modId + ":properties", "properties", version, caps, DiscoveryResult.Status.READ_ONLY, new ArrayList<>()));
        }
        return out;
    }
    @Override
    public DiscoveryResult probe(String modId, String version) {
        return supports(modId)
                ? new DiscoveryResult(DiscoveryResult.Status.READ_ONLY, "Generic properties source", new ArrayList<>())
                : new DiscoveryResult(DiscoveryResult.Status.UNSUPPORTED, "No properties source", new ArrayList<>());
    }
}