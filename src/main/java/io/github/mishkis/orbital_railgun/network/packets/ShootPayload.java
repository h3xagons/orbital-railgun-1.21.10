package io.github.mishkis.orbital_railgun.network.packets;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import static io.github.mishkis.orbital_railgun.OrbitalRailgun.MOD_ID;

public record ShootPayload(ItemStack itemStack, BlockPos pos) implements CustomPayload {

    public static final Id<ShootPayload> SHOOT_PACKET_ID = new Id<>(Identifier.of(MOD_ID, "shoot_packet"));

    public static final PacketCodec<PacketByteBuf, ShootPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        ItemStack.PACKET_CODEC.encode((RegistryByteBuf) buf, value.itemStack());
        buf.writeBlockPos(value.pos());
    }, buf -> new ShootPayload(ItemStack.PACKET_CODEC.decode((RegistryByteBuf) buf), buf.readBlockPos()));

    @NotNull
    @Override
    public Id<? extends CustomPayload> getId() {
        return SHOOT_PACKET_ID;
    }
}
