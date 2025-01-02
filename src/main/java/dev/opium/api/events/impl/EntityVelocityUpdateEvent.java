package dev.opium.api.events.impl;

import dev.opium.api.events.Event;

public class EntityVelocityUpdateEvent extends Event {
    public EntityVelocityUpdateEvent() {
        super(Stage.Pre);
    }
}
