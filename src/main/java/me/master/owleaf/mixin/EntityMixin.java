package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public Level level;
    @Shadow public abstract Vec3 position();
    @Shadow public abstract Vec3 getViewVector(float partialTicks);

    @ModifyVariable(method = "baseTick", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyBaseTick(Vec3 modify) {
        Entity entity = (Entity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN) {
            return modify;
        }

        modify = new Vec3(modify.x, modify.y - 0.04, modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, gravityDirection);
        modify = new Vec3(modify.x, modify.y + 0.04, modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, gravityDirection);
        return modify;
    }

    @ModifyVariable(method = "move", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyMove(Vec3 modify) {
        Entity entity = (Entity)(Object)this;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Vec3 rotate = new Vec3(0.0, 0.98, 0.0);
            rotate = RotationUtil.vecPlayerToWorld(rotate, gravityDirection);
            modify = new Vec3(modify.x - rotate.x, modify.y - rotate.y * 0.98, modify.z - rotate.z);
        }

        return modify;
    }

    @Redirect(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getY()D", ordinal = 0))
    private double redirectCheckFallDamageGetY(Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        return gravityDirection == Direction.DOWN ? entity.getY() :
                RotationUtil.vecWorldToPlayer(entity.position(), gravityDirection).y;
    }
}
