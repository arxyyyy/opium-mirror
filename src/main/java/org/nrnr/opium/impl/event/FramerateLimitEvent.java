package org.nrnr.opium.impl.event;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

/**
 *
 */
@Cancelable
public class FramerateLimitEvent extends Event {
    private int framerateLimit;

    public int getFramerateLimit() {
        return framerateLimit;
    }

    public void setFramerateLimit(int framerateLimit) {
        this.framerateLimit = framerateLimit;
    }
}
