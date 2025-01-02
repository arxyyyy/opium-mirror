package dev.opium.api.events.impl;

import dev.opium.api.events.Event;
import net.minecraft.entity.projectile.FireworkRocketEntity;

public class RemoveFireworkEvent extends Event {
    private final FireworkRocketEntity rocketEntity;

    public RemoveFireworkEvent(FireworkRocketEntity rocketEntity) {
        super(Stage.Pre);
        this.rocketEntity = rocketEntity;
    }

    public FireworkRocketEntity getRocketEntity() {
        return rocketEntity;
    }
}
