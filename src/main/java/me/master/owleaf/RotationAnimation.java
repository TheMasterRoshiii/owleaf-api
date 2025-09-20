package me.master.owleaf;

import me.master.owleaf.util.QuaternionUtil;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class RotationAnimation {
    private Quaternionf startGravityRotation;
    private Quaternionf endGravityRotation;
    private long startTimeMs;
    private long endTimeMs;
    private boolean inAnimation;

    public RotationAnimation() {
        this.startGravityRotation = new Quaternionf();
        this.endGravityRotation = new Quaternionf();
        this.startTimeMs = 0L;
        this.endTimeMs = 0L;
        this.inAnimation = false;
    }

    public void applyRotationAnimation(Direction newGravity, Direction prevGravity, long durationTimeMs, Entity entity, long timeMs, boolean rotateView) {
        if (durationTimeMs <= 0L) {
            this.inAnimation = false;
            return;
        }

        if (newGravity == prevGravity) {
            this.inAnimation = false;
            return;
        }

        Quaternionf newEndGravityRotation = RotationUtil.getWorldRotationQuaternion(newGravity);
        Quaternionf currentAnimatedCameraRotation = this.getCurrentGravityRotation(prevGravity, timeMs);

        if (rotateView && entity instanceof LivingEntity) {
            Vec3 newLookingDirection = this.getNewLookingDirection(newGravity, prevGravity, entity);
            Vec2 newRotation = RotationUtil.vecToRot(newLookingDirection);

            float deltaYaw = newRotation.x - entity.getYRot();
            float deltaPitch = newRotation.y - entity.getXRot();

            entity.setYRot(entity.getYRot() + deltaYaw);
            entity.setXRot(entity.getXRot() + deltaPitch);
            entity.yRotO = entity.getYRot();
            entity.xRotO = entity.getXRot();

            if (entity instanceof LivingEntity living) {
                living.yBodyRot += deltaYaw;
                living.yBodyRotO = living.yBodyRot;
                living.yHeadRot += deltaYaw;
                living.yHeadRotO = living.yHeadRot;
            }
        }

        Quaternionf newViewRotation = QuaternionUtil.getViewRotation(entity.getXRot(), entity.getYRot());
        Quaternionf animationStartGravityRotation = new Quaternionf(newViewRotation).conjugate().mul(currentAnimatedCameraRotation);

        this.inAnimation = true;
        this.startGravityRotation = animationStartGravityRotation;
        this.endGravityRotation = newEndGravityRotation;
        this.startTimeMs = timeMs;
        this.endTimeMs = timeMs + durationTimeMs;
    }

    private Vec3 getNewLookingDirection(Direction newGravity, Direction prevGravity, Entity player) {
        Vec3 oldLookingDirection = RotationUtil.vecPlayerToWorld(RotationUtil.rotToVec(player.getYRot(), player.getXRot()), prevGravity);

        if (newGravity == prevGravity.getOpposite()) {
            return oldLookingDirection.scale(-1.0);
        } else {
            Quaternionf deltaRotation = QuaternionUtil.getRotationBetween(
                    Vec3.atLowerCornerOf(prevGravity.getNormal()),
                    Vec3.atLowerCornerOf(newGravity.getNormal())
            );
            Vector3f lookingDirection = new Vector3f((float)oldLookingDirection.x, (float)oldLookingDirection.y, (float)oldLookingDirection.z);
            lookingDirection.rotate(deltaRotation);
            return new Vec3(lookingDirection.x, lookingDirection.y, lookingDirection.z);
        }
    }

    public Quaternionf getCurrentGravityRotation(Direction gravityDirection, long timeMs) {
        if (!this.inAnimation) {
            return RotationUtil.getWorldRotationQuaternion(gravityDirection);
        } else if (timeMs >= this.endTimeMs) {
            this.inAnimation = false;
            return this.endGravityRotation;
        } else {
            long totalDuration = this.endTimeMs - this.startTimeMs;
            long elapsed = timeMs - this.startTimeMs;
            float progress = (float)elapsed / (float)totalDuration;
            progress = this.easeInOutCubic(progress);
            return RotationUtil.interpolate(this.startGravityRotation, this.endGravityRotation, progress);
        }
    }

    private float easeInOutCubic(float x) {
        return x < 0.5f ? 4.0f * x * x * x : 1.0f - Mth.square(-2.0f * x + 2.0f) / 2.0f;
    }

    public boolean isInAnimation() {
        return this.inAnimation;
    }
}
