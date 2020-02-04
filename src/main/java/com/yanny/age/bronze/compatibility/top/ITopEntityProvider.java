package com.yanny.age.bronze.compatibility.top;

import mcjty.theoneprobe.api.IProbeInfoEntityProvider;

public interface ITopEntityProvider extends IProbeInfoEntityProvider {
    @Override
    default String getID() {
        return TopCompatibility.ID;
    }
}
