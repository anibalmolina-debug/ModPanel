package com.modpanel.discovery;

import com.modpanel.model.ConfigSource;
import com.modpanel.model.DiscoveryResult;

import java.util.List;

public interface DiscoveryProvider {
    String id();
    int priority();
    boolean supports(String modId);
    List<ConfigSource> discover(String modId, String version);
    DiscoveryResult probe(String modId, String version);
}