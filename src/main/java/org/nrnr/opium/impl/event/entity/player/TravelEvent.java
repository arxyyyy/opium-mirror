package org.nrnr.opium.impl.event.entity.player;

import net.minecraft.util.math.Vec3d;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.StageEvent;

@Cancelable
public class TravelEvent extends StageEvent {
    private final Vec3d movementInput;

    public TravelEvent(Vec3d movementInput) {
        this.movementInput = movementInput;
    }

    public Vec3d getMovementInput() {
        return movementInput;
    }
}
