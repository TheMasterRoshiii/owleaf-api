package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob; // <--- Importante
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LookAtPlayerGoal.class)
public abstract class LookAtEntityGoalMixin {


    @Shadow @Final protected Mob mob;

    @Redirect(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/control/LookControl;setLookAt(DDD)V"
            )
    )
    private void onSetLookAt(LookControl instance, double x, double y, double z) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.mob);

        if (gravityDirection == Direction.DOWN) {
            instance.setLookAt(x, y, z);
        } else {
            instance.setLookAt(this.mob.getEyePosition().x, this.mob.getEyePosition().y, this.mob.getEyePosition().z);
        }
    }
}