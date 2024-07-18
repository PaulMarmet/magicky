package net.pm.magicky.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;

public class MagickyScreenHandlers {
    public static final ScreenHandlerType<AnvilScreenHandlerM> ANVIL_M = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Magicky.MOD_ID, "anvil_m"), new ScreenHandlerType<>(AnvilScreenHandlerM::new, FeatureFlags.VANILLA_FEATURES));

    public static void registerScreenHandlers() {

    }
}
