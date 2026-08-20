package com.morphmod.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MorphDataProvider implements ICapabilitySerializable<Tag> {

    private final IMorphData data = new MorphData();
    private final LazyOptional<IMorphData> optional = LazyOptional.of(() -> data);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityHandler.MORPH_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        return IntTag.valueOf(data.getMorphId());
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof IntTag intTag) {
            data.setMorphId(intTag.getAsInt());
        }
    }
}
