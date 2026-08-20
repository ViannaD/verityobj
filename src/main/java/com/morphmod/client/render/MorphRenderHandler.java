package com.morphmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.morphmod.MorphCharacters;
import com.morphmod.client.ClientMorphManager;
import com.morphmod.client.model.ModModels;
import com.morphmod.client.model.ObjMeshData;
import com.morphmod.client.model.ObjMeshRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

/**
 * Sempre que um jogador está "morphado", esconde o modelo do jogador (cancela o evento)
 * e desenha, na mesma posição/rotação, o modelo customizado (character.obj) com a
 * textura do personagem escolhido.
 *
 * Se o personagem aparecer virado para o lado errado no jogo, ajuste EXTRA_Y_ROTATION
 * abaixo (some 90/180/270 até ficar certo) — a orientação depende de como o modelo
 * foi originalmente modelado no Blockbench.
 */
public class MorphRenderHandler {

    private static final float EXTRA_Y_ROTATION = 180.0f;

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        int morphId = ClientMorphManager.getMorph(player.getUUID());
        if (morphId < 0) return;

        MorphCharacters character = MorphCharacters.byId(morphId);
        ResourceLocation texture = character.getTexture();
        if (texture == null) return;

        Optional<ObjMeshData> meshOpt = ModModels.getCharacterMesh();
        if (meshOpt.isEmpty()) return; // modelo não carregou; deixa renderizar o jogador normal

        ObjMeshData mesh = meshOpt.get();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        float partialTick = event.getPartialTick();

        float bodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(EXTRA_Y_ROTATION - bodyRot));

        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        ObjMeshRenderer.render(mesh, poseStack, consumer,
                event.getPackedLight(), OverlayTexture.NO_OVERLAY,
                1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();

        event.setCanceled(true);
    }
}
