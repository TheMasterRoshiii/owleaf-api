package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY()D", ordinal = 0))
    private double redirectTravelGetY0(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getY() :
                RotationUtil.vecWorldToPlayer(livingEntity.position(), gravityDirection).y;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY()D", ordinal = 1))
    private double redirectTravelGetY1(LivingEntity livingEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(livingEntity);
        return gravityDirection == Direction.DOWN ? livingEntity.getY() :
                RotationUtil.vecWorldToPlayer(livingEntity.position(), gravityDirection).y;
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Vec3 modifyTravel(Vec3 modify) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            modify = new Vec3(modify.x, modify.y - 0.08, modify.z);
            modify = RotationUtil.vecWorldToPlayer(modify, gravityDirection);
            modify = new Vec3(modify.x, modify.y + 0.08, modify.z);
            modify = RotationUtil.vecPlayerToWorld(modify, gravityDirection);
        }

        return modify;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyKnockbackX(double x) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        return RotationUtil.vecWorldToPlayer(x, 0.0, 0.0, gravityDirection).x;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double modifyKnockbackZ(double z) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        return RotationUtil.vecWorldToPlayer(0.0, 0.0, z, gravityDirection).z;
    }
}
