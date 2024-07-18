package net.pm.magicky;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.pm.magicky.client.gui.screen.ingame.AnvilScreenM;
import net.pm.magicky.screen.MagickyScreenHandlers;

public class MagickyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(MagickyScreenHandlers.ANVIL_M, AnvilScreenM::new);
    }
}
