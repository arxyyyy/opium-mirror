package org.nrnr.opium.impl.event.render.entity;

import net.minecraft.entity.LivingEntity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class RenderEntityInvisibleEvent extends Event {
    private final LivingEntity entity;

    public RenderEntityInvisibleEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
