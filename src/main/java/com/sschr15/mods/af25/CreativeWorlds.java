package com.sschr15.mods.af25;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

public class CreativeWorlds implements ModInitializer {
    @Override
    public void onInitialize() {
        SharedConstants.IS_RUNNING_IN_IDE = FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
