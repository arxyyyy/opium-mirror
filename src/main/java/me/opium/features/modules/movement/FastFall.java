package me.opium.features.modules.movement;

import me.opium.features.modules.Module;
import me.opium.features.Feature;
import me.opium.util.traits.Util;

public class FastFall extends Module {
    public FastFall() {
        super("FastFall", "step but reversed..", Category.MOVEMENT, true, false, false);
    }

    @Override public void onUpdate() {
        if (Feature.nullCheck()) return;
        if (Util.mc.player.isInLava() || Util.mc.player.isTouchingWater() || !Util.mc.player.isOnGround()) return;
        Util.mc.player.addVelocity(0, -1, 0);
    }
}
