package org.nrnr.opium.impl.module.render;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.EnumConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;
import org.nrnr.opium.impl.event.config.ConfigUpdateEvent;
import org.nrnr.opium.impl.event.network.GameJoinEvent;
import org.nrnr.opium.impl.event.render.LightmapGammaEvent;

/**
 * @author chronos
 * @since 1.0
 */
public class FullbrightModule extends ToggleModule {

    Config<Brightness> brightnessConfig = new EnumConfig<>("Mode", "Mode for world brightness", Brightness.GAMMA, Brightness.values());

    public FullbrightModule() {
        super("Fullbright", "Brightens the world", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.world != null
                && brightnessConfig.getValue() == Brightness.POTION) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0)); // INFINITE
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null && mc.world != null
                && brightnessConfig.getValue() == Brightness.POTION) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @EventListener
    public void onGameJoin(GameJoinEvent event) {
        onDisable();
        onEnable();
    }

    @EventListener
    public void onLightmapGamma(LightmapGammaEvent event) {
        if (brightnessConfig.getValue() == Brightness.GAMMA) {
            event.cancel();
            event.setGamma(0xffffffff);
        }
    }

    @EventListener
    public void onConfigUpdate(ConfigUpdateEvent event) {
        if (mc.player != null && brightnessConfig == event.getConfig()
                && event.getStage() == EventStage.POST
                && brightnessConfig.getValue() != Brightness.POTION) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (brightnessConfig.getValue() == Brightness.POTION
                && !mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0));
        }
    }

    public enum Brightness {
        GAMMA,
        POTION
    }
}
