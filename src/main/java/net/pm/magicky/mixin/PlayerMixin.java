package net.pm.magicky.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.pm.magicky.screen.NameTagInt;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements NameTagInt {

    @Override
    public void magicky$useNameTag(InteractionHand hand) {

    }
}
