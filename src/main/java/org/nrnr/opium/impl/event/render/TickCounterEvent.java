package org.nrnr.opium.impl.event.render;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class TickCounterEvent extends Event {
    //
    private float ticks;

    /**
     * @return
     */
    public float getTicks() {
        return ticks;
    }

    /**
     * @param ticks
     */
    public void setTicks(float ticks) {
        this.ticks = ticks;
    }
}
