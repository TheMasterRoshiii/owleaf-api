package me.master.owleaf;

import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.config.OwleafConfig;
import me.master.owleaf.util.GravityCapability;
import me.master.owleaf.util.GravityChannel;
import me.master.owleaf.util.GravityEventHandler;
import me.master.owleaf.util.EntityTags;
import me.master.owleaf.command.GravityCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OwleafApiMod.MOD_ID)
public class OwleafApiMod {
    public static final String MOD_ID = "owleafapi";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static OwleafConfig config;

    public OwleafApiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OwleafConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GravityCapability.class);
        MinecraftForge.EVENT_BUS.register(GravityEventHandler.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GravityChannel.init();
            GravityCapability.register();
            EntityTags.init();
            OwleafGravityAPI.init();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GravityChannel.initClient();
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Owleaf API initialized successfully");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GravityCommand.register(event.getDispatcher());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
