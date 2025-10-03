package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.List;

@Mixin(PistonMovingBlockEntity.class)
class PistonBlockEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private static void onTick(CallbackInfo ci) {
        try {
            PistonMovingBlockEntity piston = (PistonMovingBlockEntity)(Object)ci;


            Method[] methods = PistonMovingBlockEntity.class.getDeclaredMethods();
            for (Method method : methods) {

                if (method.getName().contains("move") && method.getParameterCount() >= 2) {
                    method.setAccessible(true);

                    break;
                }
            }


            if (piston.getLevel() != null) {
                List<Entity> entities = piston.getLevel().getEntitiesOfClass(Entity.class,
                        piston.getBlockState().getShape(piston.getLevel(), piston.getBlockPos()).bounds().move(piston.getBlockPos()));

                for (Entity entity : entities) {
                    Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
                    if (gravityDirection != Direction.DOWN) {
                        Vec3 motion = entity.getDeltaMovement();
                        Vec3 rotatedMotion = RotationUtil.vecWorldToPlayer(motion, gravityDirection);
                        entity.setDeltaMovement(rotatedMotion);
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}