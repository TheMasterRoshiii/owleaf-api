package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    @Shadow protected boolean inGround;
    private boolean initialPosAdjusted = false;

    public PersistentProjectileEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyTick(Vec3 modify) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this);
        if (gravityDirection != Direction.DOWN) {
            Vec3 tempVec = new Vec3(modify.x, modify.y - 0.05, modify.z);
            tempVec = RotationUtil.vecWorldToPlayer(tempVec, gravityDirection);
            tempVec = tempVec.add(0, 0.05, 0);
            return RotationUtil.vecPlayerToWorld(tempVec, gravityDirection);
        }
        return modify;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!initialPosAdjusted && !inGround) {
            Direction gravityDirection = OwleafGravityAPI.getGravityDirection(this);
            if (gravityDirection != Direction.DOWN) {
                Vec3 adjustment = RotationUtil.vecPlayerToWorld(new Vec3(0.0, 0.1, 0.0), gravityDirection);
                this.setPos(this.getX() + adjustment.x, this.getY() + adjustment.y, this.getZ() + adjustment.z);
                initialPosAdjusted = true;
            }
        }
    }
}