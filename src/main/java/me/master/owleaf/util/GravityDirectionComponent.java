package me.master.owleaf.util;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.packet.GravityPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class GravityDirectionComponent implements GravityComponent {
    private Direction gravityDirection;
    private Direction defaultGravityDirection;
    private Direction prevGravityDirection;
    private boolean isInverted;
    private RotationAnimation animation;
    private boolean needsInitialSync;
    private ArrayList<Gravity> gravityList;
    private final Entity entity;

    public GravityDirectionComponent(Entity entity) {
        this.gravityDirection = Direction.DOWN;
        this.defaultGravityDirection = Direction.DOWN;
        this.prevGravityDirection = Direction.DOWN;
        this.isInverted = false;
        this.animation = new RotationAnimation();
        this.needsInitialSync = false;
        this.gravityList = new ArrayList<>();
        this.entity = entity;
    }

    @Override
    public void onGravityChanged(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters, boolean initialGravity) {
        this.entity.setOnGround(false);
        this.entity.setPos(this.entity.position());

        if (!initialGravity) {
            if (!(this.entity instanceof ServerPlayer)) {
                Vec3 relativeRotationCentre = this.getCentreOfRotation(oldGravity, newGravity, rotationParameters);
                Vec3 translation = RotationUtil.vecPlayerToWorld(relativeRotationCentre, oldGravity).subtract(
                        RotationUtil.vecPlayerToWorld(relativeRotationCentre, newGravity)
                );

                Direction relativeDirection = RotationUtil.dirWorldToPlayer(newGravity, oldGravity);
                Vec3 smidge = new Vec3(
                        relativeDirection == Direction.WEST ? -1.0E-6 : 0.0,
                        relativeDirection == Direction.UP ? -1.0E-6 : 0.0,
                        relativeDirection == Direction.NORTH ? -1.0E-6 : 0.0
                );
                smidge = RotationUtil.vecPlayerToWorld(smidge, oldGravity);
                this.entity.setPos(this.entity.position().add(translation).add(smidge));
            }

            if (this.shouldChangeVelocity() && !rotationParameters.alternateCenter()) {
                this.adjustEntityPosition(oldGravity, newGravity);
            }

            if (this.shouldChangeVelocity()) {
                Vec3 realWorldVelocity = this.getRealWorldVelocity(this.entity, this.prevGravityDirection);

                if (rotationParameters.rotateVelocity()) {
                    Vector3f worldSpaceVec = realWorldVelocity.toVector3f();
                    worldSpaceVec.rotate(RotationUtil.getRotationBetween(this.prevGravityDirection, this.gravityDirection));
                    this.entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(new Vec3(worldSpaceVec), this.gravityDirection));
                } else {
                    this.entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(realWorldVelocity, this.gravityDirection));
                }
            }
        }
    }

    private Vec3 getRealWorldVelocity(Entity entity, Direction prevGravityDirection) {
        return entity.verticalCollision ?
                new Vec3(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo) :
                RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), prevGravityDirection);
    }

    private boolean shouldChangeVelocity() {
        if (this.entity instanceof ItemEntity) return true;
        if (this.entity instanceof FallingBlockEntity) return true;
        return !(this.entity instanceof Projectile);
    }

    @NotNull
    private Vec3 getCentreOfRotation(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters) {
        Vec3 relativeRotationCentre = Vec3.ZERO;

        if (this.entity instanceof Boat) {
            relativeRotationCentre = new Vec3(0.0, -0.5, 0.0);
        } else if (rotationParameters.alternateCenter()) {
            net.minecraft.world.entity.EntityDimensions dimensions = this.entity.getDimensions(this.entity.getPose());

            if (newGravity.getOpposite() == oldGravity) {
                relativeRotationCentre = new Vec3(0.0, dimensions.height / 2.0, 0.0);
            } else {
                relativeRotationCentre = new Vec3(0.0, dimensions.width / 2.0, 0.0);
            }
        }
        return relativeRotationCentre;
    }

    private void adjustEntityPosition(Direction oldGravity, Direction newGravity) {
        if (!(this.entity instanceof FallingBlockEntity) &&
                !(this.entity instanceof AbstractArrow) &&
                !(this.entity instanceof Boat)) {

            AABB entityBoundingBox = this.entity.getBoundingBox();
            Direction movingDirection = oldGravity.getOpposite();

            Iterable<VoxelShape> collisions = this.entity.level().getEntityCollisions(this.entity, entityBoundingBox);
            AABB totalCollisionBox = null;

            for (VoxelShape collision : collisions) {
                if (!collision.isEmpty()) {
                    AABB boundingBox = collision.bounds();
                    if (totalCollisionBox == null) {
                        totalCollisionBox = boundingBox;
                    } else {
                        totalCollisionBox = totalCollisionBox.minmax(boundingBox);
                    }
                }
            }

            if (totalCollisionBox != null) {
                this.entity.setPos(this.entity.position().add(
                        getPositionAdjustmentOffset(entityBoundingBox, totalCollisionBox, movingDirection)
                ));
            }
        }
    }

    private static Vec3 getPositionAdjustmentOffset(AABB entityBoundingBox, AABB nearbyCollisionUnion, Direction movingDirection) {
        Direction.Axis axis = movingDirection.getAxis();
        double offset = 0.0;

        if (movingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            double pushing = nearbyCollisionUnion.min(axis);
            double pushed = entityBoundingBox.max(axis);
            if (pushing < pushed) {
                offset = pushing - pushed;
            }
        } else {
            double pushing = nearbyCollisionUnion.max(axis);
            double pushed = entityBoundingBox.min(axis);
            if (pushing > pushed) {
                offset = pushed - pushing;
            }
        }

        return new Vec3(movingDirection.getNormal()).scale(offset);
    }

    @Override
    public Direction getGravityDirection() {
        return this.canChangeGravity() ? this.gravityDirection : Direction.DOWN;
    }

    private boolean canChangeGravity() {
        return EntityTags.canChangeGravity(this.entity);
    }

    @Override
    public Direction getPrevGravityDirection() {
        return this.canChangeGravity() ? this.prevGravityDirection : Direction.DOWN;
    }

    @Override
    public Direction getDefaultGravityDirection() {
        return this.canChangeGravity() ? this.defaultGravityDirection : Direction.DOWN;
    }

    @Override
    public void updateGravity(RotationParameters rotationParameters, boolean initialGravity) {
        if (this.canChangeGravity()) {
            Direction newGravity = this.getActualGravityDirection();
            Direction oldGravity = this.gravityDirection;

            if (oldGravity != newGravity) {
                long timeMs = this.entity.level().getGameTime() * 50L;

                if (!this.entity.level().isClientSide) {
                    this.animation.applyRotationAnimation(
                            newGravity, oldGravity,
                            initialGravity ? 0L : (long)rotationParameters.rotationTime(),
                            this.entity, timeMs, rotationParameters.rotateView()
                    );
                }

                this.prevGravityDirection = oldGravity;
                this.gravityDirection = newGravity;
                this.onGravityChanged(oldGravity, newGravity, rotationParameters, initialGravity);
            }
        }
    }

    @Override
    public Direction getActualGravityDirection() {
        Direction newGravity = this.getDefaultGravityDirection();
        Gravity highestPriority = this.getHighestPriority();

        if (highestPriority != null) {
            newGravity = highestPriority.direction();
        }

        if (this.isInverted) {
            newGravity = newGravity.getOpposite();
        }

        return newGravity;
    }

    @Nullable
    private Gravity getHighestPriority() {
        return !this.gravityList.isEmpty() ?
                Collections.max(this.gravityList, Comparator.comparingInt(Gravity::priority)) :
                null;
    }

    @Override
    public void setDefaultGravityDirection(Direction gravityDirection, RotationParameters rotationParameters, boolean initialGravity) {
        if (this.canChangeGravity()) {
            this.defaultGravityDirection = gravityDirection;
            this.updateGravity(rotationParameters, initialGravity);
        }
    }

    @Override
    public void addGravity(Gravity gravity, boolean initialGravity) {
        if (this.canChangeGravity()) {
            this.gravityList.removeIf(g -> Objects.equals(g.source(), gravity.source()));
            if (gravity.direction() != null) {
                this.gravityList.add(gravity);
            }
            this.updateGravity(gravity.rotationParameters(), initialGravity);
        }
    }

    @Override
    public ArrayList<Gravity> getGravity() {
        return this.gravityList;
    }

    @Override
    public void setGravity(ArrayList<Gravity> gravityList, boolean initialGravity) {
        Gravity highestBefore = this.getHighestPriority();
        this.gravityList = gravityList;
        Gravity highestAfter = this.getHighestPriority();

        RotationParameters rp = new RotationParameters();

        if (highestBefore != highestAfter) {
            if (highestBefore == null) {
                rp = highestAfter != null ? highestAfter.rotationParameters() : new RotationParameters();
            } else if (highestAfter == null) {
                rp = highestBefore.rotationParameters();
            } else if (highestBefore.priority() != highestAfter.priority()) {
                rp = highestBefore.rotationParameters();
            } else {
                rp = highestAfter.rotationParameters();
            }
            this.updateGravity(rp, initialGravity);
        }
    }

    @Override
    public void invertGravity(boolean isInverted, RotationParameters rotationParameters, boolean initialGravity) {
        this.isInverted = isInverted;
        this.updateGravity(rotationParameters, initialGravity);
    }

    @Override
    public boolean getInvertGravity() {
        return this.isInverted;
    }

    @Override
    public void removeGravity(Gravity gravity) {
        this.gravityList.removeIf(g -> Objects.equals(g.source(), gravity.source()) &&
                g.priority() == gravity.priority() &&
                g.direction() == gravity.direction());
    }

    @Override
    public boolean hasGravity(Gravity gravity) {
        return this.gravityList.stream().anyMatch(g ->
                Objects.equals(g.source(), gravity.source()) &&
                        g.priority() == gravity.priority() &&
                        g.direction() == gravity.direction()
        );
    }

    @Override
    public void clearGravity(RotationParameters rotationParameters, boolean initialGravity) {
        this.gravityList.clear();
        this.updateGravity(rotationParameters, initialGravity);
    }

    @Override
    public RotationAnimation getRotationAnimation() {
        return this.animation;
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        Direction oldDefaultGravity = this.defaultGravityDirection;
        ArrayList<Gravity> oldList = new ArrayList<>(this.gravityList);
        boolean oldIsInverted = this.isInverted;

        if (nbt.contains("ListSize", 3)) {
            int listSize = nbt.getInt("ListSize");
            ArrayList<Gravity> newGravityList = new ArrayList<>();

            if (listSize != 0) {
                for (int index = 0; index < listSize; index++) {
                    Gravity newGravity = new Gravity(
                            Direction.from3DDataValue(nbt.getInt("GravityDirection" + index)),
                            nbt.getInt("GravityPriority" + index),
                            nbt.getInt("GravityDuration" + index),
                            nbt.getString("GravitySource" + index),
                            new RotationParameters()
                    );
                    newGravityList.add(newGravity);
                }
            }

            this.gravityList = newGravityList;
        }

        this.prevGravityDirection = Direction.from3DDataValue(nbt.getInt("PrevGravityDirection"));
        this.defaultGravityDirection = Direction.from3DDataValue(nbt.getInt("DefaultGravityDirection"));
        this.isInverted = nbt.getBoolean("IsGravityInverted");

        RotationParameters rp = new RotationParameters(false, false, false, 0);
        this.updateGravity(rp, true);

        if (oldDefaultGravity != this.defaultGravityDirection ||
                !oldList.equals(this.gravityList) ||
                oldIsInverted != this.isInverted) {
            this.needsInitialSync = true;
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag nbt) {
        int index = 0;
        for (Gravity temp : this.getGravity()) {
            if (temp.direction() != null && temp.source() != null) {
                nbt.putInt("GravityDirection" + index, temp.direction().get3DDataValue());
                nbt.putInt("GravityPriority" + index, temp.priority());
                nbt.putInt("GravityDuration" + index, temp.duration());
                nbt.putString("GravitySource" + index, temp.source());
                index++;
            }
        }

        nbt.putInt("ListSize", index);
        nbt.putInt("PrevGravityDirection", this.getPrevGravityDirection().get3DDataValue());
        nbt.putInt("DefaultGravityDirection", this.getDefaultGravityDirection().get3DDataValue());
        nbt.putBoolean("IsGravityInverted", this.getInvertGravity());
    }

    @Override
    public void tick() {
        Entity vehicle = this.entity.getVehicle();
        if (vehicle != null) {
            this.addGravity(new Gravity(
                    OwleafGravityAPI.getGravityDirection(vehicle),
                    99999999, 2, "vehicle", new RotationParameters()
            ), true);
        }

        ArrayList<Gravity> gravityList = this.getGravity();
        Gravity highestBefore = this.getHighestPriority();

        if (gravityList.removeIf(g -> g.duration() <= 0) && highestBefore != null) {
            this.updateGravity(highestBefore.rotationParameters(), false);
        }

        for (Gravity temp : gravityList) {
            if (temp.duration() > 0) {
                temp.decrementDuration();
            }
        }

        if (!this.entity.level().isClientSide && this.needsInitialSync) {
            this.needsInitialSync = false;
            RotationParameters rotationParameters = new RotationParameters(false, false, false, 0);
            GravityChannel.sendFullStatePacket(this.entity, GravityChannel.PacketMode.EVERYONE, rotationParameters, true);
        }
    }
}
