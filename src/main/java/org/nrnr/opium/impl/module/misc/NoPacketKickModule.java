package org.nrnr.opium.impl.module.misc;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.DecodePacketEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class NoPacketKickModule extends ToggleModule {

    /**
     *
     */
    public NoPacketKickModule() {
        super("NoPacketKick", "Prevents getting kicked by packets", ModuleCategory.MISCELLANEOUS);
    }

    // TODO: Add more packet kick checks
    @EventListener
    public void onDecodePacket(DecodePacketEvent event) {
        event.cancel();
    }
}
