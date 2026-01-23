package io.github.mishkis.orbital_railgun.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyExpressionValue(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"))
    public boolean smoothCursorOnAim(boolean original) {
        if (original) return true;
        assert this.client.player != null;
        return this.client.player.getActiveItem().getItem() instanceof OrbitalRailgunItem;
    }
}
