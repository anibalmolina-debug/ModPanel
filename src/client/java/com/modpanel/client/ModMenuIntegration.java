package com.modpanel.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration implements ModMenuApi {
	public static void register() {
		// Registration happens via fabric.mod.json entrypoint; this method is a no-op hook.
	}

	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		return parent -> new ModPanelScreen();
	}
}