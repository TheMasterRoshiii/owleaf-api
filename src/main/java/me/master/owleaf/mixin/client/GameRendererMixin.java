package me.master.owleaf.mixin.client;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final private Camera mainCamera;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 3, shift = At.Shift.AFTER))
    private void injectRenderLevel(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        if (this.mainCamera.getEntity() != null) {
            Entity focusedEntity = this.mainCamera.getEntity();
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(focusedEntity);

            Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(focusedEntity);
            if (animationOptional.isEmpty()) return;

            RotationAnimation animation = animationOptional.get();
            long timeMs = focusedEntity.level().getGameTime() * 50L + (long)(tickDelta * 50.0F);

            Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);
            matrix.mulPose(currentGravityRotation);
        }
    }
}
