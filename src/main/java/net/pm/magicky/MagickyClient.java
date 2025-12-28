package net.pm.magicky;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.pm.magicky.client.gui.screen.ingame.AnvilScreenM;
import net.pm.magicky.client.gui.screen.ingame.EnchantmentScreenM;
import net.pm.magicky.screen.MagickyScreenHandlers;

public class MagickyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(MagickyScreenHandlers.ANVIL_M, AnvilScreenM::new);
        MenuScreens.register(MagickyScreenHandlers.ENCHANTMENT_M, EnchantmentScreenM::new);
    }
}
