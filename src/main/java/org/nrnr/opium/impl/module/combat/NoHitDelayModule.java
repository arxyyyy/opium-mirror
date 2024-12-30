package org.nrnr.opium.impl.module.combat;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.AttackCooldownEvent;

public class NoHitDelayModule extends ToggleModule {
    public NoHitDelayModule() {
        super("NoHitDelay", "Removes vanilla attack delay", ModuleCategory.EXPLOITS);
    }

    @EventListener
    public void onAttackCooldown(AttackCooldownEvent event) {
        event.cancel();
    }
}
