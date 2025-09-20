package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLookForPlayerGoal")
public abstract class EndermanEntityChasePlayerGoalMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D", ordinal = 0))
    private double redirectTickGetEyeY0(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getEyeY() :
                livingEntity.getEyePosition().y;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getX()D", ordinal = 0))
    private double redirectTickGetX0(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getX() :
                livingEntity.getEyePosition().x;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D", ordinal = 0))
    private double redirectTickGetZ0(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getZ() :
                livingEntity.getEyePosition().z;
    }
}
