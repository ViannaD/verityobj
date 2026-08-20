package com.morphmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static final String CATEGORY = "key.categories.morphmod";

    public static final KeyMapping OPEN_MORPH_PANEL = new KeyMapping(
            "key.morphmod.open_panel",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_B,
            CATEGORY
    );
}
