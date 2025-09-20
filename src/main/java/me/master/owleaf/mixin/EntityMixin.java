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
public abstract class EntityMixin extends Entity {

    public EntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Redirect(
            method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirect_travel_applyGravity(Vec3 velocity, double x, double y, double z) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return velocity.add(x, y, z);
        }

        Vec3 gravity = new Vec3(0.0, y, 0.0);
        Vec3 rotatedGravity = RotationUtil.vecPlayerToWorld(gravity, gravityDirection);
        return velocity.add(rotatedGravity);
    }

    @ModifyVariable(method = "knockback(DDD)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double modifyKnockbackX(double x) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        return RotationUtil.vecWorldToPlayer(x, 0.0, 0.0, gravityDirection).x;
    }

    @ModifyVariable(method = "knockback(DDD)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private double modifyKnockbackZ(double z) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        return RotationUtil.vecWorldToPlayer(0.0, 0.0, z, gravityDirection).z;
    }
}