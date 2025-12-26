package net.pm.magicky.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;createScreenHandlerFactory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/screen/NamedScreenHandlerFactory;"))
    private NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
            return new EnchantmentScreenHandlerM(syncId, inventory, ScreenHandlerContext.create(world, pos));
        }, ((Nameable)world.getBlockEntity(pos)).getDisplayName());
    }
}
