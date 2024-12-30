package org.nrnr.opium.impl.event.entity.player;


import org.nrnr.opium.api.event.Event;

public class SendMovePacketPostEvent extends Event {
    private float yaw;
    private float pitch;

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
