package com.modpanel.discovery;

import com.modpanel.model.ConfigSource;
import com.modpanel.model.ConfigCapabilities;
import com.modpanel.model.DiscoveryResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LithiumProvider implements DiscoveryProvider {
    @Override
    public String id() { return "lithium"; }

    @Override
    public int priority() { return 100; }

    @Override
    public boolean supports(String modId) { return "lithium".equals(modId); }

    @Override
    public List<ConfigSource> discover(String modId, String version) {
        List<ConfigSource> out = new ArrayList<>();
        Path p = Paths.get("config", "lithium.properties");
        ConfigCapabilities caps = new ConfigCapabilities(true, true, false, false, false, true);
        DiscoveryResult.Status status = Files.exists(p) ? DiscoveryResult.Status.SUPPORTED : DiscoveryResult.Status.SUPPORTED;
        out.add(new ConfigSource(modId, "lithium:properties", "properties", version, caps, status, new ArrayList<>()));
        return out;
    }

    @Override
    public DiscoveryResult probe(String modId, String version) {
        return new DiscoveryResult(DiscoveryResult.Status.SUPPORTED, "Lithium detected", new ArrayList<>());
    }
}