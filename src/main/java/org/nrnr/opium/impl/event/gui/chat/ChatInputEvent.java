package org.nrnr.opium.impl.event.gui.chat;

import org.nrnr.opium.api.event.Event;

/**
 * @author chronos
 * @since 1.0
 */
public class ChatInputEvent extends Event {
    private final String chatText;

    public ChatInputEvent(String chatText) {
        this.chatText = chatText;
    }

    public String getChatText() {
        return chatText;
    }
}
