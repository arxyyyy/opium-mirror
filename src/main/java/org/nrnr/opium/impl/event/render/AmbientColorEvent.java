package org.nrnr.opium.impl.event.render;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

import java.awt.*;

@Cancelable
public class AmbientColorEvent extends Event {
    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
