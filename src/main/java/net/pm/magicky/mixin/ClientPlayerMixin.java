package net.pm.magicky.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.pm.magicky.client.gui.screen.ingame.NameTagScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends PlayerMixin{
    @Final
    @Shadow
    protected MinecraftClient client;

    @Override
    public void useNameTag(Hand hand) {
        this.client.setScreen(new NameTagScreen((PlayerEntity)(Object)this, hand));
    }
}
