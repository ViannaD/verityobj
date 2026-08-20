package com.morphmod.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientMorphManager {

    private static final Map<UUID, Integer> MORPHS = new HashMap<>();

    public static void setMorph(UUID playerId, int morphId) {
        if (morphId < 0) {
            MORPHS.remove(playerId);
        } else {
            MORPHS.put(playerId, morphId);
        }
    }

    public static int getMorph(UUID playerId) {
        return MORPHS.getOrDefault(playerId, -1);
    }

    public static boolean isMorphed(UUID playerId) {
        return MORPHS.containsKey(playerId);
    }
}
