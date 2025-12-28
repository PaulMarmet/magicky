package net.pm.magicky.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractArrow.class)
public abstract class ProjectileEntityMixin {

//    @ModifyArgs(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"))
//    private static void giveItemAOwner(Args args, @Local LivingEntity owner) {
//        ItemStack stack = args.get(5);
//        ItemStack shotFrom = args.get(6);
//        if (owner != null) {
//            if (stack != null)
//                stack.setEntityRepresentation(owner);
//            if (shotFrom != null)
//                shotFrom.setEntityRepresentation(owner);
//        }
//    }
//
//    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCustomName(Lnet/minecraft/network/chat/Component;)V"))
//    private void preSetOwner(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level world, ItemStack stack, ItemStack weapon, CallbackInfo ci) {
//        ((AbstractArrow)(Object)this).setOwner(stack.getEntityRepresentation());
//    }
}
