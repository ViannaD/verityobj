package com.morphmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

/**
 * Item amarelo "Voicebox". Ao ser usado (clique direito), abre uma tela
 * apenas com um campo de texto e os botões "Speak" / "Cancel".
 * Não faz absolutamente nada além de aparecer e poder ser fechado —
 * é puramente cosmético/decorativo, como pedido.
 */
public class VoiceboxItem extends Item {

    public VoiceboxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        if (level.isClientSide) {
            openScreen();
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreenClient() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.morphmod.client.gui.VoiceboxScreen());
    }

    private void openScreen() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::openScreenClient);
    }
}
