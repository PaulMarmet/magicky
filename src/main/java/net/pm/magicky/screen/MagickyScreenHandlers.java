package net.pm.magicky.screen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.pm.magicky.Magicky;

public class MagickyScreenHandlers {
    public static final MenuType<AnvilScreenHandlerM> ANVIL_M = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "anvil_m"), new MenuType<>(AnvilScreenHandlerM::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<EnchantmentScreenHandlerM> ENCHANTMENT_M = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "enchantment_m"), new MenuType<>(EnchantmentScreenHandlerM::new, FeatureFlags.VANILLA_SET));

    public static void registerScreenHandlers() {

    }
}
