package me.master.owleaf.mixin.client;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.EntityTags;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    private static final ThreadLocal<Entity> CURRENT_ENTITY = new ThreadLocal<>();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 0, shift = At.Shift.AFTER))
    private void injectRender0(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity) && !(entity instanceof Player) && !entity.getType().is(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

            matrices.pushPose();
            Optional<RotationAnimation> animationOptional = OwleafGravityAPI.getGravityAnimation(entity);
            if (animationOptional.isEmpty()) return;

            RotationAnimation animation = animationOptional.get();
            long timeMs = entity.level().getGameTime() * 50L + (long)(tickDelta * 50.0F);
            matrices.mulPose(new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs)).conjugate());
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 1))
    private void injectRender1(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity) && !(entity instanceof Player) && !entity.getType().is(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
            matrices.popPose();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 1, shift = At.Shift.AFTER))
    private void injectRender2(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity) && !(entity instanceof Player) && !entity.getType().is(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
            if (gravityDirection == Direction.DOWN) return;

            matrices.mulPose(RotationUtil.getCameraRotationQuaternion(gravityDirection));
        }
    }

    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void storeEntity(CallbackInfo ci) {
        Entity entity = CURRENT_ENTITY.get();
        CURRENT_ENTITY.set(entity);
    }

    @ModifyVariable(method = "renderHitbox", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;", ordinal = 0), ordinal = 0)
    private static AABB modifyRenderHitboxBox(AABB box) {
        Entity entity = CURRENT_ENTITY.get();
        if (entity == null) return box;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? box : RotationUtil.boxWorldToPlayer(box, gravityDirection);
    }

    @ModifyVariable(method = "renderHitbox", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;", ordinal = 0), ordinal = 0)
    private static Vec3 modifyRenderHitboxVec3d(Vec3 vec3d) {
        Entity entity = CURRENT_ENTITY.get();
        if (entity == null) return vec3d;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? vec3d : RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
