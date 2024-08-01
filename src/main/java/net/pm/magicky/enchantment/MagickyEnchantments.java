package net.pm.magicky.enchantment;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;

public class MagickyEnchantments {
    //Registries.ENCHANTMENT.get(new Identifier(Magicky.MOD_ID, "recoil"));

    public static void registerEnchantments() {
        Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(Magicky.MOD_ID, "recoil"), RecoilEnchantmentEffect.CODEC);
    }
}
