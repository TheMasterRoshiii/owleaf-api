package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
class PointedDripstoneBlockMixin {

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void onFallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);

        if (gravityDirection != Direction.DOWN) {
            Direction tip = state.getValue(PointedDripstoneBlock.TIP_DIRECTION);
            if (tip == gravityDirection.getOpposite()) {
                entity.causeFallDamage(fallDistance + 2.0F, 2.0F, entity.damageSources().stalagmite());
            }
            ci.cancel();
        }
    }
}
