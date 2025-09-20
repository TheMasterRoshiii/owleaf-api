package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(ServerGamePacketListenerImpl.class)
class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "send", at = @At("HEAD"))
    private void onSend(net.minecraft.network.protocol.Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundExplodePacket explodePacket && this.player != null) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.player);
            if (gravityDirection != Direction.DOWN) {
                try {
                    Field knockbackXField = ClientboundExplodePacket.class.getDeclaredField("knockbackX");
                    Field knockbackYField = ClientboundExplodePacket.class.getDeclaredField("knockbackY");
                    Field knockbackZField = ClientboundExplodePacket.class.getDeclaredField("knockbackZ");

                    knockbackXField.setAccessible(true);
                    knockbackYField.setAccessible(true);
                    knockbackZField.setAccessible(true);

                    float x = knockbackXField.getFloat(explodePacket);
                    float y = knockbackYField.getFloat(explodePacket);
                    float z = knockbackZField.getFloat(explodePacket);

                    Vec3 originalMotion = new Vec3(x, y, z);
                    Vec3 rotatedMotion = RotationUtil.vecWorldToPlayer(originalMotion, gravityDirection);

                    knockbackXField.setFloat(explodePacket, (float) rotatedMotion.x);
                    knockbackYField.setFloat(explodePacket, (float) rotatedMotion.y);
                    knockbackZField.setFloat(explodePacket, (float) rotatedMotion.z);

                } catch (Exception e) {
                }
            }
        }
    }
}
