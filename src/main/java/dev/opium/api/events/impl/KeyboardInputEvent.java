package dev.opium.api.events.impl;

import dev.opium.api.events.Event;

public class KeyboardInputEvent extends Event {
    public KeyboardInputEvent() {
        super(Stage.Pre);
    }
}
