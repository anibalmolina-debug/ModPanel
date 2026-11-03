package com.modpanel.discovery;

import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;

import java.util.ArrayList;
import java.util.List;

public class LibraryProvider implements DiscoveryProvider {
    @Override public String id() { return "library"; }
    @Override public int priority() { return 5; }
    @Override public boolean supports(String modId) { return false; }
    @Override public List<ConfigSource> discover(String modId, String version) { return new ArrayList<>(); }
    @Override public DiscoveryResult probe(String modId, String version) {
        return new DiscoveryResult(DiscoveryResult.Status.UNSUPPORTED, "No library source", new ArrayList<>());
    }
}