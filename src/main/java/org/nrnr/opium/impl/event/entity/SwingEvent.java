package org.nrnr.opium.impl.event.entity;

import net.minecraft.util.Hand;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class SwingEvent extends Event {
    private final Hand hand;

    public SwingEvent(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }
}
