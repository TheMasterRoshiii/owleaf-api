package me.master.owleaf.mixin.client;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 0, shift = At.Shift.AFTER))
    private void injectRender(AbstractClientPlayer player, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);

        if (gravityDirection != Direction.DOWN) {
            Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(player);
            if (animationOptional.isPresent()) {
                RotationAnimation animation = animationOptional.get();
                long timeMs = player.level().getGameTime() * 50L + (long)(tickDelta * 50.0F);

                Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
                rotation.conjugate();
                matrixStack.mulPose(rotation);
                matrixStack.mulPose(RotationUtil.getCameraRotationQuaternion(gravityDirection));
            }
        }
    }
}
