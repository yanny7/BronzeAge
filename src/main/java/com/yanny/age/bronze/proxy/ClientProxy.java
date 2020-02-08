package com.yanny.age.bronze.proxy;

import com.yanny.age.bronze.blocks.BoostedFurnaceGui;
import com.yanny.age.bronze.subscribers.ContainerSubscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        ScreenManager.registerFactory(ContainerSubscriber.boosted_furnace, BoostedFurnaceGui::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
