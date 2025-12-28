package net.pm.magicky.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.pm.magicky.Magicky;

public class MagickyEnchantments {
    //Registries.ENCHANTMENT.get(new Identifier(Magicky.MOD_ID, "recoil"));

    public static void registerEnchantments() {
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "recoil"), RecoilEnchantmentEffect.CODEC);
    }
}
