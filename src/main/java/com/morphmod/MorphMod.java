package com.morphmod;

import com.morphmod.capability.CapabilityHandler;
import com.morphmod.client.ClientSetup;
import com.morphmod.network.NetworkHandler;
import com.morphmod.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MorphMod.MOD_ID)
public class MorphMod {

    public static final String MOD_ID = "morphmod";

    public MorphMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::onItemGroupBuild);
        modEventBus.addListener(CapabilityHandler::registerCapabilities);

        NetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        // Registro exclusivo de cliente (telas, render, keybind)
        net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> ClientSetup::init);
    }

    private void onItemGroupBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.VOICEBOX.get());
        }
    }
}
