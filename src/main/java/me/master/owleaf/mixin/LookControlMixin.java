package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LookControl.class)
public abstract class LookControlMixin {

    @Shadow @Final protected Mob mob;
    @Shadow protected double wantedX;
    @Shadow protected double wantedY;
    @Shadow protected double wantedZ;

    @Inject(method = "getWantedX", at = @At("HEAD"), cancellable = true)
    private void onGetWantedX(CallbackInfoReturnable<Double> cir) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(mob);
        if (gravityDirection != Direction.DOWN) {
            Vec3 target = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
            Vec3 relative = RotationUtil.vecWorldToPlayer(
                    target.x - mob.getX(),
                    target.y - mob.getEyeY(),
                    target.z - mob.getZ(),
                    gravityDirection
            );
            double yaw = Math.atan2(relative.z, relative.x) * (180.0 / Math.PI) - 90.0;
            cir.setReturnValue(yaw);
        }
    }

    @Inject(method = "getWantedY", at = @At("HEAD"), cancellable = true)
    private void onGetWantedY(CallbackInfoReturnable<Double> cir) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(mob);
        if (gravityDirection != Direction.DOWN) {
            Vec3 target = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
            Vec3 relative = RotationUtil.vecWorldToPlayer(
                    target.x - mob.getX(),
                    target.y - mob.getEyeY(),
                    target.z - mob.getZ(),
                    gravityDirection
            );
            double pitch = -Math.atan2(relative.y, Math.sqrt(relative.x * relative.x + relative.z * relative.z)) * (180.0 / Math.PI);
            cir.setReturnValue(pitch);
        }
    }
}