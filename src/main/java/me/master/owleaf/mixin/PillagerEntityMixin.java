package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Pillager.class)
public abstract class PillagerEntityMixin implements RangedAttackMob {

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Pillager;performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;FF)V", ordinal = 0))
    private void redirectPerformRangedAttackShoot0(Pillager pillagerEntity, LivingEntity entity, LivingEntity target, Projectile projectile, float multishotSpray, float speed) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            this.performRangedAttack(entity, target, projectile, multishotSpray, speed);
        } else {
            Vec3 targetPos = target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection));
            double d = targetPos.x - entity.getX();
            double e = targetPos.z - entity.getZ();
            double f = Math.sqrt(Math.sqrt(d * d + e * e));
            double g = targetPos.y - projectile.getY() + f * 0.2F;

            Vector3f vec3f = this.getProjectileShotVector(entity, new Vec3(d, g, e), multishotSpray);
            projectile.shoot((double)vec3f.x, (double)vec3f.y, (double)vec3f.z, speed, (float)(14 - entity.level().getDifficulty().getId() * 4));
            entity.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    private Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 direction, float multishotSpray) {
        return new Vector3f((float)direction.x, (float)direction.y, (float)direction.z);
    }
}
