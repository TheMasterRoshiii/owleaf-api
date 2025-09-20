package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Direction.class)
public abstract class DirectionMixin {

    @Redirect(method = "getEntityFacingOrder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F", ordinal = 0))
    private static float redirectGetEntityFacingOrderGetYRot(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getYRot() :
                RotationUtil.rotPlayerToWorld(entity.getYRot(), entity.getXRot(), gravityDirection).x;
    }

    @Redirect(method = "getEntityFacingOrder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getXRot()F", ordinal = 0))
    private static float redirectGetEntityFacingOrderGetXRot(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getXRot() :
                RotationUtil.rotPlayerToWorld(entity.getYRot(), entity.getXRot(), gravityDirection).y;
    }
}
