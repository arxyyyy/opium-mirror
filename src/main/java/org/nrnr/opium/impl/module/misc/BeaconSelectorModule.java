package org.nrnr.opium.impl.module.misc;

import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.ScreenOpenEvent;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.impl.gui.beacon.BeaconSelectorScreen;
import org.nrnr.opium.mixin.accessor.AccessorUpdateBeaconC2SPacket;

import java.util.Optional;

/**
 * @author chronos
 * @since 1.0
 */
public class BeaconSelectorModule extends ToggleModule {
    //
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;
    //
    private boolean customBeacon;

    /**
     *
     */
    public BeaconSelectorModule() {
        super("BeaconSelector", "Allows you to change beacon effects",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (event.getPacket() instanceof UpdateBeaconC2SPacket packet) {
            ((AccessorUpdateBeaconC2SPacket) packet).setPrimaryEffect(Optional.ofNullable(primaryEffect));
            ((AccessorUpdateBeaconC2SPacket) packet).setSecondaryEffect(Optional.ofNullable(secondaryEffect));
        }
    }

    @EventListener
    public void onScreenOpen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof BeaconScreen screen && !customBeacon) {
            event.cancel();
            customBeacon = true;
            mc.setScreen(new BeaconSelectorScreen(screen.getScreenHandler(),
                    mc.player.getInventory(), screen.getTitle()));
            customBeacon = false;
        }
    }
}
