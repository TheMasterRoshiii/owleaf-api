package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public abstract class PillagerEntityMixin {

    @Inject(
            method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void owleaf_performRangedAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        ci.cancel();

        Pillager self = (Pillager)(Object)this;
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(self, item -> item instanceof CrossbowItem);

        if (self.isHolding(stack -> stack.getItem() instanceof CrossbowItem)) {
            Vec3 targetPos = target.getEyePosition().add(RotationUtil.vecPlayerToWorld(0.0, target.getBbHeight() * 0.3333333333333333, 0.0, gravityDirection));

            Projectile projectile = ProjectileUtil.getMobArrow(self, new ItemStack(Items.ARROW), 1.0f);

            double dX = targetPos.x - self.getX();
            double dY = targetPos.y - projectile.getY();
            double dZ = targetPos.z - self.getZ();

            projectile.shoot(dX, dY, dZ, 1.6F, (float)(14 - self.level().getDifficulty().getId() * 4));

            self.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (self.getRandom().nextFloat() * 0.4F + 0.8F));
            self.level().addFreshEntity(projectile);
        }
    }
}