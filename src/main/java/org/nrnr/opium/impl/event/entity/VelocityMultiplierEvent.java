package org.nrnr.opium.impl.event.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class VelocityMultiplierEvent extends Event {
    //
    private final BlockState state;

    /**
     * @param state
     */
    public VelocityMultiplierEvent(BlockState state) {
        this.state = state;
    }

    /**
     * @return
     */
    public Block getBlock() {
        return state.getBlock();
    }
}
