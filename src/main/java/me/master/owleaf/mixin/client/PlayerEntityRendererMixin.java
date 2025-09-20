package me.master.owleaf.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At("TAIL")
    )
    private void inject_setupRotations(AbstractClientPlayer player, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);

        if (gravityDirection != Direction.DOWN) {
            Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(player);
            if (animationOptional.isPresent()) {
                RotationAnimation animation = animationOptional.get();
                long timeMs = player.level().getGameTime() * 50L + (long) (partialTicks * 50.0F);

                Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
                rotation.conjugate();
                matrixStack.mulPose(rotation);
                matrixStack.mulPose(RotationUtil.getCameraRotationQuaternion(gravityDirection));
            }
        }
    }
}