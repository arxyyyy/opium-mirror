package org.nrnr.opium.impl.event.render.block;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
public class RenderTileEntityEvent extends Event {
    @Cancelable
    public static class EnchantingTableBook extends RenderTileEntityEvent {

    }
}
