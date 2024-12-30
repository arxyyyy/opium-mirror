package org.nrnr.opium.impl.event.gui.click;

import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.api.module.ToggleModule;

/**
 * @author chronos
 * @since 1.0
 */
public class ToggleGuiEvent extends Event {
    private final ToggleModule module;

    public ToggleGuiEvent(ToggleModule module) {
        this.module = module;
    }

    public ToggleModule getModule() {
        return module;
    }

    public boolean isEnabled() {
        return module.isEnabled();
    }
}
