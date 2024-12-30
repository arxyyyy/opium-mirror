package org.nrnr.opium.mixin.accessor;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author chronos
 * @see LightmapTextureManager
 * @since 1.0
 */
@Mixin(LightmapTextureManager.class)
public interface AccessorLightmapTextureManager {
    /**
     * @param dirty
     */
    @Accessor("dirty")
    void setUpdateLightmap(boolean dirty);
}
