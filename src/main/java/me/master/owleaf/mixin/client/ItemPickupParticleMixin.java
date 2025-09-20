package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemPickupParticle.class)
public abstract class ItemPickupParticleMixin extends Particle {

    @Shadow @Final private Entity target;

    protected ItemPickupParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void owleaf_onTickHead(CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this.target);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Vec3 gravityForce = new Vec3(0.0, -0.04, 0.0);
        Vec3 rotatedGravity = RotationUtil.vecPlayerToWorld(gravityForce, gravityDirection);

        this.xd += rotatedGravity.x;
        this.yd += rotatedGravity.y;
        this.zd += rotatedGravity.z;
    }
}