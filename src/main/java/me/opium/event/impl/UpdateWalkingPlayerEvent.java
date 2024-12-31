package me.opium.event.impl;

import me.opium.event.Event;
import me.opium.event.Stage;

public class UpdateWalkingPlayerEvent extends Event {
    private final Stage stage;

    public UpdateWalkingPlayerEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
