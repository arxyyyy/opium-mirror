package org.nrnr.opium.impl.module.client;

import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ConcurrentModule;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.init.Managers;

import static net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN;

/**
 * @author xgraza
 * @since 1.0
 */
public final class ServerModule extends ConcurrentModule {
    Config<Boolean> packetKickConfig = new BooleanConfig("NoPacketKick", "Prevents thrown exceptions from kicking you", true);
    Config<Boolean> demoConfig = new BooleanConfig("NoDemo", "Prevents servers from forcing you to a demo screen", true);
    Config<Boolean> resourcePackConfig = new BooleanConfig("NoResourcePack", "Prevents server from forcing resource pack", false);

    public ServerModule() {
        super("Server", "Prevents servers actions on player", ModuleCategory.CLIENT);
    }

    @EventListener
    public void onPacketInbound(final PacketEvent.Inbound event) {
        if (event.getPacket() instanceof GameStateChangeS2CPacket packet) {
            if (packet.getReason() == DEMO_MESSAGE_SHOWN && !mc.isDemo() && demoConfig.getValue()) {
                Opium.info("Server attempted to use Demo mode features on you!");
                event.cancel();
            }
        }
        if (event.getPacket() instanceof ResourcePackSendS2CPacket && resourcePackConfig.getValue()) {
            event.cancel();
            Managers.NETWORK.sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.DECLINED));
        }
    }

    public boolean isPacketKick() {
        return packetKickConfig.getValue();
    }
}
