package org.nrnr.opium.impl.event.entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.nrnr.opium.api.event.Event;

public class StatusEffectEvent extends Event {

    private final StatusEffectInstance statusEffectInstance;

    public StatusEffectEvent(StatusEffectInstance statusEffectInstance) {
        this.statusEffectInstance = statusEffectInstance;
    }

    public StatusEffectInstance getStatusEffect() {
        return statusEffectInstance;
    }

    public static class Add extends StatusEffectEvent {

        public Add(StatusEffectInstance statusEffectInstance) {
            super(statusEffectInstance);
        }
    }

    public static class Remove extends StatusEffectEvent {

        public Remove(StatusEffectInstance statusEffectInstance) {
            super(statusEffectInstance);
        }
    }
}
