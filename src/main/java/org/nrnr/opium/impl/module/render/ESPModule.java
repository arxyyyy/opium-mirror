package org.nrnr.opium.impl.module.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.ColorConfig;
import org.nrnr.opium.api.config.setting.EnumConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.EntityOutlineEvent;
import org.nrnr.opium.impl.event.entity.decoration.TeamColorEvent;
import org.nrnr.opium.util.world.EntityUtil;

import java.awt.*;

/**
 * @author chronos
 * @since 1.0
 */
public class ESPModule extends ToggleModule {
    //
    Config<ESPMode> modeConfig = new EnumConfig<>("Mode", "ESP rendering mode", ESPMode.GLOW, ESPMode.values());
    Config<Boolean> playersConfig = new BooleanConfig("Players", "Render players through walls", true);
    Config<Boolean> selfConfig = new BooleanConfig("Self", "Render self through walls", true);
    Config<Color> playersColorConfig = new ColorConfig("PlayersColor", "The render color for players", new Color(200, 60, 60), false, () -> playersConfig.getValue() || selfConfig.getValue());
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters", "Render monsters through walls", true);
    Config<Color> monstersColorConfig = new ColorConfig("MonstersColor", "The render color for monsters", new Color(200, 60, 60), false, () -> monstersConfig.getValue());
    Config<Boolean> animalsConfig = new BooleanConfig("Animals", "Render animals through walls", true);
    Config<Color> animalsColorConfig = new ColorConfig("AnimalsColor", "The render color for animals", new Color(0, 200, 0), false, () -> animalsConfig.getValue());
    Config<Boolean> vehiclesConfig = new BooleanConfig("Vehicles", "Render vehicles through walls", false);
    Config<Color> vehiclesColorConfig = new ColorConfig("VehiclesColor", "The render color for vehicles", new Color(200, 100, 0), false, () -> vehiclesConfig.getValue());
    Config<Boolean> itemsConfig = new BooleanConfig("Items", "Render dropped items through walls", false);
    Config<Color> itemsColorConfig = new ColorConfig("ItemsColor", "The render color for items", new Color(200, 100, 0), false, () -> itemsConfig.getValue());
    Config<Boolean> crystalsConfig = new BooleanConfig("EndCrystals", "Render end crystals through walls", false);
    Config<Color> crystalsColorConfig = new ColorConfig("EndCrystalsColor", "The render color for end crystals", new Color(200, 100, 200), false, () -> crystalsConfig.getValue());

    public ESPModule() {
        super("ESP", "See entities and objects through walls", ModuleCategory.RENDER);
    }

    @EventListener
    public void onEntityOutline(EntityOutlineEvent event) {
        if (modeConfig.getValue() == ESPMode.GLOW && checkESP(event.getEntity())) {
            event.cancel();
        }
    }

    @EventListener
    public void onTeamColor(TeamColorEvent event) {
        if (modeConfig.getValue() == ESPMode.GLOW && checkESP(event.getEntity())) {
            event.cancel();
            event.setColor(getESPColor(event.getEntity()).getRGB());
        }
    }

    public Color getESPColor(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return playersColorConfig.getValue();
        }
        if (EntityUtil.isMonster(entity)) {
            return monstersColorConfig.getValue();
        }
        if (EntityUtil.isNeutral(entity) || EntityUtil.isPassive(entity)) {
            return animalsColorConfig.getValue();
        }
        if (EntityUtil.isVehicle(entity)) {
            return vehiclesColorConfig.getValue();
        }
        if (entity instanceof EndCrystalEntity) {
            return crystalsColorConfig.getValue();
        }
        if (entity instanceof ItemEntity) {
            return itemsColorConfig.getValue();
        }
        return null;
    }

    public boolean checkESP(Entity entity) {
        if (entity instanceof PlayerEntity && playersConfig.getValue()) {
            return selfConfig.getValue() || entity != mc.player;
        }
        return EntityUtil.isMonster(entity) && monstersConfig.getValue()
                || (EntityUtil.isNeutral(entity)
                || EntityUtil.isPassive(entity)) && animalsConfig.getValue()
                || EntityUtil.isVehicle(entity) && vehiclesConfig.getValue()
                || entity instanceof EndCrystalEntity && crystalsConfig.getValue()
                || entity instanceof ItemEntity && itemsConfig.getValue();
    }

    public enum ESPMode {
        // OUTLINE,
        GLOW
    }
}
