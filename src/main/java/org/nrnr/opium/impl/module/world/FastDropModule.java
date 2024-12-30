package org.nrnr.opium.impl.module.world;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;
import org.nrnr.opium.init.Managers;

/**
 * @author chronos
 * @since 1.0
 */
public class FastDropModule extends ToggleModule {

    Config<Integer> delayConfig = new NumberConfig<>("Delay", "The delay for dropping items", 0, 0, 4);

    private int dropTicks;

    public FastDropModule() {
        super("FastDrop", "Drops items from the hotbar faster", ModuleCategory.WORLD);
    }

    /**
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }
        if (mc.options.dropKey.isPressed() && dropTicks > delayConfig.getValue()) {
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ITEM,
                    BlockPos.ORIGIN, Direction.DOWN));
            dropTicks = 0;
        }
        ++dropTicks;
    }
}
