package org.nrnr.opium.impl.event;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.MixinMinecraftClient;

/**
 * Allows mining and eating at the same time
 *
 * @see MixinMinecraftClient
 */
@Cancelable
public class ItemMultitaskEvent extends Event {

}
