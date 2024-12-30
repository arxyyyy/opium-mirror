package org.nrnr.opium.impl.module.movement;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.entity.LevitationEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class AntiLevitationModule extends ToggleModule {

    /**
     *
     */
    public AntiLevitationModule() {
        super("AntiLevitation", "Prevents the player from being levitated",
                ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onLevitation(LevitationEvent event) {
        event.cancel();
    }
}
