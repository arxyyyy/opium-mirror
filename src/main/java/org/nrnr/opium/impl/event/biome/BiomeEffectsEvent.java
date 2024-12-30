package org.nrnr.opium.impl.event.biome;

import net.minecraft.world.biome.BiomeParticleConfig;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class BiomeEffectsEvent extends Event {

    private BiomeParticleConfig particleConfig;

    public BiomeParticleConfig getParticleConfig() {
        return particleConfig;
    }

    public void setParticleConfig(BiomeParticleConfig particleConfig) {
        this.particleConfig = particleConfig;
    }
}
