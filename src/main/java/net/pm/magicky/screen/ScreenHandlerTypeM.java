package net.pm.magicky.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerTypeM {
    public static final ScreenHandlerType<AnvilScreenHandler> ANVIL_M = Registry.register(Registries.SCREEN_HANDLER, "anvil_m", new ScreenHandlerType(AnvilScreenHandlerM::new, FeatureFlags.VANILLA_FEATURES));
}
