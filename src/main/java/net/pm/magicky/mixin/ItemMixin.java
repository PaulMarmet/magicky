package net.pm.magicky.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.pm.magicky.Magicky;
import net.pm.magicky.datagen.MagickyEnchantmentTags;
import net.pm.magicky.datagen.MagickyItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemMixin {

    @Inject(method = "isValidRepairItem", at = @At(value = "RETURN"), cancellable = true)
    private void repairTag(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        //TODO: Something fishy here?
        if (itemStack.is(MagickyItemTags.MENDING_AGENT) && EnchantmentHelper.hasTag(((ItemStack)(Object)this), MagickyEnchantmentTags.MENDS)) {
            cir.setReturnValue(true);
        }
        Identifier ingredientID = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        if (((ItemStack)(Object)this).is(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "repair_ingredient/" + ingredientID.getNamespace() + "/" +ingredientID.getPath())))) {
            cir.setReturnValue(true);
        }
    }
}
