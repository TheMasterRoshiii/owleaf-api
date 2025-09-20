package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
class ProjectileEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Projectile entity = (Projectile)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Vec3 motion = entity.getDeltaMovement();


            motion = new Vec3(motion.x, motion.y - 0.03, motion.z);
            motion = RotationUtil.vecWorldToPlayer(motion, gravityDirection);
            motion = new Vec3(motion.x, motion.y + 0.03, motion.z);
            motion = RotationUtil.vecPlayerToWorld(motion, gravityDirection);

            entity.setDeltaMovement(motion);
        }
    }
}
