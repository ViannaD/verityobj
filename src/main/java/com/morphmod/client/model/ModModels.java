package com.morphmod.client.model;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ModModels {

    public static final ResourceLocation CHARACTER_MODEL =
            new ResourceLocation("morphmod", "models/entity/character.obj");

    private static ObjMeshData cachedMesh;
    private static boolean loadAttempted = false;

    /** Retorna a malha do personagem, carregando (e cacheando) na primeira chamada. */
    public static Optional<ObjMeshData> getCharacterMesh() {
        if (!loadAttempted) {
            loadAttempted = true;
            cachedMesh = ObjMeshLoader.load(CHARACTER_MODEL).orElse(null);
        }
        return Optional.ofNullable(cachedMesh);
    }
}
