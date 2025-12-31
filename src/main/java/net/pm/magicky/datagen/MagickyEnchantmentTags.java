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
import net.pm.magicky.enchantment.MagickyEnchantments;

import java.util.concurrent.CompletableFuture;

public class MagickyEnchantmentTags extends FabricTagProvider<Enchantment> {
    public MagickyEnchantmentTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    static final String EX_SET = "exclusive_set/";

    public static final TagKey<Enchantment> LUCK_AFFECTED = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "luck_affected"));
    public static final TagKey<Enchantment> MENDS = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "mends"));

    public static final TagKey<Enchantment> BLADE = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"blade_addition"));
    public static final TagKey<Enchantment> BOW = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"specialization_bow"));
    public static final TagKey<Enchantment> GENERAL_PROTECTION = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"general_protection"));
    public static final TagKey<Enchantment> SPECIAL_PROTECTION = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"specialized_protection"));
    public static final TagKey<Enchantment> SPECIALIZATION = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"specialization"));
    public static final TagKey<Enchantment> SPECIAL_DAMAGE = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, EX_SET+"special_damage"));

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(LUCK_AFFECTED)
                .addElement(Enchantments.FORTUNE.identifier())
                .addElement(Enchantments.LOOTING.identifier());
        getOrCreateRawBuilder(MENDS)
                .addElement(Enchantments.MENDING.identifier());

        getOrCreateRawBuilder(BLADE)
                .addElement(Enchantments.FIRE_ASPECT.identifier())
                .addElement(Enchantments.SWEEPING_EDGE.identifier());
        getOrCreateRawBuilder(BOW)
                .addElement(Enchantments.INFINITY.identifier())
                .addElement(Enchantments.POWER.identifier())
                .addElement(Enchantments.PUNCH.identifier());
        getOrCreateRawBuilder(GENERAL_PROTECTION)
                .addElement(Enchantments.PROTECTION.identifier())
                .addElement(Enchantments.THORNS.identifier());
        getOrCreateRawBuilder(SPECIAL_PROTECTION)
                .addElement(Enchantments.BLAST_PROTECTION.identifier())
                .addElement(Enchantments.FIRE_PROTECTION.identifier())
                .addElement(Enchantments.PROJECTILE_PROTECTION.identifier())
                .addElement(Enchantments.FEATHER_FALLING.identifier())
                .addElement(MagickyEnchantments.MAGIC_PROTECTION.identifier());
        getOrCreateRawBuilder(SPECIALIZATION)
                .addElement(Enchantments.BREACH.identifier())
                .addElement(Enchantments.DENSITY.identifier())
                .addElement(Enchantments.EFFICIENCY.identifier())
                .addElement(Enchantments.FORTUNE.identifier())
                .addElement(Enchantments.KNOCKBACK.identifier())
                .addElement(Enchantments.LOOTING.identifier())
                .addElement(Enchantments.SILK_TOUCH.identifier())
                .addElement(Enchantments.SHARPNESS.identifier());
        getOrCreateRawBuilder(SPECIAL_DAMAGE)
                .addElement(Enchantments.BANE_OF_ARTHROPODS.identifier())
                .addElement(Enchantments.SMITE.identifier());
    }
}
