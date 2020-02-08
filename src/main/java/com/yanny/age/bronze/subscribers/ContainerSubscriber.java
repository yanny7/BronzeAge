package com.yanny.age.bronze.subscribers;

import com.yanny.age.bronze.blocks.BoostedFurnaceContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static com.yanny.age.bronze.Reference.MODID;

@ObjectHolder(MODID)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerSubscriber {
    public static final ContainerType<BoostedFurnaceContainer> boosted_furnace = IForgeContainerType.create(BoostedFurnaceContainer::new);

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(boosted_furnace.setRegistryName(MODID, "boosted_furnace"));
    }
}