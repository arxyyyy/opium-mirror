package org.nrnr.opium.impl.module.movement;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class AutoWalkModule extends ToggleModule {
    //
    Config<Boolean> lockConfig = new BooleanConfig("Lock", "Stops movement when sneaking or jumping", false);

    /**
     *
     */
    public AutoWalkModule() {
        super("AutoWalk", "Automatically moves forward", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE) {
            mc.options.forwardKey.setPressed(!mc.options.sneakKey.isPressed()
                    && (!lockConfig.getValue() || (!mc.options.jumpKey.isPressed() && mc.player.isOnGround())));
        }
    }
}
