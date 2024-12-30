package org.nrnr.opium.impl.event.network;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class AttackBlockEvent extends Event {
    //
    private final BlockPos pos;
    private final BlockState state;
    //
    private final Direction direction;

    /**
     * @param pos
     * @param state
     * @param direction
     */
    public AttackBlockEvent(BlockPos pos, BlockState state, Direction direction) {
        this.pos = pos;
        this.state = state;
        this.direction = direction;
    }

    /**
     * @return
     */
    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    /**
     * @return
     */
    public Direction getDirection() {
        return direction;
    }
}
