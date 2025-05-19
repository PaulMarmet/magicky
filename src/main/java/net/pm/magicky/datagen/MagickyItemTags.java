package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyItemTags extends FabricTagProvider.ItemTagProvider{
    public MagickyItemTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final TagKey<Item> ENCHANTING_CATALYST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "enchanting_catalyst"));
    public static final TagKey<Item> INSCRIBING_CATALYST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "catalysts/inscribing"));
    public static final TagKey<Item> CLONING_CATALYST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "catalysts/cloning"));
    public static final TagKey<Item> REDUCING_CATALYST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "catalysts/reducing"));

    public static final TagKey<Item> LUCKY = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "lucky"));
    public static final TagKey<Item> MENDING_AGENT = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "mending_agent"));
    public static final TagKey<Item> PET_ARMOR = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "pet_armor"));

    public static final TagKey<Item> FROSTING = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "enchantable/frosting"));
    public static final TagKey<Item> NONE = TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "enchantable/none"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ENCHANTING_CATALYST)
                .addTag(INSCRIBING_CATALYST)
                .addTag(CLONING_CATALYST)
                .addTag(REDUCING_CATALYST)
                .setReplace(false);
        getOrCreateTagBuilder(INSCRIBING_CATALYST)
                .add(Items.LAPIS_LAZULI)
                .setReplace(false);
        getOrCreateTagBuilder(CLONING_CATALYST)
                .add(Items.ECHO_SHARD)
                .setReplace(false);
        getOrCreateTagBuilder(REDUCING_CATALYST)
                .add(Items.AMETHYST_SHARD)
                .setReplace(false);

        getOrCreateTagBuilder(LUCKY)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD)
                .setReplace(false);
        getOrCreateTagBuilder(MENDING_AGENT)
                .add(Items.EXPERIENCE_BOTTLE)
                .add(Items.LAPIS_BLOCK)
                .setReplace(false);
        getOrCreateTagBuilder(PET_ARMOR)
                .add(Items.WOLF_ARMOR)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(Items.GOLDEN_HORSE_ARMOR)
                .add(Items.IRON_HORSE_ARMOR)
                .add(Items.DIAMOND_HORSE_ARMOR)
                .setReplace(false);

        //the enchantable subgroup
        getOrCreateTagBuilder(FROSTING)
                .forceAddTag(ItemTags.FOOT_ARMOR)
                .addTag(PET_ARMOR)
                .setReplace(false);
        getOrCreateTagBuilder(NONE);
    }
}
