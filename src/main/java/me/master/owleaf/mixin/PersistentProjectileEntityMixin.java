package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    public PersistentProjectileEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
    public Vec3 modifyTick(Vec3 modify) {
        modify = new Vec3(modify.x, modify.y - 0.05, modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, OwleafGravityAPI.getGravityDirection(this));
        modify = new Vec3(modify.x, modify.y + 0.05, modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, OwleafGravityAPI.getGravityDirection(this));
        return modify;
    }

    @ModifyArgs(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;)V", ordinal = 0))
    private static void modifyInit(Args args, EntityType<? extends AbstractArrow> type, LivingEntity owner, Level world) {
        net.minecraft.core.Direction gravityDirection = OwleafGravityAPI.getGravityDirection(owner);
        if (gravityDirection != net.minecraft.core.Direction.DOWN) {
            Vec3 pos = owner.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, 0.1, 0.0, gravityDirection));
            args.set(1, pos.x);
            args.set(2, pos.y);
            args.set(3, pos.z);
        }
    }
}
