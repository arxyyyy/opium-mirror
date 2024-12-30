package org.nrnr.opium.impl.event;

import net.minecraft.entity.Entity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class EntityOutlineEvent extends Event {
    private final Entity entity;

    public EntityOutlineEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
