package net.pm.magicky.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;

public record RenameNameTagPayload(String name, Boolean mainHand) implements CustomPayload {
    public static final CustomPayload.Id<RenameNameTagPayload> ID = new CustomPayload.Id<>(Identifier.of(Magicky.MOD_ID, "rename_name_tag"));
    public static final PacketCodec<RegistryByteBuf, RenameNameTagPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, RenameNameTagPayload::name, PacketCodecs.BOOL, RenameNameTagPayload::mainHand, RenameNameTagPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
