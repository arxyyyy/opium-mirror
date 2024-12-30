package org.nrnr.opium.impl.event.config;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.event.StageEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class ConfigUpdateEvent extends StageEvent {
    //
    private final Config<?> config;

    /**
     * @param config
     */
    public ConfigUpdateEvent(Config<?> config) {
        this.config = config;
    }

    /**
     * @return
     */
    public Config<?> getConfig() {
        return config;
    }
}
