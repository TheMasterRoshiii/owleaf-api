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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shulker.class)
public abstract class ShulkerEntityMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void owleaf_onPush(Entity pushedEntity, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(pushedEntity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        ci.cancel();

        Shulker self = (Shulker)(Object)this;
        Vec3 movement = pushedEntity.position().subtract(self.position()).normalize();
        Vec3 rotatedMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);

        pushedEntity.move(MoverType.SHULKER_BOX, rotatedMovement);
    }
}