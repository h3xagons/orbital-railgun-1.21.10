package io.github.mishkis.orbital_railgun;

import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItem;
import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItems;
import io.github.mishkis.orbital_railgun.network.packets.ClientSyncPayload;
import io.github.mishkis.orbital_railgun.network.packets.ShootPayload;
import io.github.mishkis.orbital_railgun.util.OrbitalRailgunStrikeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.logging.Logger;

public class OrbitalRailgun implements ModInitializer {
    public static final String MOD_ID = "orbital_railgun";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        OrbitalRailgunItems.initialize();
        OrbitalRailgunStrikeManager.initialize();
        LOGGER.info("Initializing " + MOD_ID);

        PayloadTypeRegistry.playS2C().register(ClientSyncPayload.CLIENT_SYNC_PACKET_ID, ClientSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShootPayload.SHOOT_PACKET_ID, ShootPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ShootPayload.SHOOT_PACKET_ID, (payload, ctx) -> {
            OrbitalRailgunItem orbitalRailgun = (OrbitalRailgunItem) payload.itemStack().getItem();
            BlockPos blockPos = payload.pos();
            ServerPlayerEntity serverPlayerEntity = ctx.player();
            ctx.server().execute(() -> {
                orbitalRailgun.shoot(serverPlayerEntity);
                List<Entity> nearby = serverPlayerEntity.getWorld().getOtherEntities(null, Box.of(blockPos.toCenterPos(), 500., 500., 500.));
                OrbitalRailgunStrikeManager.activeStrikes.put(new Pair<>(blockPos, nearby), new Pair<>(ctx.server().getTicks(), serverPlayerEntity.getWorld().getRegistryKey()));

                nearby.forEach(entry -> {
                    if (entry instanceof ServerPlayerEntity serverPlayer) {
                        ServerPlayNetworking.send(serverPlayer, new ClientSyncPayload(blockPos));
                    }
                });
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(OrbitalRailgunStrikeManager::tick);
    }
}
