package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Shulker.class)
public abstract class ShulkerEntityMixin {

    @Redirect(method = "push", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private void redirectPushMove0(Entity entity, MoverType movementType, Vec3 vec3d) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            entity.move(movementType, vec3d);
        } else {
            entity.move(movementType, RotationUtil.vecWorldToPlayer(vec3d, gravityDirection));
        }
    }
}
