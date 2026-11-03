package com.modpanel;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModPanel {
	public static final String MOD_ID = "modpanel";

	// This logger is used to write text to the console and the log file.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final com.modpanel.core.ModRegistry MOD_REGISTRY = new com.modpanel.core.ModRegistry();
	public static final com.modpanel.core.AdapterManager ADAPTER_MANAGER = new com.modpanel.core.AdapterManager();
	public static final com.modpanel.core.DiscoveryCoordinator DISCOVERY = new com.modpanel.core.DiscoveryCoordinator();
	public static final com.modpanel.core.ConfigRegistry CONFIG_REGISTRY = new com.modpanel.core.ConfigRegistry();

	private ModPanel() {
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}