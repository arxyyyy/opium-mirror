package org.nrnr.opium.impl.event.entity;

import net.minecraft.util.math.Box;
import org.nrnr.opium.api.event.Event;

public class SetBBEvent extends Event {

    private final Box boundingBox;

    public SetBBEvent(Box boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Box getBoundingBox() {
        return boundingBox;
    }
}
