package org.nrnr.opium.impl.module.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;
import org.nrnr.opium.impl.event.entity.EntityDeathEvent;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.util.chat.ChatUtil;

public class AutoEZModule extends ToggleModule {

    public AutoEZModule() {
        super("AutoEz", "Simple AutoEZ module",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof EntityDeathEvent packet) {
            LivingEntity entity = packet.getEntity();
            String name = String.valueOf(entity.getDisplayName());
            ChatUtil.clientSendMessage(name + " has died");

        }
    }

    @EventListener
    public void onTick(TickEvent event){
        if (mc.player == null || mc.world == null) return;
        for (PlayerEntity otherPlayer : mc.world.getPlayers()) {
            if (otherPlayer.isDead()){
                ChatUtil.clientSendMessage("Opium strong " + otherPlayer.getDisplayName());
            }
        }
    }
}
