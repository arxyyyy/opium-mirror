package org.nrnr.opium.impl.module.misc;

import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.chat.ChatUtil;
import org.nrnr.opium.util.math.timer.CacheTimer;
import org.nrnr.opium.util.math.timer.Timer;

/**
 * @author chronos
 * @since 1.0
 */
public class AutoAcceptModule extends ToggleModule {
    //
    private final Timer acceptTimer = new CacheTimer();
    //
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay before" +
            " accepting teleport requests", 0.0f, 3.0f, 10.0f);

    /**
     *
     */
    public AutoAcceptModule() {
        super("AutoAccept", "Automatically accepts teleport requests",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof ChatMessageS2CPacket packet) {
            String text = packet.body().content();
            if ((text.contains("has requested to teleport to you.")
                    || text.contains("has requested you teleport to them."))
                    && acceptTimer.passed(delayConfig.getValue() * 1000)) {
                for (String friend : Managers.SOCIAL.getFriends()) {
                    if (text.contains(friend)) {
                        ChatUtil.serverSendMessage("/tpaccept");
                        break;
                    }
                }
            }
        }
    }
}
