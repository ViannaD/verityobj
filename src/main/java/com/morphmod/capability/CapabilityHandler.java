package com.morphmod.capability;

import com.morphmod.MorphMod;
import com.morphmod.network.NetworkHandler;
import com.morphmod.network.packet.S2CSyncMorphPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.network.PacketDistributor;

public class CapabilityHandler {

    public static Capability<IMorphData> MORPH_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation MORPH_CAP_ID = new ResourceLocation(MorphMod.MOD_ID, "morph_data");

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IMorphData.class);
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(MORPH_CAP_ID, new MorphDataProvider());
        }
    }

    // Mantém o morph entre morte/respawn (remova este listener se preferir resetar ao morrer)
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(MORPH_DATA).ifPresent(oldStore -> {
            event.getEntity().getCapability(MORPH_DATA).ifPresent(newStore -> {
                if (event.isWasDeath()) {
                    newStore.setMorphId(oldStore.getMorphId());
                }
            });
        });
    }

    // Sincroniza o morph do jogador para todos que estejam vendo ele quando ele entra no mundo/dimensão
    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Player player) {
            player.getCapability(MORPH_DATA).ifPresent(store -> {
                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new S2CSyncMorphPacket(player.getUUID(), store.getMorphId()));
            });
        }
    }

    public static int getMorphId(Player player) {
        LazyOptional<IMorphData> cap = player.getCapability(MORPH_DATA);
        return cap.map(IMorphData::getMorphId).orElse(-1);
    }

    public static void setMorphId(Player player, int id) {
        player.getCapability(MORPH_DATA).ifPresent(store -> store.setMorphId(id));
    }
}
