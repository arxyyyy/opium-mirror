package org.nrnr.opium.impl.module.render;

import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;

/**
 * @author chronos
 * @since 1.0
 */
public class TrajectoriesModule extends ToggleModule {

    public TrajectoriesModule() {
        super("Trajectories", "Renders the trajectory path of projectiles", ModuleCategory.RENDER);
    }
}
