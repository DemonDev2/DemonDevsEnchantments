package net.demondev.demondevenchantments;

import com.mojang.logging.LogUtils;
import net.demondev.demondevenchantments.block.ModBlocks;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.demondev.demondevenchantments.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DemonDevEnchantments.MOD_ID)
public class DemonDevEnchantments {
    public static final String MOD_ID = "demondev_enchantments";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DemonDevEnchantments() {
        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register blocks, items, enchantments, and loot modifiers
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEnchantments.register(modEventBus);

        // Add common setup listener
        modEventBus.addListener(this::commonSetup);

        // Register event listeners
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup tasks can go here
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Logic for adding custom items to creative tabs
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Code to execute when the server is starting
    }

    // Register client-side events (e.g., rendering setup)
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client setup tasks (e.g., rendering setups)
        }
    }
}
