package net.pm.magicky;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MagickyTags {
    public static final TagKey<Item> LUCKY = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "lucky"));
    public static final TagKey<Enchantment> LUCK_AFFECTED = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Magicky.MOD_ID, "luck_affected"));

    public static void registerTags() {

    }
}
