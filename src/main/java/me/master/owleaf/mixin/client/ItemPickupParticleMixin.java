package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemPickupParticle.class)
public abstract class ItemPickupParticleMixin {

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyTick(Vec3 modify, Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            modify = new Vec3(modify.x, modify.y - 0.04, modify.z);
            modify = RotationUtil.vecWorldToPlayer(modify, gravityDirection);
            modify = new Vec3(modify.x, modify.y + 0.04, modify.z);
            modify = RotationUtil.vecPlayerToWorld(modify, gravityDirection);
        }

        return modify;
    }
}
