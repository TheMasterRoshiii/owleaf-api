package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
class PiglinEntityMixin {

    @Inject(method = "performRangedAttack", at = @At("HEAD"), cancellable = true)
    private void onPerformRangedAttack(LivingEntity target, float distanceFactor, CallbackInfo ci) {
        Piglin piglin = (Piglin)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);

        if (gravityDirection != Direction.DOWN) {
            ci.cancel();


            Vec3 targetPos = target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection));
            double d = targetPos.x - piglin.getX();
            double e = targetPos.z - piglin.getZ();
            double f = Math.sqrt(Math.sqrt(d * d + e * e));
            double g = targetPos.y - piglin.getY() + f * 0.2F;

            Vector3f vec3f = this.getProjectileShotVector(piglin, new Vec3(d, g, e), 1.0f);


            piglin.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (piglin.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    private Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 direction, float multishotSpray) {
        return new Vector3f((float)direction.x, (float)direction.y, (float)direction.z);
    }
}
