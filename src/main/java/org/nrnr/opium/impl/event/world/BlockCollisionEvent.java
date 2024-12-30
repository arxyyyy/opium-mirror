package org.nrnr.opium.impl.event.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
@Cancelable
public class BlockCollisionEvent extends Event {
    //
    private final BlockPos pos;
    private final BlockState state;
    //
    private VoxelShape voxelShape;

    public BlockCollisionEvent(VoxelShape voxelShape, BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
        this.voxelShape = voxelShape;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public Block getBlock() {
        return state.getBlock();
    }

    public VoxelShape getVoxelShape() {
        return voxelShape;
    }

    public void setVoxelShape(VoxelShape voxelShape) {
        this.voxelShape = voxelShape;
    }
}
