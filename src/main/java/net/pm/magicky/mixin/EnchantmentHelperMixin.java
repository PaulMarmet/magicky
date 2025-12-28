package net.pm.magicky.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.pm.magicky.datagen.MagickyEnchantmentTags;
import net.pm.magicky.datagen.MagickyItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "RETURN"), cancellable = true)
    private static void addGoldenFortune(Holder<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int currentLevel = cir.getReturnValue();
        if (stack.is(MagickyItemTags.LUCKY) && enchantment.is(MagickyEnchantmentTags.LUCK_AFFECTED)) {
            cir.setReturnValue(currentLevel + 1);
        }
    }

}
