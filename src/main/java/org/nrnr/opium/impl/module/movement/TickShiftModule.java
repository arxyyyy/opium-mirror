package org.nrnr.opium.impl.module.movement;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.PlayerUpdateEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.player.MovementUtil;

/**
 * @author chronos
 * @since 1.0
 */
public class TickShiftModule extends ToggleModule {
    // Basically auto timer for NCP
    //
    Config<Integer> ticksConfig = new NumberConfig<>("MaxTicks", "Maximum charge ticks", 1, 20, 40);
    Config<Integer> packetsConfig = new NumberConfig<>("Packets", "Packets to release from storage every tick", 1, 1, 5);
    Config<Integer> chargeSpeedConfig = new NumberConfig<>("ChargeSpeed", "The speed to charge the stored packets", 1, 1, 5);
    //
    private int packets;

    /**
     *
     */
    public TickShiftModule() {
        super("TickShift", "Exploits NCP to speed up ticks",
                ModuleCategory.MOVEMENT);
    }

    @Override
    public String getModuleData() {
        return String.valueOf(packets);
    }

    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }
        if (MovementUtil.isMoving() || !mc.player.isOnGround()) {
            packets -= packetsConfig.getValue();
            if (packets <= 0) {
                packets = 0;
                Managers.TICK.setClientTick(1.0f);
                return;
            }
            Managers.TICK.setClientTick(packetsConfig.getValue() + 1.0f);
        } else {
            packets += chargeSpeedConfig.getValue();
            if (packets > ticksConfig.getValue()) {
                packets = ticksConfig.getValue();
            }
        }
    }
}
