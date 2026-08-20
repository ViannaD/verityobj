package com.morphmod.network;

import com.morphmod.MorphMod;
import com.morphmod.network.packet.C2SSetMorphPacket;
import com.morphmod.network.packet.S2CSyncMorphPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MorphMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    private static int nextId() {
        return id++;
    }

    public static void register() {
        CHANNEL.registerMessage(nextId(), C2SSetMorphPacket.class,
                C2SSetMorphPacket::encode, C2SSetMorphPacket::decode, C2SSetMorphPacket::handle);

        CHANNEL.registerMessage(nextId(), S2CSyncMorphPacket.class,
                S2CSyncMorphPacket::encode, S2CSyncMorphPacket::decode, S2CSyncMorphPacket::handle);
    }
}
