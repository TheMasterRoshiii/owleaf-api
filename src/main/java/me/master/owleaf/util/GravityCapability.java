package me.master.owleaf.util;

import me.master.owleaf.OwleafApiMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = OwleafApiMod.MOD_ID)
public class GravityCapability {
    public static final Capability<GravityComponent> GRAVITY_COMPONENT = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation GRAVITY_CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(OwleafApiMod.MOD_ID, "gravity_direction");

    public static void register() {
        // Registration is handled automatically by Forge for CapabilityTokens in 1.20.1
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (EntityTags.canChangeGravity(event.getObject())) {
            GravityCapabilityProvider provider = new GravityCapabilityProvider(event.getObject());
            event.addCapability(GRAVITY_CAPABILITY_ID, provider);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        Optional<GravityComponent> oldCap = getGravityComponent(event.getOriginal());
        Optional<GravityComponent> newCap = getGravityComponent(event.getEntity());

        if (oldCap.isPresent() && newCap.isPresent()) {
            CompoundTag nbt = new CompoundTag();
            oldCap.get().writeToNbt(nbt);
            newCap.get().readFromNbt(nbt);
        }
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (EntityTags.canChangeGravity(event.getEntity())) {
            getGravityComponent(event.getEntity()).ifPresent(GravityComponent::tick);
        }
    }

    public static Optional<GravityComponent> getGravityComponent(Entity entity) {
        if (entity == null) return Optional.empty();
        return entity.getCapability(GRAVITY_COMPONENT).resolve();
    }

    public static class GravityCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
        private final GravityDirectionComponent gravityComponent;
        private final LazyOptional<GravityComponent> lazyOptional;

        public GravityCapabilityProvider(Entity entity) {
            this.gravityComponent = new GravityDirectionComponent(entity);
            this.lazyOptional = LazyOptional.of(() -> gravityComponent);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == GRAVITY_COMPONENT ? lazyOptional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            gravityComponent.writeToNbt(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            gravityComponent.readFromNbt(nbt);
        }

        public void invalidate() {
            lazyOptional.invalidate();
        }
    }
}
