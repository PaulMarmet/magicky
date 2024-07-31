package net.pm.magicky.mixin;

import net.minecraft.item.ToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ToolMaterials.class)
public class ToolMaterialsMixin {
    @ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=GOLD")), constant = @Constant(ordinal = 1))
    private static int alterGoldDurability(int value) {
        return 200;
    }
    @ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=GOLD")), constant = @Constant(ordinal = 3))
    private static float alterGoldDamage(float value) {
        return 1.5f;
    }

    @ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=IRON")), constant = @Constant(ordinal = 1))
    private static int alterIronDurability(int value) {
        return 400;
    }
}
