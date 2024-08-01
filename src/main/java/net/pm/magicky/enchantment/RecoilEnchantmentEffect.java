package net.pm.magicky.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.pm.magicky.Magicky;

public record RecoilEnchantmentEffect(EnchantmentLevelBasedValue value) implements EnchantmentEntityEffect {
    public static final MapCodec<RecoilEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter((recoilEnchantmentEffect) -> {
            return recoilEnchantmentEffect.value;
        })).apply(instance, RecoilEnchantmentEffect::new);
    });

    public RecoilEnchantmentEffect(EnchantmentLevelBasedValue value) {
        this.value = value;
    }

    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        if(user instanceof ProjectileEntity) {
            Entity owner = ((ProjectileEntity) user).getOwner();
            if (owner != null) {
                Vec3d iVec = (new Vec3d(owner.getRotationVec(1.0f).toVector3f())).multiply(-value.getValue(level));
                owner.addVelocity(iVec.x, iVec.y, iVec.z);
                if (owner instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) owner).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(owner));
                }
            } else {
                Magicky.LOGGER.error("Cannot apply recoil because owner is null!");
            }
        }
    }

    public MapCodec<RecoilEnchantmentEffect> getCodec() {
        return CODEC;
    }

    public EnchantmentLevelBasedValue value() {
        return this.value;
    }
}
