package net.pm.magicky;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.pm.magicky.enchantment.MagickyEnchantments;
import net.pm.magicky.packet.RenameNameTagPayload;
import net.pm.magicky.screen.MagickyScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Magicky implements ModInitializer {
	public static final String MOD_ID = "magicky";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MagickyScreenHandlers.registerScreenHandlers();
		PayloadTypeRegistry.playC2S().register(RenameNameTagPayload.ID, RenameNameTagPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(RenameNameTagPayload.ID, (payload, context) -> {
			ItemStack itemStack = context.player().getItemInHand(payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			if (payload.name().isEmpty()) {
				itemStack.remove(DataComponents.CUSTOM_NAME);
			} else {
				itemStack.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty(payload.name()));
			}
		});

		MagickyEnchantments.register();
	}
}