package org.nrnr.opium.impl.event.render;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.render.MixinLightmapTextureManager;

/**
 * @author chronos
 * @see MixinLightmapTextureManager
 * @since 1.0
 */
@Cancelable
public class LightmapGammaEvent extends Event {
    //
    private int gamma;

    /**
     * @param gamma
     */
    public LightmapGammaEvent(int gamma) {
        this.gamma = gamma;
    }

    public int getGamma() {
        return gamma;
    }

    public void setGamma(int gamma) {
        this.gamma = gamma;
    }
}
