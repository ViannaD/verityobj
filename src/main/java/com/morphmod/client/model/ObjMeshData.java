package com.morphmod.client.model;

/**
 * Malha já triangulada e "achatada" (sem reuso de índices) pronta para desenhar.
 * Cada vértice ocupa 8 floats: x, y, z, u, v, nx, ny, nz.
 */
public class ObjMeshData {

    public final float[] vertexData;
    public final int vertexCount;

    public ObjMeshData(float[] vertexData) {
        this.vertexData = vertexData;
        this.vertexCount = vertexData.length / 8;
    }
}
