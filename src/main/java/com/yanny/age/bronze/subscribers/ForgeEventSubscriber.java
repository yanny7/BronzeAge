package com.yanny.age.bronze.subscribers;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.yanny.age.bronze.Reference.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void rightClickInfo(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) {
            return;
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Chunk chunk = event.getWorld().getChunkAt(event.getPos());
        Map<Block, MutableInt> stats = new HashMap<>();

        for (int y = 0; y < 255; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    pos.setPos(x, y, z);
                    Block block = chunk.getBlockState(pos).getBlock();

                    if (stats.get(block) == null) {
                        stats.put(block, new MutableInt(0));
                    }

                    stats.get(block).increment();
                }
            }
        }

        System.out.println("-----------------------------------------------------------------");
        //stats.forEach((block, mutableInt) -> System.out.println(block + ": " + mutableInt));
        stats.entrySet().stream().filter(value -> Objects.requireNonNull(value.getKey().getRegistryName()).getNamespace().equals(MODID)).sorted(Map.Entry.comparingByValue())
                .forEach(map -> System.out.println(map.getKey() + ": " + map.getValue()));
        System.out.println("-----------------------------------------------------------------");
    }
}
