package org.nrnr.opium.impl.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;
import org.nrnr.opium.impl.event.entity.LookDirectionEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class YawModule extends ToggleModule {

    Config<Boolean> lockConfig = new BooleanConfig("Lock", "Locks the yaw in cardinal direction", false);

    /**
     *
     */
    public YawModule() {
        super("Yaw", "Locks player yaw to a cardinal axis",
                ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE) {
            float yaw = Math.round(mc.player.getYaw() / 45.0f) * 45.0f;
            Entity vehicle = mc.player.getVehicle();
            if (vehicle != null) {
                vehicle.setYaw(yaw);
                if (vehicle instanceof LlamaEntity llama) {
                    llama.setHeadYaw(yaw);
                }
                return;
            }
            mc.player.setYaw(yaw);
            mc.player.setHeadYaw(yaw);
        }
    }

    @EventListener
    public void onLookDirection(LookDirectionEvent event) {
        if (lockConfig.getValue()) {
            event.cancel();
        }
    }
}
