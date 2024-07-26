package net.pm.magicky.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.pm.magicky.screen.NameTagInt;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements NameTagInt {

    @Override
    public void useNameTag(Hand hand) {

    }
}
