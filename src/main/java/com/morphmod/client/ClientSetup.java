package com.morphmod.client;

import com.morphmod.client.gui.MorphScreen;
import com.morphmod.client.render.MorphRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::registerKeyMappings);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new MorphRenderHandler());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        // Nada adicional necessário por enquanto
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.OPEN_MORPH_PANEL);
    }

    public static class ClientTickHandler {
        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            while (KeyBindings.OPEN_MORPH_PANEL.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(new MorphScreen());
                }
            }
        }
    }
}
