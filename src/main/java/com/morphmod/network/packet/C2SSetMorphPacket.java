package com.morphmod.network.packet;

import com.morphmod.capability.CapabilityHandler;
import com.morphmod.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class C2SSetMorphPacket {

    private final int morphId;

    public C2SSetMorphPacket(int morphId) {
        this.morphId = morphId;
    }

    public static void encode(C2SSetMorphPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.morphId);
    }

    public static C2SSetMorphPacket decode(FriendlyByteBuf buf) {
        return new C2SSetMorphPacket(buf.readInt());
    }

    public static void handle(C2SSetMorphPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            CapabilityHandler.setMorphId(player, packet.morphId);

            // Reenvia para todos que estão rastreando esse jogador (e para ele mesmo)
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new S2CSyncMorphPacket(player.getUUID(), packet.morphId));
        });
        ctx.setPacketHandled(true);
    }
}
