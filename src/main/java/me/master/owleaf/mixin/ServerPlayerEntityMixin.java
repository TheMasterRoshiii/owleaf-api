package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.config.OwleafConfig;
import me.master.owleaf.util.GravityCapability;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
class ServerPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;

        if (OwleafConfig.voidDamageAboveWorld) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);
            if (gravityDirection == Direction.UP && player.getY() > player.level().getMaxBuildHeight()) {
                player.hurt(player.damageSources().generic(), 4.0F);
            }
        }
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void injectRestoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;

        if (!alive && OwleafConfig.resetGravityOnRespawn) {
            GravityCapability.getGravityComponent(player).ifPresent(gc -> {
                gc.clearGravity(new RotationParameters(0), true);
                gc.setDefaultGravityDirection(Direction.DOWN, new RotationParameters(0), true);
                gc.invertGravity(false, new RotationParameters(0), true);
            });
        }
    }
}
