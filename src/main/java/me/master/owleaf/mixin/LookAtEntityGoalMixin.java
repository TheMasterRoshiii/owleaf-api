package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LookAtPlayerGoal.class)
class LookAtEntityGoalMixin {

    @Shadow
    protected LivingEntity mob;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/control/LookControl;setLookAt(DDD)V"), index = 0)
    private double modifyTickX(double x) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.mob);
        return gravityDirection == Direction.DOWN ? x : this.mob.getEyePosition().x;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/control/LookControl;setLookAt(DDD)V"), index = 1)
    private double modifyTickEyeY(double eyeY) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.mob);
        return gravityDirection == Direction.DOWN ? eyeY : this.mob.getEyePosition().y;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/control/LookControl;setLookAt(DDD)V"), index = 2)
    private double modifyTickZ(double z) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.mob);
        return gravityDirection == Direction.DOWN ? z : this.mob.getEyePosition().z;
    }
}
