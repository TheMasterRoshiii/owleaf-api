package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class RamImpactTaskMixin {

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyKnockbackX(double x) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Vec3 rotated = RotationUtil.vecWorldToPlayer(new Vec3(x, 0.0, 0.0), gravityDirection);
            return rotated.x;
        }
        return x;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double modifyKnockbackZ(double z) {
        LivingEntity entity = (LivingEntity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Vec3 rotated = RotationUtil.vecWorldToPlayer(new Vec3(0.0, 0.0, z), gravityDirection);
            return rotated.z;
        }
        return z;
    }
}
