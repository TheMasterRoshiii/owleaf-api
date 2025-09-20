package me.master.owleaf.util;

import me.master.owleaf.OwleafApiMod;
import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.config.OwleafConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OwleafApiMod.MOD_ID)
public class GravityEventHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (OwleafConfig.resetGravityOnRespawn) {
            GravityCapability.getGravityComponent(event.getEntity()).ifPresent(gc -> {
                gc.clearGravity(new RotationParameters(0), true);
                gc.setDefaultGravityDirection(net.minecraft.core.Direction.DOWN, new RotationParameters(0), true);
                gc.invertGravity(false, new RotationParameters(0), true);
            });
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(EntityTravelToDimensionEvent event) {
        if (OwleafConfig.resetGravityOnDimensionChange && event.getEntity() instanceof Player) {
            GravityCapability.getGravityComponent(event.getEntity()).ifPresent(gc -> {
                gc.clearGravity(new RotationParameters(0), true);
                gc.setDefaultGravityDirection(net.minecraft.core.Direction.DOWN, new RotationParameters(0), true);
                gc.invertGravity(false, new RotationParameters(0), true);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (EntityTags.canChangeGravity(entity)) {
            GravityCapability.getGravityComponent(entity).ifPresent(gc -> {
                if (OwleafConfig.resetGravityOnRespawn) {
                    gc.clearGravity(new RotationParameters(0), true);
                    gc.setDefaultGravityDirection(net.minecraft.core.Direction.DOWN, new RotationParameters(0), true);
                    gc.invertGravity(false, new RotationParameters(0), true);
                }
            });
        }
    }
}
