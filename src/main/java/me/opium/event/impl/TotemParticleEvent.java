package me.opium.event.impl;

import me.opium.event.Event;
import java.awt.*;

public class TotemParticleEvent extends Event {
    public double velocityX, velocityY, velocityZ;
    public Color color;
    public TotemParticleEvent(double velocityX, double velocityY, double velocityZ) {

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }
}
