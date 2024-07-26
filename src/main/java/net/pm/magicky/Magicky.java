package net.pm.magicky;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
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
			ItemStack itemStack = context.player().getStackInHand(payload.mainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND);
			if (payload.name().isEmpty()) {
				itemStack.remove(DataComponentTypes.CUSTOM_NAME);
			} else {
				itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of(payload.name()));
			}
			LOGGER.info(payload.name());
		});
	}
}