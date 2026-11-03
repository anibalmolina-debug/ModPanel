package com.modpanel.client.gui;

import com.modpanel.model.ConfigEntry;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.network.chat.Component;

public class ControlFactory {
    public static Object create(ConfigEntryBuilder builder, ConfigEntry entry) {
        // Placeholder: real mapping lives in GeneratedConfigScreen.
        return Component.literal(entry.getKey());
    }
}