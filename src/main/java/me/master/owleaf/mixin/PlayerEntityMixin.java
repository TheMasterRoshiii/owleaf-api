package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 redirectTravelGetLookAngle(Player playerEntity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(playerEntity);
        return gravityDirection == Direction.DOWN ? playerEntity.getLookAngle() :
                RotationUtil.vecWorldToPlayer(playerEntity.getLookAngle(), gravityDirection);
    }

    @ModifyArgs(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;", ordinal = 0))
    private void modifyTravelContaining(Args args) {
        Vec3 rotate = new Vec3(0.0, 0.9, 0.0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, OwleafGravityAPI.getGravityDirection(this));
        args.set(0, (Double)args.get(0) - rotate.x);
        args.set(1, (Double)args.get(1) - rotate.y * 0.9);
        args.set(2, (Double)args.get(2) - rotate.z);
    }

    @Redirect(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;", ordinal = 0))
    private AABB redirectMaybeBackOffFromEdgeMove(AABB box, double x, double y, double z) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection((Player)(Object)this);
        return gravityDirection == Direction.DOWN ? box.move(x, y, z) :
                box.move(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @ModifyArgs(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;", ordinal = 1))
    private void modifyAiStepInflate(Args args) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection((Player)(Object)this);
        if (gravityDirection != Direction.DOWN) {
            Vec3 vec3d = RotationUtil.maskPlayerToWorld((Double)args.get(0), (Double)args.get(1), (Double)args.get(2), gravityDirection);
            args.set(0, vec3d.x);
            args.set(1, vec3d.y);
            args.set(2, vec3d.z);
        }
    }
}
