package me.master.owleaf.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin<T extends Entity> {

    @Inject(

            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void onRenderNameTag(T entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        if (gravityDirection != Direction.DOWN) {


            RotationUtil.applyGravityRotation(poseStack, gravityDirection);
        }
    }
}