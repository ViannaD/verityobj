package com.morphmod.capability;

public class MorphData implements IMorphData {

    private int morphId = -1; // -1 = normal (sem morph)

    @Override
    public int getMorphId() {
        return morphId;
    }

    @Override
    public void setMorphId(int id) {
        this.morphId = id;
    }
}
