package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyEnchantmentTags extends FabricTagProvider.EnchantmentTagProvider {
    public MagickyEnchantmentTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    public static final TagKey<Enchantment> LUCK_AFFECTED = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Magicky.MOD_ID, "luck_affected"));
    public static final TagKey<Enchantment> MENDS = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Magicky.MOD_ID, "mends"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(LUCK_AFFECTED)
                .add(Enchantments.FORTUNE)
                .add(Enchantments.LOOTING)
                .setReplace(false);
        getOrCreateTagBuilder(MENDS)
                .add(Enchantments.MENDING)
                .setReplace(false);

    }
}
