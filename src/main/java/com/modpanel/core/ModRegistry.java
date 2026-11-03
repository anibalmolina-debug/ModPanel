package com.modpanel.core;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.ArrayList;
import java.util.List;

public class ModRegistry {
    public record ModInfo(String id, String name, String version) {}

    public List<ModInfo> getLoadedMods() {
        List<ModInfo> out = new ArrayList<>();
        for (ModContainer mc : FabricLoader.getInstance().getAllMods()) {
            out.add(new ModInfo(mc.getMetadata().getId(), mc.getMetadata().getName(), mc.getMetadata().getVersion().getFriendlyString()));
        }
        return out;
    }

    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public String getVersion(String id) {
        return FabricLoader.getInstance().getModContainer(id)
                .map(m -> m.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
    }
}