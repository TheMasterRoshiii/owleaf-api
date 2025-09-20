package me.master.owleaf.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Gui.class)
public abstract class InGameOverlayRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At("HEAD")
    )
    private void owleaf_onRenderHead(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(player);
        if (animationOptional.isEmpty()) return;

        RotationAnimation animation = animationOptional.get();
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);


        if (gravityDirection != Direction.DOWN || animation.isInAnimation()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();


            long timeMs = player.level().getGameTime() * 50L + (long)(partialTicks * 50.0F);
            Quaternionf currentRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);


            poseStack.translate(guiGraphics.guiWidth() / 2.0, guiGraphics.guiHeight() / 2.0, 0);
            poseStack.mulPose(currentRotation);
            poseStack.translate(-guiGraphics.guiWidth() / 2.0, -guiGraphics.guiHeight() / 2.0, 0);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At("TAIL")
    )
    private void owleaf_onRenderTail(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(player);
        if (animationOptional.isEmpty()) return;

        RotationAnimation animation = animationOptional.get();
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);


        if (gravityDirection != Direction.DOWN || animation.isInAnimation()) {
            guiGraphics.pose().popPose();
        }
    }
}