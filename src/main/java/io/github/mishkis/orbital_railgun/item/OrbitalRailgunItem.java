package io.github.mishkis.orbital_railgun.item;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OrbitalRailgunItem extends Item implements GeoItem {
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = makeRenderer(this);
    public final MutableObject<GeoRenderProvider> renderProviderHolder = new MutableObject<>();

    public OrbitalRailgunItem() {
        super(new Item.Settings().rarity(Rarity.EPIC).maxCount(1));
    }

    static Supplier<Object> makeRenderer(GeoItem item) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
            return () -> null;

        return Suppliers.memoize(() -> {
            AtomicReference<Object> renderProvider = new AtomicReference<>();
            item.createGeoRenderer(renderProvider::set);
            return renderProvider.get();
        });
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 24000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getItemCooldownManager().isCoolingDown(this)) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }

        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    public void shoot(PlayerEntity user) {
        user.getItemCooldownManager().set(this, 2400);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(this.renderProviderHolder.getValue());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}
