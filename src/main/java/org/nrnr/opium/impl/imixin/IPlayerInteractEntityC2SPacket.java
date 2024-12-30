package org.nrnr.opium.impl.imixin;

import net.minecraft.entity.Entity;
import org.nrnr.opium.util.network.InteractType;

/**
 *
 */
public interface IPlayerInteractEntityC2SPacket {
    /**
     * @return
     */
    Entity getEntity();

    /**
     * @return
     */
    InteractType getType();
}
