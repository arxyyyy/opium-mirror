package org.nrnr.opium.impl.event;

import net.minecraft.client.gui.screen.Screen;
import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
public class ScreenOpenEvent extends Event {
    //
    private final Screen screen;

    public ScreenOpenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
