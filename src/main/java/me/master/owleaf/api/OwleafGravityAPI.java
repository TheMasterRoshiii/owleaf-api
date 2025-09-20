package me.master.owleaf.api;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.util.Gravity;
import me.master.owleaf.util.GravityCapability;
import me.master.owleaf.util.GravityChannel;
import me.master.owleaf.util.GravityComponent;
import me.master.owleaf.util.EntityTags;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import me.master.owleaf.util.RotationUtil;
import java.util.ArrayList;
import java.util.Optional;

public class OwleafGravityAPI {

    public static void init() {
        // Initialization logic if needed
    }

    public static Direction getGravityDirection(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getGravityDirection)
                        .orElse(Direction.DOWN) : Direction.DOWN;
    }

    public static ArrayList<Gravity> getGravityList(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getGravity)
                        .orElse(new ArrayList<>()) : new ArrayList<>();
    }

    public static Direction getPrevGravityDirection(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getPrevGravityDirection)
                        .orElse(Direction.DOWN) : Direction.DOWN;
    }

    public static Direction getDefaultGravityDirection(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getDefaultGravityDirection)
                        .orElse(Direction.DOWN) : Direction.DOWN;
    }

    public static Direction getActualGravityDirection(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getActualGravityDirection)
                        .orElse(Direction.DOWN) : Direction.DOWN;
    }

    public static boolean getIsInverted(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getInvertGravity)
                        .orElse(false) : false;
    }

    public static Optional<RotationAnimation> getGravityAnimation(Entity entity) {
        return EntityTags.canChangeGravity(entity) ?
                GravityCapability.getGravityComponent(entity)
                        .map(GravityComponent::getRotationAnimation) : Optional.empty();
    }

    public static void addGravity(Entity entity, Gravity gravity) {
        if (onCorrectSide(entity, true) && EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                gc.addGravity(gravity, false);
            });
            GravityChannel.sendToClient(entity, new me.master.owleaf.util.packet.UpdateGravityPacket(gravity, false), GravityChannel.PacketMode.EVERYONE);
        }
    }

    public static void updateGravity(Entity entity) {
        updateGravity(entity, new RotationParameters());
    }

    public static void updateGravity(Entity entity, RotationParameters rotationParameters) {
        if (EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc ->
                    gc.updateGravity(rotationParameters, false)
            );
        }
    }

    public static void setGravity(Entity entity, ArrayList<Gravity> gravity) {
        if (onCorrectSide(entity, true) && EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                gc.setGravity(gravity, false);
            });
            GravityChannel.sendToClient(entity, new me.master.owleaf.util.packet.OverwriteGravityPacket(gravity, false), GravityChannel.PacketMode.EVERYONE);
        }
    }

    public static void setIsInverted(Entity entity, boolean isInverted) {
        setIsInverted(entity, isInverted, new RotationParameters());
    }

    public static void setIsInverted(Entity entity, boolean isInverted, RotationParameters rotationParameters) {
        if (onCorrectSide(entity, true) && EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                gc.invertGravity(isInverted, rotationParameters, false);
            });
            GravityChannel.sendToClient(entity, new me.master.owleaf.util.packet.InvertGravityPacket(isInverted, rotationParameters, false), GravityChannel.PacketMode.EVERYONE);
        }
    }

    public static void clearGravity(Entity entity) {
        clearGravity(entity, new RotationParameters());
    }

    public static void clearGravity(Entity entity, RotationParameters rotationParameters) {
        if (onCorrectSide(entity, true) && EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                gc.clearGravity(rotationParameters, false);
            });
            GravityChannel.sendToClient(entity, new me.master.owleaf.util.packet.OverwriteGravityPacket(new ArrayList<>(), false), GravityChannel.PacketMode.EVERYONE);
        }
    }

    @Deprecated
    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection, int animationDurationMs) {
        setDefaultGravityDirection(entity, gravityDirection, new RotationParameters(animationDurationMs));
    }

    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection) {
        setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
    }

    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection, RotationParameters rotationParameters) {
        if (onCorrectSide(entity, true) && EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
            });
            GravityChannel.sendToClient(entity, new me.master.owleaf.util.packet.DefaultGravityPacket(gravityDirection, rotationParameters, false), GravityChannel.PacketMode.EVERYONE);
        }
    }

    public static GravityComponent getGravityComponent(Entity entity) {
        return GravityCapability.getGravityComponent(entity).orElse(null);
    }

    public static Vec3 getWorldVelocity(Entity playerEntity) {
        return RotationUtil.vecPlayerToWorld(playerEntity.getDeltaMovement(), getGravityDirection(playerEntity));
    }

    public static void setWorldVelocity(Entity entity, Vec3 worldVelocity) {
        entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(worldVelocity, getGravityDirection(entity)));
    }

    public static Vec3 getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0.0, entity.getEyeHeight(), 0.0, getGravityDirection(entity));
    }

    private static boolean onCorrectSide(Entity entity, boolean shouldBeOnServer) {
        if (entity.level().isClientSide == shouldBeOnServer) {
            return false;
        } else if (!entity.level().isClientSide && !shouldBeOnServer) {
            return false;
        } else {
            return true;
        }
    }
}
