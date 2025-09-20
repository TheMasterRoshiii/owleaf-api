package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeEffectSoundPlayerMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double redirectTickGetEyeY0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(clientPlayerEntity);
        return gravityDirection == Direction.DOWN ? clientPlayerEntity.getY() :
                clientPlayerEntity.getEyePosition().y;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D", ordinal = 0))
    private double redirectTickGetX0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(clientPlayerEntity);
        return gravityDirection == Direction.DOWN ? clientPlayerEntity.getX() :
                clientPlayerEntity.getEyePosition().x;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D", ordinal = 0))
    private double redirectTickGetZ0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(clientPlayerEntity);
        return gravityDirection == Direction.DOWN ? clientPlayerEntity.getZ() :
                clientPlayerEntity.getEyePosition().z;
    }
}
