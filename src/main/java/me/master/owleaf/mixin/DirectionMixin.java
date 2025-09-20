package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Arrays;
import java.util.Comparator;

@Mixin(BlockPlaceContext.class)
public abstract class DirectionMixin extends UseOnContext {


    private DirectionMixin() {
        super(null, null, null);
        throw new AssertionError();
    }

    @Inject(method = "getNearestLookingDirections()[Lnet/minecraft/core/Direction;", at = @At("HEAD"), cancellable = true)
    private void owleaf_getNearestLookingDirections(CallbackInfoReturnable<Direction[]> cir) {
        Player player = ((BlockPlaceContext)(Object)this).getPlayer();
        if (player == null) {
            return;
        }

        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        float yaw = player.getViewYRot(1.0F);
        float pitch = player.getViewXRot(1.0F);

        Vec2 rotated = RotationUtil.rotPlayerToWorld(yaw, pitch, gravityDirection);
        final Vec3 viewVector = RotationUtil.rotToVec(rotated.x, rotated.y);

        Direction[] directions = Direction.values();
        Arrays.sort(directions, Comparator.comparingDouble(direction -> -viewVector.dot(Vec3.atLowerCornerOf(direction.getNormal()))));

        cir.setReturnValue(directions);
    }
}