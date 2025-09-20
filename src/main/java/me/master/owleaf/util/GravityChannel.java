package me.master.owleaf.util;

import me.master.owleaf.OwleafApiMod;
import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public class GravityChannel {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OwleafApiMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void init() {
        INSTANCE.messageBuilder(DefaultGravityPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DefaultGravityPacket::new)
                .encoder(DefaultGravityPacket::write)
                .consumerMainThread(GravityChannel::handleDefaultGravity)
                .add();

        INSTANCE.messageBuilder(UpdateGravityPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UpdateGravityPacket::new)
                .encoder(UpdateGravityPacket::write)
                .consumerMainThread(GravityChannel::handleUpdateGravity)
                .add();

        INSTANCE.messageBuilder(InvertGravityPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InvertGravityPacket::new)
                .encoder(InvertGravityPacket::write)
                .consumerMainThread(GravityChannel::handleInvertGravity)
                .add();

        INSTANCE.messageBuilder(OverwriteGravityPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OverwriteGravityPacket::new)
                .encoder(OverwriteGravityPacket::write)
                .consumerMainThread(GravityChannel::handleOverwriteGravity)
                .add();
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        // Client-specific initialization if needed
    }

    public static void sendToClient(Entity entity, GravityPacket packet, PacketMode mode) {
        if (entity.level().isClientSide) return;

        packet.entityId = entity.getId();

        switch (mode) {
            case EVERYONE -> INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
            case EVERYONE_BUT_SELF -> {
                if (entity instanceof ServerPlayer player) {
                    INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
                } else {
                    INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
                }
            }
            case ONLY_SELF -> {
                if (entity instanceof ServerPlayer player) {
                    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
                }
            }
        }
    }

    public static void sendFullStatePacket(Entity entity, PacketMode mode, RotationParameters rp, boolean initialGravity) {
        Optional<GravityComponent> gc = GravityCapability.getGravityComponent(entity);
        if (gc.isPresent()) {
            GravityComponent component = gc.get();
            sendToClient(entity, new OverwriteGravityPacket(component.getGravity(), initialGravity), mode);
            sendToClient(entity, new DefaultGravityPacket(component.getDefaultGravityDirection(), rp, initialGravity), mode);
            sendToClient(entity, new InvertGravityPacket(component.getInvertGravity(), rp, initialGravity), mode);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleDefaultGravity(DefaultGravityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (entity != null) {
                GravityCapability.getGravityComponent(entity).ifPresent(gc ->
                        gc.setDefaultGravityDirection(packet.direction, packet.rotationParameters, packet.initialGravity)
                );
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleUpdateGravity(UpdateGravityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (entity != null) {
                GravityCapability.getGravityComponent(entity).ifPresent(gc ->
                        gc.addGravity(packet.gravity, packet.initialGravity)
                );
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleInvertGravity(InvertGravityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (entity != null) {
                GravityCapability.getGravityComponent(entity).ifPresent(gc ->
                        gc.invertGravity(packet.inverted, packet.rotationParameters, packet.initialGravity)
                );
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOverwriteGravity(OverwriteGravityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (entity != null) {
                GravityCapability.getGravityComponent(entity).ifPresent(gc ->
                        gc.setGravity(packet.gravityList, packet.initialGravity)
                );
            }
        });
        context.setPacketHandled(true);
    }

    public enum PacketMode {
        EVERYONE,
        EVERYONE_BUT_SELF,
        ONLY_SELF
    }
}
