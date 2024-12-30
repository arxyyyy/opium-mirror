package org.nrnr.opium.impl.event.entity;

import org.nrnr.opium.api.event.Event;

public final class JumpRotationEvent extends Event {
    private float yaw;


    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
