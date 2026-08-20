package com.morphmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Tela puramente cosmética/decorativa. Possui um campo de texto e dois botões
 * (Speak / Cancel), mas nenhum dos dois envia, executa ou salva nada — apenas
 * fecham a tela, como pedido.
 */
public class VoiceboxScreen extends Screen {

    private EditBox textBox;

    public VoiceboxScreen() {
        super(Component.literal("Voicebox"));
    }

    @Override
    protected void init() {
        super.init();

        int boxWidth = 300;
        int boxHeight = 20;
        int centerX = this.width / 2;
        int textY = this.height / 2 - 10;

        textBox = new EditBox(this.font, centerX - boxWidth / 2, textY, boxWidth, boxHeight, Component.literal("texto"));
        textBox.setMaxLength(256);
        textBox.setValue("");
        addRenderableWidget(textBox);
        setInitialFocus(textBox);

        int buttonWidth = 140;
        int buttonY = textY + boxHeight + 10;

        addRenderableWidget(Button.builder(Component.literal("Speak"), button -> onClose())
                .bounds(centerX - buttonWidth - 5, buttonY, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> onClose())
                .bounds(centerX + 5, buttonY, buttonWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
