package org.nrnr.opium.impl.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;
import org.nrnr.opium.impl.event.entity.passive.EntitySteerEvent;
import org.nrnr.opium.impl.event.network.MountJumpStrengthEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class EntityControlModule extends ToggleModule {
    //
    Config<Float> jumpStrengthConfig = new NumberConfig<>("JumpStrength", "The fixed jump strength of the mounted entity", 0.1f, 0.7f, 2.0f);
    Config<Boolean> noPigMoveConfig = new BooleanConfig("NoPigAI", "Prevents the pig movement when controlling pigs", false);

    /**
     *
     */
    public EntityControlModule() {
        super("EntityControl", "Allows you to steer entities without a saddle",
                ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        Entity vehicle = mc.player.getVehicle();
        if (vehicle == null) {
            return;
        }
        vehicle.setYaw(mc.player.getYaw());
        if (vehicle instanceof LlamaEntity llama) {
            llama.headYaw = mc.player.getYaw();
        }
    }

    @EventListener
    public void onEntitySteer(EntitySteerEvent event) {
        event.cancel();
    }

    @EventListener
    public void onMountJumpStrength(MountJumpStrengthEvent event) {
        event.cancel();
        event.setJumpStrength(jumpStrengthConfig.getValue());
    }
}
