package dev.opium.api.events.impl;

import dev.opium.api.events.Event;

public class UpdateWalkingPlayerEvent extends Event {
    public UpdateWalkingPlayerEvent(Stage stage) {
        super(stage);
    }
}
