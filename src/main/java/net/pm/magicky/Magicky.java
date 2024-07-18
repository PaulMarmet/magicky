package net.pm.magicky;

import net.fabricmc.api.ModInitializer;

import net.pm.magicky.screen.MagickyScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Magicky implements ModInitializer {
	public static final String MOD_ID = "magicky";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MagickyScreenHandlers.registerScreenHandlers();
	}
}