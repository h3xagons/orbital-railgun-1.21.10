package io.github.mishkis.orbital_railgun.network.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import static io.github.mishkis.orbital_railgun.OrbitalRailgun.MOD_ID;

public record ClientSyncPayload(BlockPos pos) implements CustomPayload {

    public static final Id<ClientSyncPayload> CLIENT_SYNC_PACKET_ID = new Id<>(Identifier.of(MOD_ID, "client_synch_packet"));

    public static final PacketCodec<PacketByteBuf, ClientSyncPayload> CODEC = CustomPayload.codecOf((value, buf) -> {buf.writeBlockPos(value.pos);}, buf -> new ClientSyncPayload(buf.readBlockPos()));

    @NotNull
    @Override
    public Id<? extends CustomPayload> getId() {
        return CLIENT_SYNC_PACKET_ID;
    }
}
