package com.yanny.age.bronze.compatibility.top;

import mcjty.theoneprobe.api.IProbeInfoProvider;

public interface ITopBlockProvider extends IProbeInfoProvider {
    @Override
    default String getID() {
        return TopCompatibility.ID;
    }
}
