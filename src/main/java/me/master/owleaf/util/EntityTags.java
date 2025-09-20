package me.master.owleaf.util;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import me.master.owleaf.OwleafApiMod;

public abstract class EntityTags {
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITY_RENDERING =
            net.minecraft.tags.TagKey.create(Registries.ENTITY_TYPE,  ResourceLocation.fromNamespaceAndPath(OwleafApiMod.MOD_ID, "forbidden_entity_rendering"));

    public static void init() {

    }

    public static boolean canChangeGravity(Entity entity) {
        if (entity == null) return false;
        return entity instanceof LivingEntity ||
                entity instanceof ItemEntity ||
                entity instanceof FallingBlockEntity ||
                entity instanceof AbstractArrow ||
                entity instanceof FishingHook ||
                entity instanceof Boat ||
                entity instanceof Minecart;
    }
}
