package net.pm.magicky.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Mutable
    @Final
    @Shadow public int x;
    @Mutable
    @Final
    @Shadow public int y;

    @Inject(method = "onTake", at = @At(value = "HEAD"))
    public void makeWiggle(int amount, CallbackInfo ci) {
        this.wiggle(amount);
    }

    @Unique
    public void wiggle(int ticks) {
        this.x = (int) (35 + 16*Math.cos(ticks*2*Math.PI/100.0));
        this.y = (int) (47 + 16*Math.sin(ticks*2*Math.PI/100.0));
    }
}
