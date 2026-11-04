package com.modpanel.client;

import com.modpanel.ModPanel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public class ModPanelClient implements ClientModInitializer {
	public static KeyMapping openKey;

	@Override
	public void onInitializeClient() {
		// Register providers and adapters
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.LithiumProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.GenericPropertiesProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.GenericColonProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.GenericJsonProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.GenericTomlProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.PropertiesProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.JsonProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.TomlProvider());
		com.modpanel.ModPanel.DISCOVERY.registerProvider(new com.modpanel.discovery.LibraryProvider());
		com.modpanel.ModPanel.ADAPTER_MANAGER.register(new com.modpanel.adapter.LithiumAdapter());
		com.modpanel.ModPanel.ADAPTER_MANAGER.register(new com.modpanel.adapter.GenericPropertiesAdapter());
		com.modpanel.ModPanel.ADAPTER_MANAGER.register(new com.modpanel.adapter.GenericColonAdapter());
		com.modpanel.ModPanel.ADAPTER_MANAGER.register(new com.modpanel.adapter.GenericJsonAdapter());
		com.modpanel.ModPanel.ADAPTER_MANAGER.register(new com.modpanel.adapter.GenericTomlAdapter());

		openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.modpanel.open",
				GLFW.GLFW_KEY_M,
				"category.modpanel"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new ModPanelScreen());
				}
			}
		});

		// Optional Mod Menu integration
		if (FabricLoader.getInstance().isModLoaded("modmenu")) {
			try {
				ModMenuIntegration.register();
			} catch (Throwable t) {
				ModPanel.LOGGER.warn("Mod Menu integration unavailable", t);
			}
		}

		ModPanel.LOGGER.info("ModPanel client initialized");
	}
}