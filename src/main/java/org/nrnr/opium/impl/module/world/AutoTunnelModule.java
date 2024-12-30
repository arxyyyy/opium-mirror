package org.nrnr.opium.impl.module.world;

import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;

/**
 * @author chronos
 * @since 1.0
 */
public class AutoTunnelModule extends ToggleModule {

    public AutoTunnelModule() {
        super("AutoTunnel", "Automatically mines a tunnel", ModuleCategory.WORLD);
    }
}
