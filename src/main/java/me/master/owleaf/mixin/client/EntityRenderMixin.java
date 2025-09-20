package me.master.owleaf.mixin.client;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {

    @ModifyVariable(method = "renderNameTag", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyRenderNameTag(Vec3 modify, Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            modify = RotationUtil.vecWorldToPlayer(modify, gravityDirection);
        }

        return modify;
    }
}
