package org.nrnr.opium.impl.module.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.InteractBlockEvent;
import org.nrnr.opium.impl.event.network.PacketEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author chronos
 * @since 1.0
 */
public class AntiInteractModule extends ToggleModule {
    //
    List<Block> blacklist = Arrays.asList(Blocks.ENDER_CHEST, Blocks.ANVIL);

    public AntiInteractModule() {
        super("AntiInteract", "Prevents player from interacting with certain objects", ModuleCategory.WORLD);
    }

    @EventListener
    public void onInteractBlock(InteractBlockEvent event) {
        BlockPos pos = event.getHitResult().getBlockPos();
        BlockState state = mc.world.getBlockState(pos);
        if (blacklist.contains(state.getBlock())) {
            event.cancel();
            // Managers.NETWORK.sendSequencedPacket(sequence -> new PlayerInteractBlockC2SPacket(
            //        event.getHand(), event.getHitResult(), sequence));
        }
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet) {
            BlockPos pos = packet.getBlockHitResult().getBlockPos();
            BlockState state = mc.world.getBlockState(pos);
            if (blacklist.contains(state.getBlock())) {
                event.cancel();
            }
        }
    }
}
