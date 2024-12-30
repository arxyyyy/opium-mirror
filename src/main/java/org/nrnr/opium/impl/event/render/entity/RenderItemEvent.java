package org.nrnr.opium.impl.event.render.entity;

import net.minecraft.entity.ItemEntity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class RenderItemEvent extends Event {
    private final ItemEntity itemEntity;

    public RenderItemEvent(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public ItemEntity getItem() {
        return itemEntity;
    }
}
