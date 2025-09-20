package me.master.owleaf.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "owleafapi", bus = Mod.EventBusSubscriber.Bus.MOD)
public class OwleafConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue KEEP_WORLD_LOOK = BUILDER
            .comment("Keep world look direction when gravity changes")
            .define("keepWorldLook", false);

    public static final ForgeConfigSpec.IntValue ROTATION_TIME = BUILDER
            .comment("Rotation animation time in milliseconds")
            .defineInRange("rotationTime", 500, 0, 5000);

    public static final ForgeConfigSpec.BooleanValue WORLD_VELOCITY = BUILDER
            .comment("Rotate velocity with gravity changes")
            .define("worldVelocity", false);

    public static final ForgeConfigSpec.BooleanValue RESET_GRAVITY_ON_DIMENSION_CHANGE = BUILDER
            .comment("Reset gravity when changing dimensions")
            .define("resetGravityOnDimensionChange", true);

    public static final ForgeConfigSpec.BooleanValue RESET_GRAVITY_ON_RESPAWN = BUILDER
            .comment("Reset gravity on player respawn")
            .define("resetGravityOnRespawn", true);

    public static final ForgeConfigSpec.BooleanValue VOID_DAMAGE_ABOVE_WORLD = BUILDER
            .comment("Apply void damage above world when gravity is inverted")
            .define("voidDamageAboveWorld", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean keepWorldLook;
    public static int rotationTime;
    public static boolean worldVelocity;
    public static boolean resetGravityOnDimensionChange;
    public static boolean resetGravityOnRespawn;
    public static boolean voidDamageAboveWorld;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        keepWorldLook = KEEP_WORLD_LOOK.get();
        rotationTime = ROTATION_TIME.get();
        worldVelocity = WORLD_VELOCITY.get();
        resetGravityOnDimensionChange = RESET_GRAVITY_ON_DIMENSION_CHANGE.get();
        resetGravityOnRespawn = RESET_GRAVITY_ON_RESPAWN.get();
        voidDamageAboveWorld = VOID_DAMAGE_ABOVE_WORLD.get();
    }
}
