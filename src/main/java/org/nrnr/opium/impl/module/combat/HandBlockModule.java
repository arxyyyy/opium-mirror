package org.nrnr.opium.impl.module.combat;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.RotationModule;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.impl.event.network.PlayerTickEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.player.FindItemResult;
import org.nrnr.opium.util.player.InventoryUtil;

public class HandBlockModule extends RotationModule {

    Config<Boolean> autoSwap = new BooleanConfig("AutoSwap", "AutoSwap to Obsidian", true);
    Config<Boolean> swapBackConfig = new BooleanConfig("SwapBack", "Swaps Back after autoswap", false);
    Config<Boolean> swingConfig = new BooleanConfig("Swing", "Swings your mainhand", false);
    Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection", "Simple strict direction placements", false);
    Config<Boolean> grimConfig = new BooleanConfig("Grim", "For GrimAC", false);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate before breaking", false);


    public HandBlockModule() {
        super("HandBlock", "Mainly for flat, places block at crossair location", ModuleCategory.LEGIT);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet) {
            final BlockState state = packet.getState();
            final BlockPos targetPos = packet.getPos();
        }
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event){
        FindItemResult obsidianItemResult;
        if (autoSwap.getValue()) {
            obsidianItemResult = InventoryUtil.findInHotbar(Items.OBSIDIAN);
        }
        else {
            assert mc.player != null;
            obsidianItemResult = new FindItemResult(mc.player.getInventory().selectedSlot, 1);
        }

        assert mc.crosshairTarget != null;
        Vec3d crossairPosVec = mc.crosshairTarget.getPos();
        MinecraftClient client1 = MinecraftClient.getInstance();
        assert !(client1 == null);
        BlockHitResult hitResult = (BlockHitResult) client1.crosshairTarget;

        assert hitResult != null;
        Managers.INTERACT.placeBlock(hitResult.getBlockPos(), obsidianItemResult.slot(), grimConfig.getValue(), strictDirectionConfig.getValue(), swingConfig.getValue(), (state, angles) ->
        {
                    if (rotateConfig.getValue()) {
                        if (state) {
                            Managers.ROTATION.setRotationSilent(angles[0], angles[1], grimConfig.getValue());
                        } else {
                            Managers.ROTATION.setRotationSilentSync(grimConfig.getValue());
                        }
                    }
        });

    }




}
