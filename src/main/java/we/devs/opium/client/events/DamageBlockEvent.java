package we.devs.opium.client.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import we.devs.opium.api.manager.event.EventArgument;
import we.devs.opium.api.manager.event.EventListener;

public class DamageBlockEvent extends EventArgument {
    private final BlockPos pos;
    private final Direction direction;

    public DamageBlockEvent(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void call(EventListener listener) {
        listener.onDamageBlock(this);
    }
}
