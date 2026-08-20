package com.morphmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ObjMeshRenderer {

    /**
     * Desenha a malha inteira usando a pose atual do PoseStack.
     * Cada vértice do array segue o formato: x, y, z, u, v, nx, ny, nz.
     */
    public static void render(ObjMeshData mesh, PoseStack poseStack, VertexConsumer consumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float[] data = mesh.vertexData;
        for (int i = 0; i < data.length; i += 8) {
            float x = data[i];
            float y = data[i + 1];
            float z = data[i + 2];
            float u = data[i + 3];
            float v = data[i + 4];
            float nx = data[i + 5];
            float ny = data[i + 6];
            float nz = data[i + 7];

            consumer.vertex(matrix, x, y, z)
                    .color(red, green, blue, alpha)
                    .uv(u, v)
                    .overlayCoords(packedOverlay)
                    .uv2(packedLight)
                    .normal(normalMatrix, nx, ny, nz)
                    .endVertex();
        }
    }
}
