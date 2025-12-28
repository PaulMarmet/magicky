package net.pm.magicky.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.pm.magicky.Magicky;

public record RecoilEnchantmentEffect(LevelBasedValue value) implements EnchantmentEntityEffect {
    public static final MapCodec<RecoilEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(LevelBasedValue.CODEC.fieldOf("value").forGetter((recoilEnchantmentEffect) -> {
            return recoilEnchantmentEffect.value;
        })).apply(instance, RecoilEnchantmentEffect::new);
    });

    public RecoilEnchantmentEffect(LevelBasedValue value) {
        this.value = value;
    }

    public void apply(ServerLevel world, int level, EnchantedItemInUse context, Entity user, Vec3 pos) {
        if(user instanceof Projectile) {
            Entity owner = ((Projectile) user).getOwner();
            if (owner != null) {
                Vec3 iVec = (new Vec3(owner.getViewVector(1.0f).toVector3f())).scale(-value.calculate(level));
                owner.push(iVec.x, iVec.y, iVec.z);
                if (owner instanceof ServerPlayer) {
                    ((ServerPlayer) owner).connection.send(new ClientboundSetEntityMotionPacket(owner));
                }
            } else {
                Magicky.LOGGER.error("Cannot apply recoil because owner is null!");
            }
        }
    }

    public MapCodec<RecoilEnchantmentEffect> codec() {
        return CODEC;
    }

    public LevelBasedValue value() {
        return this.value;
    }
}
