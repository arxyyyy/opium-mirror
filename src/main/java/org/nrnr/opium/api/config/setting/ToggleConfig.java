package org.nrnr.opium.api.config.setting;

import org.nrnr.opium.Opium;
import org.nrnr.opium.api.config.ConfigContainer;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.util.render.animation.Animation;

/**
 * @author chronos
 * @see BooleanConfig
 * @since 1.0
 */
public class ToggleConfig extends BooleanConfig {
    public ToggleConfig(String name, String desc, Boolean val) {
        super(name, desc, val);
    }

    /**
     * @param val The param value
     */
    @Override
    public void setValue(Boolean val) {
        super.setValue(val);
        ConfigContainer container = getContainer();
        if (container instanceof ToggleModule toggle) {
            Animation anim = toggle.getAnimation();
            anim.setState(val);
            if (val) {
                Opium.EVENT_HANDLER.subscribe(toggle);
            } else {
                Opium.EVENT_HANDLER.unsubscribe(toggle);
            }
        }
    }

    public void enable() {
        ConfigContainer container = getContainer();
        if (container instanceof ToggleModule toggle) {
            toggle.enable();
        }
    }

    public void disable() {
        ConfigContainer container = getContainer();
        if (container instanceof ToggleModule toggle) {
            toggle.disable();
        }
    }
}
