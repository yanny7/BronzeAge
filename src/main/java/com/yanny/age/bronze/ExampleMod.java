package com.yanny.age.bronze;

import com.yanny.age.bronze.config.ConfigHolder;
import com.yanny.age.bronze.proxy.ClientProxy;
import com.yanny.age.bronze.proxy.IProxy;
import com.yanny.age.bronze.proxy.ServerProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Reference.MODID)
public class ExampleMod {
    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public ExampleMod() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
    }
}
