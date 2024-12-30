package org.nrnr.opium.impl.module.misc;

import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.PacketEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chronos
 * @since 1.0
 */
public class AntiSpamModule extends ToggleModule {

    // TODO: ADD MORE SPAM CHECKS
    Config<Boolean> unicodeConfig = new BooleanConfig("Unicode", "Prevents unicode characters from being rendered in chat", false);
    //
    private final Map<UUID, String> messages = new HashMap<>();

    /**
     *
     */
    public AntiSpamModule() {
        super("AntiSpam", "Prevents players from spamming the game chat",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof ChatMessageS2CPacket packet) {
            if (unicodeConfig.getValue()) {
                String msg = packet.body().content();
                Pattern pattern = Pattern.compile("[\\x00-\\x7F]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(msg);
                if (matcher.find()) {
                    event.cancel();
                    return;
                }
            }
            // prevents same message spam
            final UUID sender = packet.sender();
            final String chatMessage = packet.body().content();
            String lastMessage = messages.get(sender);
            if (chatMessage.equalsIgnoreCase(lastMessage)) {
                event.cancel();
            } else if (lastMessage != null) {
                messages.replace(sender, chatMessage);
            } else {
                messages.put(sender, chatMessage);
            }
        }
    }
}
