package me.master.owleaf.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameOverlayRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At("HEAD")
    )
    private void owleaf_onRenderHead(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);

        if (gravityDirection != Direction.DOWN) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(guiGraphics.guiWidth() / 2.0, guiGraphics.guiHeight() / 2.0, 0);
            poseStack.mulPose(RotationUtil.getRotationBetween(Direction.DOWN, gravityDirection));
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

        if (OwleafGravityAPI.getGravityDirection(player) != Direction.DOWN) {
            guiGraphics.pose().popPose();
        }
    }
}