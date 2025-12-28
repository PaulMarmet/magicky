package net.pm.magicky.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.pm.magicky.Magicky;

public record RenameNameTagPayload(String name, Boolean mainHand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RenameNameTagPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "rename_name_tag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RenameNameTagPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, RenameNameTagPayload::name, ByteBufCodecs.BOOL, RenameNameTagPayload::mainHand, RenameNameTagPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
