package org.nrnr.opium.impl.event.entity;

import net.minecraft.entity.LivingEntity;
import org.nrnr.opium.api.event.Event;

public class EntityDeathEvent extends Event {

    private final LivingEntity entity;

    public EntityDeathEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
