package net.pm.magicky.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;"))
    private MenuProvider createScreenHandlerFactory(BlockState instance, Level level, BlockPos blockPos) {
        return new SimpleMenuProvider((syncId, inventory, player) -> {
            return new EnchantmentScreenHandlerM(syncId, inventory, ContainerLevelAccess.create(level, blockPos));
        }, ((Nameable)level.getBlockEntity(blockPos)).getDisplayName());
    }
}
