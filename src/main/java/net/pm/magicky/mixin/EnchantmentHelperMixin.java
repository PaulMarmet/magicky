package net.pm.magicky.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.pm.magicky.MagickyTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getLevel(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ItemStack;)I", at = @At(value = "RETURN"), cancellable = true)
    private static void addGoldenFortune(RegistryEntry<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int currentLevel = cir.getReturnValue();
        if (stack.isIn(MagickyTags.LUCKY) && enchantment.isIn(MagickyTags.LUCK_AFFECTED)) {
            cir.setReturnValue(currentLevel + 1);
        }
    }

}
