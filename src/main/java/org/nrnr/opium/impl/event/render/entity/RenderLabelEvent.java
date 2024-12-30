package org.nrnr.opium.impl.event.render.entity;

import net.minecraft.entity.Entity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class RenderLabelEvent extends Event {
    private final Entity entity;

    public RenderLabelEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
