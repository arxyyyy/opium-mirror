package org.nrnr.opium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class OpiumMod implements ClientModInitializer {
    public static final String MOD_NAME = "Opium";
    public static final String MOD_VER = "1.3.1";
    public static int finaluid = -1;

    @Override
    public void onInitializeClient() {
        Opium.init();
    }

    public static boolean isBaritonePresent() {
        return FabricLoader.getInstance().getModContainer("baritone").isPresent();
    }
}
