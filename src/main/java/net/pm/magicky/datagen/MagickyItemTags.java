package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyItemTags extends FabricTagProvider.ItemTagProvider{
    public MagickyItemTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final TagKey<Item> ENCHANTING_CATALYST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "enchanting_catalyst"));
    public static final TagKey<Item> INSCRIBING_CATALYST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "catalysts/inscribing"));
    public static final TagKey<Item> CLONING_CATALYST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "catalysts/cloning"));
    public static final TagKey<Item> REDUCING_CATALYST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "catalysts/reducing"));

    public static final TagKey<Item> LUCKY = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "lucky"));
    public static final TagKey<Item> MENDING_AGENT = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "mending_agent"));
    public static final TagKey<Item> PET_ARMOR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "pet_armor"));

    public static final TagKey<Item> FROSTING = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "enchantable/frosting"));
    public static final TagKey<Item> NONE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "enchantable/none"));

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(ENCHANTING_CATALYST)
                .addTag(INSCRIBING_CATALYST)
                .addTag(CLONING_CATALYST)
                .addTag(REDUCING_CATALYST)
                .setReplace(false);
        valueLookupBuilder(INSCRIBING_CATALYST)
                .add(Items.LAPIS_LAZULI)
                .setReplace(false);
        valueLookupBuilder(CLONING_CATALYST)
                .add(Items.ECHO_SHARD)
                .setReplace(false);
        valueLookupBuilder(REDUCING_CATALYST)
                .add(Items.AMETHYST_SHARD)
                .setReplace(false);

        valueLookupBuilder(LUCKY)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD)
                .setReplace(false);
        valueLookupBuilder(MENDING_AGENT)
                .add(Items.EXPERIENCE_BOTTLE)
                .add(Items.LAPIS_BLOCK)
                .setReplace(false);
        valueLookupBuilder(PET_ARMOR)
                .add(Items.WOLF_ARMOR)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(Items.GOLDEN_HORSE_ARMOR)
                .add(Items.IRON_HORSE_ARMOR)
                .add(Items.DIAMOND_HORSE_ARMOR)
                .setReplace(false);

        //the enchantable subgroup
        valueLookupBuilder(FROSTING)
                .forceAddTag(ItemTags.FOOT_ARMOR)
                .addTag(PET_ARMOR)
                .setReplace(false);
        valueLookupBuilder(NONE);
    }
}
