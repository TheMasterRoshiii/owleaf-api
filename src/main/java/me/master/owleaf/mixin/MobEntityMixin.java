package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public abstract class MobEntityMixin {

    private float getRotatedYaw(Mob attacker, Entity target) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? attacker.getYRot() :
                RotationUtil.rotWorldToPlayer(attacker.getYRot(), attacker.getXRot(), gravityDirection).x;
    }

    @Redirect(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F", ordinal = 0))
    private float redirectDoHurtTargetGetYaw0(Mob attacker, Entity target) {
        return getRotatedYaw(attacker, target);
    }

    @Redirect(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F", ordinal = 1))
    private float redirectDoHurtTargetGetYaw1(Mob attacker, Entity target) {
        return getRotatedYaw(attacker, target);
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D", ordinal = 0))
    private double redirectLookAtGetEyeY0(net.minecraft.world.entity.LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getEyeY() :
                livingEntity.getEyePosition().y;
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getX()D", ordinal = 0))
    private double redirectLookAtGetX0(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getX() :
                entity.getEyePosition().x;
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getZ()D", ordinal = 0))
    private double redirectLookAtGetZ0(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getZ() :
                entity.getEyePosition().z;
    }
}