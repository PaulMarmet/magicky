package net.pm.magicky.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.pm.magicky.client.gui.screen.ingame.NameTagScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class ClientPlayerMixin extends PlayerMixin{
    @Final
    @Shadow
    protected Minecraft minecraft;

    @Override
    public void magicky$useNameTag(InteractionHand hand) {
        this.minecraft.setScreen(new NameTagScreen((Player)(Object)this, hand));
    }
}
