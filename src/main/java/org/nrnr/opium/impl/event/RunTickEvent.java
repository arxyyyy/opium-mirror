package org.nrnr.opium.impl.event;

import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.MixinMinecraftClient;

/**
 * The main game loop event, this "tick" runs while the
 * {@link net.minecraft.client.MinecraftClient#running} var is <tt>true</tt>.
 *
 * @author chronos
 * @see MixinMinecraftClient
 * @since 1.0
 */
public class RunTickEvent extends Event {

}
