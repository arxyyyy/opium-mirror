package org.nrnr.opium.impl.module.misc;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.mixin.gui.screen.MixinDisconnectedScreen;

/**
 * @author chronos
 * @see MixinDisconnectedScreen
 * @since 1.0
 */
public class AutoReconnectModule extends ToggleModule {
    //
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "The delay between reconnects to a server", 0, 5, 100);

    /**
     *
     */
    public AutoReconnectModule() {
        super("AutoReconnect", "Automatically reconnects to a server " +
                "immediately after disconnecting", ModuleCategory.MISCELLANEOUS);
    }

    /**
     * @return
     */
    public int getDelay() {
        return delayConfig.getValue();
    }
}
