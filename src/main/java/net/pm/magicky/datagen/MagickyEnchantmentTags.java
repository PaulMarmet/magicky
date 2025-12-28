package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyEnchantmentTags extends FabricTagProvider<Enchantment> {
    public MagickyEnchantmentTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    public static final TagKey<Enchantment> LUCK_AFFECTED = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "luck_affected"));
    public static final TagKey<Enchantment> MENDS = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "mends"));

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(LUCK_AFFECTED)
                .addElement(Enchantments.FORTUNE.identifier())
                .addElement(Enchantments.LOOTING.identifier());
        getOrCreateRawBuilder(MENDS)
                .addElement(Enchantments.MENDING.identifier());

    }
}
