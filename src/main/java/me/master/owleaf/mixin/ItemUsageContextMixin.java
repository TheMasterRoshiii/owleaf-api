package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UseOnContext.class)
public abstract class ItemUsageContextMixin {

    @Shadow public abstract Player getPlayer();

    @Inject(method = "getClickLocation()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void owleaf_onGetClickLocation(CallbackInfoReturnable<Vec3> cir) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }

        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Vec3 originalLocation = cir.getReturnValue();
        Vec3 playerSpaceLocation = RotationUtil.vecWorldToPlayer(originalLocation, gravityDirection);
        cir.setReturnValue(playerSpaceLocation);
    }
}