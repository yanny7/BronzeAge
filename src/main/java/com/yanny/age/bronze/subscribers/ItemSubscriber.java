package com.yanny.age.bronze.subscribers;

import com.yanny.age.bronze.Reference;
import com.yanny.ages.api.group.ModItemGroup;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static com.yanny.age.bronze.Reference.MODID;

@SuppressWarnings({"unused", "WeakerAccess"})
@ObjectHolder(Reference.MODID)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemSubscriber {
    public static final Item copper_ingot = null;
    public static final Item tin_ingot = null;
    public static final Item bronze_ingot = null;
    public static final Item copper_nugget = null;
    public static final Item tin_nugget = null;
    public static final Item bronze_nugget = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "copper_ingot"));
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "tin_ingot"));
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "bronze_ingot"));
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "copper_nugget"));
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "tin_nugget"));
        registry.register(new Item(new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "bronze_nugget"));
    }
}
