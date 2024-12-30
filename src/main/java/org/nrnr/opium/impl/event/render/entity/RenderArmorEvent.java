package org.nrnr.opium.impl.event.render.entity;

import net.minecraft.entity.LivingEntity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class RenderArmorEvent extends Event {
    private final LivingEntity entity;

    public RenderArmorEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
