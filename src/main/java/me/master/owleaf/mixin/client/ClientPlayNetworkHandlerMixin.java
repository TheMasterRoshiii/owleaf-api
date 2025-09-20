package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
class ClientPlayNetworkHandlerMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Map<UUID, net.minecraft.client.multiplayer.PlayerInfo> playerInfoMap;

    @Inject(method = "handleSetEntityData", at = @At("HEAD"))
    private void redirectHandleSetEntityDataGetEyeY0(CallbackInfo ci) {
        if (this.minecraft.player != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.minecraft.player);
            if (gravityDirection != Direction.DOWN) {
            }
        }
    }

    @Inject(method = "handleSetEntityData", at = @At("HEAD"))
    private void redirectHandleSetEntityDataGetX0(CallbackInfo ci) {
        if (this.minecraft.player != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.minecraft.player);
            if (gravityDirection != Direction.DOWN) {
            }
        }
    }

    @Inject(method = "handleSetEntityData", at = @At("HEAD"))
    private void redirectHandleSetEntityDataGetZ0(CallbackInfo ci) {
        if (this.minecraft.player != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.minecraft.player);
            if (gravityDirection != Direction.DOWN) {

            }
        }
    }

    @Inject(method = "handleExplosion", at = @At("HEAD"))
    private void redirectHandleExplodeAdd0(CallbackInfo ci) {
        if (this.minecraft.player != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.minecraft.player);
            if (gravityDirection != Direction.DOWN) {
            }
        }
    }
}
