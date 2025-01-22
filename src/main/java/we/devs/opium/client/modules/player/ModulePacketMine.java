package we.devs.opium.client.modules.player;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.Opium;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.*;
import we.devs.opium.client.events.DamageBlockEvent;
import we.devs.opium.client.events.EventRender3D;
import we.devs.opium.client.values.impl.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

@RegisterModule(name = "PacketMine", description = "Mines the blocks you hit sequentially without an animation", category = Module.Category.PLAYER)
public class ModulePacketMine extends Module {
    private final ValueBoolean strictSwitch = new ValueBoolean("StrictSwitch", "Strict Switch", "Switches to Pickaxe at the end of the mining process.", false);
    private final ValueNumber breakSpeed = new ValueNumber("BreakSpeed", "Break Speed", "The Amount of Progress needs to be made to mine the Block", 1.0, 0.5, 1.0);
    private final ValueNumber switchDelay = new ValueNumber("SwitchDelay", "Switch Delay", "Delay between switching back", 1.0, 0.1, 30);
    private final ValueBoolean rotate = new ValueBoolean("Rotate", "Rotate", "Will rotate you to the pos", true);
    private final ValueBoolean rotateC = new ValueBoolean("RotateClientSide", "Rotate Client Side", "Will move your camera to the pos", false);

    private final ValueCategory renderCategory = new ValueCategory("Render", "Render settings");
    private final ValueBoolean fill = new ValueBoolean("Fill", "Fill", "Show fill", true);
    private final ValueColor miningFill = new ValueColor("MiningFill", "MiningFill", "Color while mining", renderCategory, new Color(250, 20, 20, 150));
    private final ValueColor finishedFill = new ValueColor("FinishedFill", "FinishedFill", "Color after mining", renderCategory, new Color(20, 250, 20, 150));
    private final ValueBoolean outline = new ValueBoolean("Outline", "Outline", "Show outline", true);
    private final ValueColor miningOutline = new ValueColor("MiningOutline", "MiningOutline", "Outline color while mining", renderCategory, new Color(250, 20, 20, 150).darker());
    private final ValueColor finishedOutline = new ValueColor("FinishedOutline", "FinishedOutline", "Outline color after mining", renderCategory, new Color(20, 250, 20, 150).darker());

    private final ValueColor queColor = new ValueColor("QueueOutline", "QueueOutline", "Outline color for Queue", renderCategory, new Color(177, 177, 177, 82).darker());

    private final Queue<BlockPos> blockQueue = new LinkedList<>();
    private BlockPos currentBlock = null;
    private double progress = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        blockQueue.clear();
        currentBlock = null;
        progress = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        blockQueue.clear();
        currentBlock = null;
        progress = 0;
    }

    @Override
    public void onDamageBlock(DamageBlockEvent event) {
        if (mc.world == null) return;

        BlockState blockState = mc.world.getBlockState(event.getPos());
        if (blockState.getHardness(mc.world, event.getPos()) >= 0) {
            blockQueue.offer(event.getPos());
        }
    }

    @Override
    public void onTick() {
        super.onTick();

        // If no current block is set, but there's something in the queue, select the first one
        if (currentBlock == null && !blockQueue.isEmpty()) {
            currentBlock = blockQueue.poll();
            progress = 0; // Reset progress when we start a new block
            assert mc.player != null;
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, currentBlock, Direction.UP));
        }

        // If there’s no current block, skip
        if (currentBlock == null) return;

        assert mc.world != null;
        BlockState blockState = mc.world.getBlockState(currentBlock);

        // Ensure the block is valid (not air or unmineable)
        if (blockState.isAir() || blockState.getHardness(mc.world, currentBlock) < 0) {
            currentBlock = null;
            return; // Skip if the block is invalid
        }

        int slot = InventoryUtils.findBestTool(blockState, true);
        if (slot == -1) {
            currentBlock = null;
            return; // Skip if no suitable tool is found
        }

        mineBlock(currentBlock, blockState, slot);
    }

    private void mineBlock(BlockPos pos, BlockState state, int slot) {
        progress += BlockUtils.getBreakDelta(slot, state);
        assert mc.player != null;
        int beforeSlot = mc.player.getInventory().selectedSlot;

        // If progress is low, send the start packet to begin mining
        if (progress <= 0.2) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        }

        // Once the block is broken enough, stop the mining process
        if (progress > breakSpeed.getValue().doubleValue()) {
            InventoryUtils.switchSlot(slot, !strictSwitch.getValue());
            if (rotate.getValue()) RotationsUtil.rotateToBlockPos(pos, rotateC.getValue());
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

            // Add a delay before switching back to the original slot
            new Thread(() -> {
                try {
                    Thread.sleep((long) (switchDelay.getValue().doubleValue() * 1000)); // Convert delay from seconds to milliseconds
                    InventoryUtils.switchSlot(beforeSlot, !strictSwitch.getValue());
                } catch (InterruptedException e) {
                    Opium.LOGGER.error("[PacketMine] {}", e.getMessage());
                }
            }).start();

            // Reset progress and move to the next block in the queue
            progress = 0;
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, Direction.UP));
            currentBlock = null; // This should allow the next block to be selected
        }
    }


    @Override
    public void onRender3D(EventRender3D event) {
        if (currentBlock == null) return;

        if (progress >= breakSpeed.getValue().doubleValue() - 0.05) {
            Renderer3d.renderEdged(event.getMatrices(), finishedFill.getValue(), finishedOutline.getValue(), Vec3d.of(currentBlock), new Vec3d(1, 1, 1));
        } else {
            double renderSize = MathHelper.clamp(progress, 0, 1);
            renderProgress(event, renderSize, currentBlock, miningFill, miningOutline, fill, outline);
        }

        for (BlockPos blockPos : blockQueue) {
            assert mc.world != null;
            BlockState blockState = mc.world.getBlockState(blockPos);
            if (blockState.isAir()) continue;
            Renderer3d.renderEdged(event.getMatrices(), new Color(0 , 0,0,0), queColor.getValue(), Vec3d.of(blockPos), new Vec3d(1, 1, 1));
        }
    }

    public static void renderProgress(EventRender3D event, double renderSize, BlockPos blockPos, ValueColor fillColor, ValueColor outlineColor, ValueBoolean fill, ValueBoolean outline) {
        double renderProg = 0.5 - (renderSize) / 2;

        Vec3d init = Vec3d.of(blockPos).add(renderProg, renderProg, renderProg);
        Vec3d size = new Vec3d(renderSize, renderSize, renderSize);
        if (fill.getValue()) Renderer3d.renderFilled(event.getMatrices(), fillColor.getValue(), init, size);
        if (outline.getValue()) Renderer3d.renderOutline(event.getMatrices(), outlineColor.getValue(), init, size);
    }
}
