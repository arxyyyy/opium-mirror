package org.nrnr.opium.impl.event.entity;

import org.nrnr.opium.api.event.Event;

public class StepEvent extends Event {
    private final double stepHeight;

    public StepEvent(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getStepHeight() {
        return stepHeight;
    }
}
