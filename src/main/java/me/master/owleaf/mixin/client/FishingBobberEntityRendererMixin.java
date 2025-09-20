package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHookRenderer.class)
public abstract class FishingBobberEntityRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void injectRender(FishingHook fishingBobberEntity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
        Player playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity == null) return;

        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) return;

        matrixStack.pushPose();

        Vec3 lineStart = new Vec3(
                Mth.lerp(tickDelta, playerEntity.xo, playerEntity.getX()),
                Mth.lerp(tickDelta, playerEntity.yo, playerEntity.getY()) + playerEntity.getEyeHeight(),
                Mth.lerp(tickDelta, playerEntity.zo, playerEntity.getZ())
        );
        lineStart = lineStart.add(RotationUtil.vecPlayerToWorld(
                -Mth.cos(playerEntity.yBodyRot * 0.017453292F) * 0.8,
                playerEntity.isShiftKeyDown() ? -0.1875F : 0.0F - 0.45,
                -Mth.sin(playerEntity.yBodyRot * 0.017453292F) * 0.8,
                gravityDirection
        ));

        double bobberX = Mth.lerp(tickDelta, fishingBobberEntity.xo, fishingBobberEntity.getX());
        double bobberY = Mth.lerp(tickDelta, fishingBobberEntity.yo, fishingBobberEntity.getY()) + 0.25F;
        double bobberZ = Mth.lerp(tickDelta, fishingBobberEntity.zo, fishingBobberEntity.getZ());

        float relX = (float)(lineStart.x - bobberX);
        float relY = (float)(lineStart.y - bobberY);
        float relZ = (float)(lineStart.z - bobberZ);

        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(net.minecraft.client.renderer.RenderType.lineStrip());
        PoseStack.Pose entry2 = matrixStack.last();

        for (int i = 0; i <= 16; ++i) {
            renderPart(relX, relY, relZ, vertexConsumer2, entry2, fraction(i, 16), fraction(i + 1, 16));
        }

        matrixStack.popPose();
    }

    private static float fraction(int index, int total) {
        return (float)index / (float)total;
    }

    private static void renderPart(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose matrices, float segmentStart, float segmentEnd) {
        float lerpedX = x * segmentStart;
        float lerpedY = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float lerpedZ = z * segmentStart;

        vertexConsumer.vertex(matrices.pose(), lerpedX, lerpedY, lerpedZ)
                .color(0, 0, 0, 255)
                .endVertex();
    }
}
