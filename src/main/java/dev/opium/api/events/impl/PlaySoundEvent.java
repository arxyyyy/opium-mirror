package dev.opium.api.events.impl;

import dev.opium.api.events.Event;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends Event {
    public final SoundInstance sound;

    public PlaySoundEvent(SoundInstance soundInstance) {
        super(Stage.Pre);
        sound = soundInstance;
    }
}
