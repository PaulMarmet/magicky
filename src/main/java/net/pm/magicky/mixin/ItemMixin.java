package net.pm.magicky.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;
import net.pm.magicky.datagen.MagickyEnchantmentTags;
import net.pm.magicky.datagen.MagickyItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "canRepair", at = @At(value = "RETURN"), cancellable = true)
    private void repairTag(ItemStack stack, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (ingredient.isIn(MagickyItemTags.MENDING_AGENT) && EnchantmentHelper.hasAnyEnchantmentsIn(stack, MagickyEnchantmentTags.MENDS)) {
            cir.setReturnValue(true);
        }
        Identifier ingredientID = Registries.ITEM.getId(ingredient.getItem());
        if (stack.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of(Magicky.MOD_ID, "repair_ingredient/" + ingredientID.getNamespace() + "/" +ingredientID.getPath())))) {
            cir.setReturnValue(true);
        }
    }
}
