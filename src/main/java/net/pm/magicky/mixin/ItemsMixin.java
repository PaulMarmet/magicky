package net.pm.magicky.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public class ItemsMixin {
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value="CONSTANT", args="stringValue=golden_helmet", ordinal = 0)), at = @At(value = "NEW", target = "(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0))
    private static ArmorItem upGoldDurabilityHelmet(RegistryEntry material, ArmorItem.Type type, Item.Settings settings) {
        return new ArmorItem(material, type, settings.maxDamage(net.minecraft.item.ArmorItem.Type.HELMET.getMaxDamage(12)));
    }
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value="CONSTANT", args="stringValue=golden_chestplate", ordinal = 0)), at = @At(value = "NEW", target = "(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0))
    private static ArmorItem upGoldDurabilityChestplate(RegistryEntry material, ArmorItem.Type type, Item.Settings settings) {
        return new ArmorItem(material, type, settings.maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(12)));
    }
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value="CONSTANT", args="stringValue=golden_leggings", ordinal = 0)), at = @At(value = "NEW", target = "(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0))
    private static ArmorItem upGoldDurabilityLeggings(RegistryEntry material, ArmorItem.Type type, Item.Settings settings) {
        return new ArmorItem(material, type, settings.maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(12)));
    }
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value="CONSTANT", args="stringValue=golden_boots", ordinal = 0)), at = @At(value = "NEW", target = "(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/ArmorItem;", ordinal = 0))
    private static ArmorItem upGoldDurabilityBoots(RegistryEntry material, ArmorItem.Type type, Item.Settings settings) {
        return new ArmorItem(material, type, settings.maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(12)));
    }
}
