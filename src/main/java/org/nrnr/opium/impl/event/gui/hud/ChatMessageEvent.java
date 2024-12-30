package org.nrnr.opium.impl.event.gui.hud;

import net.minecraft.text.Text;
import org.nrnr.opium.api.event.Event;

public class ChatMessageEvent extends Event {
    private final Text text;

    public ChatMessageEvent(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }
}
