package com.modpanel.adapter;

public class AdapterContext {
    private final String modId;
    private final String modVersion;
    private final String minecraftVersion;

    public AdapterContext(String modId, String modVersion, String minecraftVersion) {
        this.modId = modId;
        this.modVersion = modVersion;
        this.minecraftVersion = minecraftVersion;
    }

    public String getModId() { return modId; }
    public String getModVersion() { return modVersion; }
    public String getMinecraftVersion() { return minecraftVersion; }
}