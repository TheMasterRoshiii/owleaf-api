package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Drowned.class)
public abstract class DrownedEntityMixin {

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getX()D", ordinal = 0))
    private double redirectPerformRangedAttackGetX0(LivingEntity target) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? target.getX() :
                target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection)).x;
    }

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D", ordinal = 0))
    private double redirectPerformRangedAttackGetBodyY0(LivingEntity target, double heightScale) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? target.getY(heightScale) :
                target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection)).y;
    }

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D", ordinal = 0))
    private double redirectPerformRangedAttackGetZ0(LivingEntity target) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? target.getZ() :
                target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection)).z;
    }
}
