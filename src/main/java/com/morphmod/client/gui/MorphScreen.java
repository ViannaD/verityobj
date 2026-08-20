package com.morphmod.client.gui;

import com.morphmod.MorphCharacters;
import com.morphmod.network.NetworkHandler;
import com.morphmod.network.packet.C2SSetMorphPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MorphScreen extends Screen {

    public MorphScreen() {
        super(Component.literal("Painel de Morph"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 160;
        int buttonHeight = 20;
        int spacing = 6;
        int startY = this.height / 2 - 70;
        int x = this.width / 2 - buttonWidth / 2;

        int y = startY;

        for (MorphCharacters character : MorphCharacters.values()) {
            if (character == MorphCharacters.NORMAL) continue;

            addRenderableWidget(Button.builder(
                    Component.literal("Virar: " + character.getDisplayName()),
                    button -> selectMorph(character)
            ).bounds(x, y, buttonWidth, buttonHeight).build());

            y += buttonHeight + spacing;
        }

        y += spacing;

        addRenderableWidget(Button.builder(
                Component.literal("Voltar ao Normal"),
                button -> selectMorph(MorphCharacters.NORMAL)
        ).bounds(x, y, buttonWidth, buttonHeight).build());

        y += buttonHeight + spacing * 2;

        addRenderableWidget(Button.builder(
                Component.literal("Fechar"),
                button -> onClose()
        ).bounds(x, y, buttonWidth, buttonHeight).build());
    }

    private void selectMorph(MorphCharacters character) {
        NetworkHandler.CHANNEL.sendToServer(new C2SSetMorphPacket(character.getId()));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
