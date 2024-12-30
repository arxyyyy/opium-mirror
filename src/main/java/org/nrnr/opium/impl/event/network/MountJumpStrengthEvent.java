package org.nrnr.opium.impl.event.network;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class MountJumpStrengthEvent extends Event {
    //
    private float jumpStrength;

    public float getJumpStrength() {
        return jumpStrength;
    }

    public void setJumpStrength(float jumpStrength) {
        this.jumpStrength = jumpStrength;
    }
}
