package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UseOnContext.class)
public abstract class ItemUsageContextMixin {

    @Redirect(method = "getClickLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 redirectGetClickLocationAdd(Vec3 vec3d, double x, double y, double z, Player player) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);
        return gravityDirection == Direction.DOWN ? vec3d.add(x, y, z) :
                vec3d.add(RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection));
    }
}
