package dev.opium.api.events.impl;

import dev.opium.api.events.Event;

public class GameLeftEvent extends Event {
    public GameLeftEvent() {
        super(Stage.Post);
    }
}
