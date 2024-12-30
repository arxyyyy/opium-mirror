package org.nrnr.opium.impl.module.render;

import net.minecraft.entity.player.PlayerEntity;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.render.entity.RenderEntityInvisibleEvent;

/**
 * @author xgraza
 * @since 1.0
 */
public final class TrueSightModule extends ToggleModule {
    Config<Boolean> onlyPlayersConfig = new BooleanConfig("OnlyPlayers", "If to only reveal invisible players", true);

    public TrueSightModule() {
        super("TrueSight", "Allows you to see invisible entities", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderEntityInvisible(final RenderEntityInvisibleEvent event) {
        if (event.getEntity().isInvisible() && (!onlyPlayersConfig.getValue() || event.getEntity() instanceof PlayerEntity)) {
            event.cancel();
        }
    }
}
