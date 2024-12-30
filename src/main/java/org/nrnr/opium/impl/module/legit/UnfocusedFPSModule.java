package org.nrnr.opium.impl.module.legit;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.FramerateLimitEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class UnfocusedFPSModule extends ToggleModule {
    //
    Config<Integer> limitConfig = new NumberConfig<>("Limit", "The FPS limit when game is in the background", 5, 30, 120);

    /**
     *
     */
    public UnfocusedFPSModule() {
        super("UnfocusedFPS", "Reduces FPS when game is in the background",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onFramerateLimit(FramerateLimitEvent event) {
        if (!mc.isWindowFocused()) {
            event.cancel();
            event.setFramerateLimit(limitConfig.getValue());
        }
    }
}
