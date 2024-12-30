package org.nrnr.opium.api.discord.callbacks;

import com.sun.jna.Callback;
import org.nrnr.opium.api.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}
