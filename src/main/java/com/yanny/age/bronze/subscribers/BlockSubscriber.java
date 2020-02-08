package com.yanny.age.bronze.subscribers;

import com.yanny.age.bronze.blocks.BoostedFurnaceBlock;
import com.yanny.age.bronze.blocks.ChimneyBlock;
import com.yanny.ages.api.group.ModItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static com.yanny.age.bronze.Reference.MODID;

@SuppressWarnings("WeakerAccess")
@ObjectHolder(MODID)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockSubscriber {
    public static final Block copper_ore = null;
    public static final Block tin_ore = null;
    public static final Block copper_block = null;
    public static final Block tin_block = null;
    public static final Block bronze_block = null;
    public static final Block boosted_furnace = null;
    public static final Block chimney = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        Block.Properties properties = Block.Properties.create(Material.ROCK).harvestLevel(ItemTier.STONE.getHarvestLevel()).harvestTool(ToolType.PICKAXE).
                hardnessAndResistance(2, 2);

        registry.register(new Block(properties).setRegistryName(MODID, "copper_ore"));
        registry.register(new Block(properties).setRegistryName(MODID, "tin_ore"));
        registry.register(new Block(properties).setRegistryName(MODID, "copper_block"));
        registry.register(new Block(properties).setRegistryName(MODID, "tin_block"));
        registry.register(new Block(properties).setRegistryName(MODID, "bronze_block"));

        Block.Properties chimneyProperties = Block.Properties.create(Material.ROCK).harvestLevel(ItemTier.STONE.getHarvestLevel()).harvestTool(ToolType.PICKAXE).
                hardnessAndResistance(2, 2).variableOpacity();
        registry.register(new ChimneyBlock(chimneyProperties).setRegistryName(MODID, "chimney"));

        Block.Properties furnaceProperties = Block.Properties.create(Material.ROCK).harvestLevel(ItemTier.STONE.getHarvestLevel()).harvestTool(ToolType.PICKAXE).
                hardnessAndResistance(3, 3).lightValue(13);
        registry.register(new BoostedFurnaceBlock(furnaceProperties).setRegistryName(MODID, "boosted_furnace"));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new BlockItem(copper_ore, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "copper_ore"));
        registry.register(new BlockItem(tin_ore, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "tin_ore"));
        registry.register(new BlockItem(copper_block, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "copper_block"));
        registry.register(new BlockItem(tin_block, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "tin_block"));
        registry.register(new BlockItem(bronze_block, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "bronze_block"));
        registry.register(new BlockItem(boosted_furnace, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "boosted_furnace"));
        registry.register(new BlockItem(chimney, new Item.Properties().group(ModItemGroup.AGES)).setRegistryName(MODID, "chimney"));
    }
}
