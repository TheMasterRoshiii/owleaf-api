package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrowableProjectile.class)
public abstract class ThrownEntityMixin {

    @Shadow protected abstract float getGravity();

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyTick(Vec3 modify) {
        ThrowableProjectile entity = (ThrowableProjectile)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Vec3 tempVec = new Vec3(modify.x, modify.y - this.getGravity(), modify.z);
            tempVec = RotationUtil.vecWorldToPlayer(tempVec, gravityDirection);
            tempVec = tempVec.add(0, this.getGravity(), 0);
            return RotationUtil.vecPlayerToWorld(tempVec, gravityDirection);
        }
        return modify;
    }
}