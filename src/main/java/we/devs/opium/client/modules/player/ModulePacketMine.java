package we.devs.opium.client.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.BlockUtils;
import we.devs.opium.api.utilities.InventoryUtils;
import we.devs.opium.api.utilities.Renderer3d;
import we.devs.opium.client.events.EventRender3D;
import we.devs.opium.client.values.impl.*;


import java.awt.*;

@RegisterModule(name = "PacketMine", description = "Mines the Block that you hit without an animation", category = Module.Category.PLAYER)
public class ModulePacketMine extends Module {
    ValueBoolean strictSwitch = new ValueBoolean("StrictSwitch", "Strict Switch", "Switches to Pickaxe at the end of the mining process.", false);
    ValueNumber breakSpeed = new ValueNumber("BreakSpeed", "Break Speed", "The Amount of Progress needs to be made to mine the Block", 1.0, 0.5, 1.0);

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

    @Subscribe
    public void onStartBlockBreaking(BlockBreakingInfo event) {
        if (mc.world == null) return;
        BlockState blockState = mc.world.getBlockState(event.getPos());
        if (blockState.getHardness(mc.world, event.getPos()) >= 0) {
            if (event.getStage() >= 1) {
                blockPos = event.getPos();
                status = 1;
                int slot = InventoryUtils.findBestTool(blockState, true);
                doMine(blockPos, blockState, slot, status);
            }
        } else {
            status = 0;
        }
    }

    void doMine(BlockPos pos, BlockState state, int slot, int s) {
        blockPos = pos;
        progress += BlockUtils.getBreakDelta(slot, state);
        status = s;
        assert mc.player != null;
        if (progress <= 0.2) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        }
        if (progress > breakSpeed.getValue().doubleValue()) {
            InventoryUtils.switchSlot(slot, strictSwitch.getValue());
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
            progress = 0;
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
        if (blockPos == null) return;
        if (progress >= 1) {
            Renderer3d.renderEdged(event.getMatrices(), finishedFill.getValue(), finishedOutline.getValue(), Vec3d.of(blockPos), new Vec3d(1, 1, 1));
        } else {
            double renderSize = switch (((ModuleAntiCrawl.Easing) easing.getValue())) {
                case None -> MathHelper.clamp(progress, 0, 1);
                case EaseOutCircular -> easeOutCirc(MathHelper.clamp(progress, 0, 1));
            };
            double renderProg = 0.5 - (renderSize) / 2;

            Vec3d init = Vec3d.of(blockPos).add(renderProg, renderProg, renderProg);
            Vec3d size = new Vec3d(renderSize, renderSize, renderSize);
            Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);
            if (fill.getValue() && outline.getValue())
                Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);
            else if (fill.getValue()) Renderer3d.renderFilled(event.getMatrices(), miningFill.getValue(), init, size);
            else if (outline.getValue())
                Renderer3d.renderOutline(event.getMatrices(), miningOutline.getValue(), init, size);
        }
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
