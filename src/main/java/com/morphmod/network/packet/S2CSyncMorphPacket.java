package com.morphmod.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CSyncMorphPacket {

    private final UUID playerId;
    private final int morphId;

    public S2CSyncMorphPacket(UUID playerId, int morphId) {
        this.playerId = playerId;
        this.morphId = morphId;
    }

    public static void encode(S2CSyncMorphPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerId);
        buf.writeInt(packet.morphId);
    }

    public static S2CSyncMorphPacket decode(FriendlyByteBuf buf) {
        return new S2CSyncMorphPacket(buf.readUUID(), buf.readInt());
    }

    public static void handle(S2CSyncMorphPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                com.morphmod.client.ClientMorphManager.setMorph(packet.playerId, packet.morphId)));
        ctx.setPacketHandled(true);
    }
}
