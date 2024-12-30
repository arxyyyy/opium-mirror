package org.nrnr.opium.impl.event.text;

import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.text.MixinTextVisitFactory;

/**
 * @see MixinTextVisitFactory
 */
@Cancelable
public class TextVisitEvent extends Event {
    //
    private String text;

    /**
     * @param text
     */
    public TextVisitEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
