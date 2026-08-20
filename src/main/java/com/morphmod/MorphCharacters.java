package com.morphmod;

import net.minecraft.resources.ResourceLocation;

/**
 * Os 3 personagens que o jogador pode "morphar". Todos usam o MESMO modelo
 * (character.obj), apenas com uma textura diferente cada.
 * NORMAL representa "voltar ao normal" (jogador comum, sem morph).
 */
public enum MorphCharacters {

    NORMAL(-1, "Normal", null),
    CHARACTER_1(0, "Vermelho", new ResourceLocation("morphmod", "textures/entity/character_1.png")),
    CHARACTER_2(1, "Rosa", new ResourceLocation("morphmod", "textures/entity/character_2.png")),
    CHARACTER_3(2, "Amarelo", new ResourceLocation("morphmod", "textures/entity/character_3.png"));

    private final int id;
    private final String displayName;
    private final ResourceLocation texture;

    MorphCharacters(int id, String displayName, ResourceLocation texture) {
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static MorphCharacters byId(int id) {
        for (MorphCharacters c : values()) {
            if (c.id == id) return c;
        }
        return NORMAL;
    }
}
