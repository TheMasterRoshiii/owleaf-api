package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanFreezeWhenLookedAt")
public abstract class EndermanEntityChasePlayerGoalMixin {

    private Vec3 getRotatedEyePosition(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getEyePosition() :
                new Vec3(livingEntity.getEyePosition().x, livingEntity.getEyePosition().y, livingEntity.getEyePosition().z);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D"))
    private double redirectTickGetEyeY0(LivingEntity livingEntity) {
        return getRotatedEyePosition(livingEntity).y;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"))
    private double redirectTickGetX0(LivingEntity livingEntity) {
        return getRotatedEyePosition(livingEntity).x;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D"))
    private double redirectTickGetZ0(LivingEntity livingEntity) {
        return getRotatedEyePosition(livingEntity).z;
    }
}