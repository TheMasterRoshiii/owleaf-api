package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Witch.class)
public abstract class WitchEntityMixin {

    @ModifyVariable(method = "performRangedAttack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;", ordinal = 0), ordinal = 0)
    private Vec3 modifyPerformRangedAttackVec3d0(Vec3 value, LivingEntity target, float pullProgress) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? value :
                RotationUtil.vecWorldToPlayer(value, gravityDirection);
    }

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    private double redirectPerformRangedAttackSqrt0(double value, LivingEntity target, float pullProgress) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        return gravityDirection == Direction.DOWN ? Math.sqrt(value) :
                Math.sqrt(Math.sqrt(value));
    }
}
