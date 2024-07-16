package net.pm.magicky.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.pm.magicky.screen.AnvilScreenHandlerM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ScreenHandlerType.class)
public class ScreenHandlerTypeMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerType;register(Ljava/lang/String;Lnet/minecraft/screen/ScreenHandlerType$Factory;)Lnet/minecraft/screen/ScreenHandlerType;"), index = 1)
    private static <T extends ScreenHandler> ScreenHandlerType.Factory<T> newAnvil(ScreenHandlerType.Factory<T> factory) {
        return AnvilScreenHandlerM::new;
    }
}
