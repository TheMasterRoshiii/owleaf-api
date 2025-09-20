package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LookControl.class)
class LookControlMixin {

    @Inject(method = "getWantedY", at = @At(value = "HEAD"), cancellable = true)
    private void onGetWantedY(Entity entity, CallbackInfoReturnable<Double> cir) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        if (gravityDirection != Direction.DOWN) {
            cir.setReturnValue(entity.getEyePosition().y);
        }
    }

    @Redirect(method = "setLookAt(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getX()D", ordinal = 0))
    private double redirectSetLookAtGetX0(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getX() :
                entity.getEyePosition().x;
    }

    @Redirect(method = "setLookAt(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getZ()D", ordinal = 0))
    private double redirectSetLookAtGetZ0(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getZ() :
                entity.getEyePosition().z;
    }
}
