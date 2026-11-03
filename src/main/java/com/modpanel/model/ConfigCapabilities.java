package com.modpanel.model;

public record ConfigCapabilities(
        boolean read,
        boolean write,
        boolean reset,
        boolean reload,
        boolean liveApply,
        boolean atomicSave
) {
    public static ConfigCapabilities readOnly() {
        return new ConfigCapabilities(true, false, false, false, false, false);
    }

    public static ConfigCapabilities full() {
        return new ConfigCapabilities(true, true, true, true, false, true);
    }
}