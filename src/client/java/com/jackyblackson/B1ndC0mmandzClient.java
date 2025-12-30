package com.jackyblackson;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

public class B1ndC0mmandzClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
	}
}