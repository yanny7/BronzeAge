package com.yanny.age.bronze.config;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

class ServerConfig {

    ServerConfig(@Nonnull final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        builder.pop();
    }
}
