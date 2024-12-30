package org.nrnr.opium.impl.module.movement;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.entity.JumpDelayEvent;

public class NoJumpDelayModule extends ToggleModule {
    public NoJumpDelayModule() {
        super("NoJumpDelay", "Removes the vanilla jump delay", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onJumpDelay(JumpDelayEvent event) {
        event.cancel();
    }
}
