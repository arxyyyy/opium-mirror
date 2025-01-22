package we.devs.opium.client.modules.player;

import net.minecraft.block.BlockState;
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

@RegisterModule(name = "PacketMine", description = "Mines the Block that you hit without an animation", category = Module.Category.PLAYER)
public class ModulePacketMine extends Module {
    ValueBoolean strictSwitch = new ValueBoolean("StrictSwitch", "Strict Switch", "Switches to Pickaxe at the end of the mining process.", false);
    ValueNumber breakSpeed = new ValueNumber("BreakSpeed", "Break Speed", "The Amount of Progress needs to be made to mine the Block", 1.0, 0.5, 1.0);
    ValueNumber switchDelay = new ValueNumber("SwitchDelay", "Switch Delay", "Delay between switching back", 1.0, 0.1, 30);
    ValueBoolean rotate = new ValueBoolean("Rotate", "Rotate", "Will rotate you to the pos", true);
    ValueBoolean rotateC = new ValueBoolean("Rotate Client Side", "Rotate Client Side", "Will move your camera to the pos", false);

    ValueColor getSetting(String name, Color defaultC) {
        return new ValueColor(name, name, name, renderCategory, defaultC);
    }

    ValueCategory renderCategory = new ValueCategory("Render", "Render settings");
    ValueBoolean fill = new ValueBoolean("Fill", "Fill", "Show fill", true);
    ValueColor miningFill = getSetting("MiningFill", new Color(250, 20, 20, 150));
    ValueColor finishedFill = getSetting("FinishedFill", new Color(20, 250, 20, 150));
    ValueBoolean outline = new ValueBoolean("Outline", "Outline", "Show outline", true);
    ValueColor miningOutline = getSetting("MiningOutline", new Color(250, 20, 20, 150).darker());
    ValueColor finishedOutline = getSetting("FinishedOutline", new Color(20, 250, 20, 150).darker());
    ValueEnum easing = new ValueEnum("Easing", "Easing", "How to ease the progress rendering", renderCategory, Easing.EaseOutCircular);

    double progress = 0;
    BlockPos blockPos = null;
    int status = 2;

    @Override
    public void onEnable() {
        super.onEnable();
        progress = 0;
        status = -2;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        progress = 0;
        blockPos = null;
        status = 2;
    }

    @Override
    public void onDamageBlock(DamageBlockEvent event) {
        if (mc.world == null) return;
        BlockState blockState = mc.world.getBlockState(event.getPos());
        if (blockState.getHardness(mc.world, event.getPos()) >= 0) {
            blockPos = event.getPos();
            status = 1;
        } else {
            status = 0;
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (blockPos == null) return;
        assert mc.world != null;
        BlockState blockState = mc.world.getBlockState(blockPos);
        int slot = InventoryUtils.findBestTool(blockState, true);
        if (slot == -1) {
            return;
        }
        doMine(blockPos, blockState, slot, status);
    }

    void doMine(BlockPos pos, BlockState state, int slot, int s) {
        blockPos = pos;
        progress += BlockUtils.getBreakDelta(slot, state);
        status = s;
        assert mc.player != null;
        int beforeSlot = mc.player.getInventory().selectedSlot;

        if (progress <= 0.2) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        }
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

            // Reset progress and block position to remove rendering
            progress = 0;
            blockPos = null;
            status = 2;
        }
    }


    @Override
    public String getHudInfo() {
        if (status == 1) {
            return "Mining";
        } else if (status == 0) {
            return "Unbreakable";
        } else {
            return "Idle";
        }
    }

    @Override
    public void onRender3D(EventRender3D event) {
        if (blockPos == null) return; // Skip rendering if blockPos is null

        if (progress >= breakSpeed.getValue().doubleValue() - 0.05) {
            Renderer3d.renderEdged(event.getMatrices(), finishedFill.getValue(), finishedOutline.getValue(), Vec3d.of(blockPos), new Vec3d(1, 1, 1));
        } else {
            double renderSize = switch (((ModulePacketMine.Easing) easing.getValue())) {
                case None -> MathHelper.clamp(progress, 0, 1);
                case EaseOutCircular -> easeOutCirc(MathHelper.clamp(progress, 0, 1));
            };
            renderProg(event, renderSize, blockPos, miningFill, miningOutline, fill, outline);
        }
    }

    static void renderProg(EventRender3D event, double renderSize, BlockPos blockPos, ValueColor miningFill, ValueColor miningOutline, ValueBoolean fill, ValueBoolean outline) {
        double renderProg = 0.5 - (renderSize) / 2;

        Vec3d init = Vec3d.of(blockPos).add(renderProg, renderProg, renderProg);
        Vec3d size = new Vec3d(renderSize, renderSize, renderSize);
        Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);

        if (fill.getValue() && outline.getValue())
            Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);
        else if (fill.getValue())
            Renderer3d.renderFilled(event.getMatrices(), miningFill.getValue(), init, size);
        else if (outline.getValue())
            Renderer3d.renderOutline(event.getMatrices(), miningOutline.getValue(), init, size);
    }


    static double easeOutCirc(double n) {
        return Math.sqrt(1 - Math.pow(n - 1, 2));
    }

    public enum Easing {
        None,
        EaseOutCircular,
        // todo
    }
}
