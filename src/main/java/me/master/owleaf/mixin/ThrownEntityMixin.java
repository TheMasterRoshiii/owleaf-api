package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
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
        modify = new Vec3(modify.x, modify.y - (double)this.getGravity(), modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, OwleafGravityAPI.getGravityDirection(entity));
        modify = new Vec3(modify.x, modify.y + (double)this.getGravity(), modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, OwleafGravityAPI.getGravityDirection(entity));
        return modify;
    }
}
