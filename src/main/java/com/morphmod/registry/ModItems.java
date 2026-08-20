package com.morphmod.registry;

import com.morphmod.MorphMod;
import com.morphmod.item.VoiceboxItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MorphMod.MOD_ID);

    public static final RegistryObject<Item> VOICEBOX = ITEMS.register("voicebox",
            () -> new VoiceboxItem(new Item.Properties().stacksTo(1)));
}
