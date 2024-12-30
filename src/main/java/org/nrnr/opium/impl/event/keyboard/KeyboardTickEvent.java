package org.nrnr.opium.impl.event.keyboard;

import net.minecraft.client.input.Input;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.StageEvent;

@Cancelable
public class KeyboardTickEvent extends StageEvent {

    private final Input input;

    public KeyboardTickEvent(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return input;
    }
}
