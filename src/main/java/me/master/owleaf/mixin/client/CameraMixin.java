package me.master.owleaf.mixin.client;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private Entity entity;
    @Shadow @Final private Quaternionf rotation;
    @Shadow private float yRot;
    @Shadow private float xRot;
    private float storedTickDelta = 0.0F;

    @Shadow protected abstract void setPosition(double x, double y, double z);

    @Inject(method = "setup", at = @At("HEAD"))
    private void injectSetup(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        this.storedTickDelta = tickDelta;
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", ordinal = 0))
    private void redirectSetupSetPosition(Camera camera, double x, double y, double z, BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(focusedEntity);
        Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(focusedEntity);

        if (animationOptional.isEmpty()) {
            this.setPosition(x, y, z);
        } else {
            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
                this.setPosition(x, y, z);
            } else {
                long timeMs = focusedEntity.level().getGameTime() * 50L + (long)(this.storedTickDelta * 50.0F);
                Quaternionf gravityRotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
                gravityRotation.conjugate();

                double entityX = Mth.lerp((double)tickDelta, focusedEntity.xo, focusedEntity.getX());
                double entityY = Mth.lerp((double)tickDelta, focusedEntity.yo, focusedEntity.getY());
                double entityZ = Mth.lerp((double)tickDelta, focusedEntity.zo, focusedEntity.getZ());
                double currentCameraY = (double)Mth.lerp(tickDelta, this.yRot, this.xRot);

                Vector3f eyeOffset = new Vector3f(0.0F, (float)currentCameraY, 0.0F);
                eyeOffset.rotate(gravityRotation);

                this.setPosition(entityX + (double)eyeOffset.x, entityY + (double)eyeOffset.y, entityZ + (double)eyeOffset.z);
            }
        }
    }

    @Inject(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", shift = At.Shift.AFTER))
    private void injectSetRotation(CallbackInfo ci) {
        if (this.entity != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.entity);
            Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(this.entity);

            if (animationOptional.isEmpty()) return;

            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) return;

            long timeMs = this.entity.level().getGameTime() * 50L + (long)(this.storedTickDelta * 50.0F);

            Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
            rotation.conjugate();
            rotation.mul(this.rotation);
            this.rotation.set(rotation.x, rotation.y, rotation.z, rotation.w);
        }
    }
}
