package we.devs.opium.client.modules.player;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.api.utilities.*;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.client.events.EventRender3D;
import we.devs.opium.client.values.impl.ValueBoolean;
import we.devs.opium.client.values.impl.ValueCategory;
import we.devs.opium.client.values.impl.ValueColor;
import we.devs.opium.client.values.impl.ValueEnum;

import java.awt.*;

@RegisterModule(name = "AntiCrawl", description = "Mines Blocks above or below you to get you out of a crawl state.", category = Module.Category.PLAYER)
public class ModuleAntiCrawl extends Module {
    //ValueEnum autoSwitch = new ValueEnum("AutoSwitch", "Auto Switch", "Automatically switches to your Pickaxe.", AutoSwitch.None);

    ValueColor getSetting(String name, Color defaultC) {
        return new ValueColor(name, name, name, renderCategory, defaultC);
    }

    ValueCategory renderCategory = new ValueCategory("Render", "Render settings");
    ValueBoolean fill = new ValueBoolean("Fill", "Fill", "Show fill", true);
    ValueColor miningFill = getSetting("MiningFill", new Color(250, 20, 20));
    ValueColor finishedFill = getSetting("FinishedFill", new Color(20, 250, 20));
    ValueBoolean outline = new ValueBoolean("Outline", "Outline", "Show outline", true);
    ValueColor miningOutline = getSetting("MiningOutline", new Color(250, 20, 20).darker());
    ValueColor finishedOutline = getSetting("FinishedOutline", new Color(20, 250, 20).darker());
    ValueEnum easing = new ValueEnum("Easing", "Easing", "How to ease the progress rendering", renderCategory, Easing.EaseOutCircular);

    double progress = 0;
    BlockPos blockPos = null;
    int status = -2;

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
        assert mc.player != null;
        BlockPos pos = new BlockPos((int) Math.floor(mc.player.getX()), (int) mc.player.getY(), (int) Math.floor(mc.player.getZ()));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, pos.up(), Direction.UP));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, pos.down(), Direction.UP));
        status = -2;
    }

    @Override
    public void onTick() {
        super.onTick();
        if (nullCheck()) {
            return;
        }
        assert mc.world != null;
        assert mc.player != null;

        BlockPos pos = BlockPos.ofFloored(mc.player.getPos());
        BlockState aboveState = mc.world.getBlockState(pos.up());
        BlockState belowState = mc.world.getBlockState(pos.down());
        double up = aboveState.getHardness(mc.world, null);
        double down = belowState.getHardness(mc.world, null);

        if(up == -1 && down == -1) return;
        int slot = InventoryUtils.findBestTool((up < down && up != -1) ? aboveState : belowState, true);
        if (slot != -1) {
            if (HoleUtils.isInCrawlHole(mc.player)) {
                if ((up < down) && (up != -1)) {
                    blockPos = pos.up();
                    progress += BlockUtils.getBreakDelta(slot, aboveState);
                    status = 1;
                    if (progress <= 0.2) {
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                    }
                    if (progress > 1.0) {
                        InventoryUtils.switchSlot(slot, true);
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos.up(), Direction.UP));
                        progress = 0;
                        status = -2;
                    }
                } else if ((down < up) && (down != -1)) {
                    blockPos = pos.down();
                    progress += BlockUtils.getBreakDelta(slot, belowState);
                    status = 0;
                    if (progress <= 0.2) {
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos.down(), Direction.DOWN));
                    }
                    if (progress > 1.0) {
                        InventoryUtils.switchSlot(slot, true);
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos.down(), Direction.DOWN));
                        progress = 0;
                        status = -2;
                    }
                } else if ((up == down) && (up != -1)) {
                    progress += BlockUtils.getBreakDelta(slot, aboveState);
                    blockPos = pos.up();
                    status = 1;
                    if (progress <= 0.2) {
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                    }
                    if (progress > 1.0) {
                        InventoryUtils.switchSlot(slot, true);
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos.up(), Direction.UP));
                        progress = 0;
                        blockPos = null; // todo add fade out
                        status = -2;
                    }
                } else {
                    status = -1;
                    progress = 0;
                }
            }
        }
    }

    @Override
    public String getHudInfo() {
        if (status == 1) {
            return "Mining Up";
        }
        else if (status == 0) {
            return "Mining Down";
        }
        else if (status == -1) {
            return "Ur Fucked";
        }
        else {
            return "Idle";
        }
    }

    @Override
    public void onRender3D(EventRender3D event) {
        if(blockPos == null) return;
        if(progress >= 1) {
            Renderer3d.renderEdged(event.getMatrices(), finishedFill.getValue(), finishedOutline.getValue(), Vec3d.of(blockPos), new Vec3d(1, 1, 1));
        } else {
            double renderSize = switch (((Easing) easing.getValue())) {
                case None -> MathHelper.clamp(progress, 0, 1);
                case EaseOutCircular -> easeOutCirc(MathHelper.clamp(progress, 0, 1));
            };
            double renderProg = 0.5 - (renderSize) / 2;

            Vec3d init = Vec3d.of(blockPos).add(renderProg, renderProg, renderProg);
            Vec3d size = new Vec3d(renderSize, renderSize, renderSize);
            Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);
            if(fill.getValue() && outline.getValue()) Renderer3d.renderEdged(event.getMatrices(), miningFill.getValue(), miningOutline.getValue(), init, size);
            else if(fill.getValue()) Renderer3d.renderFilled(event.getMatrices(), miningFill.getValue(), init, size);
            else if(outline.getValue()) Renderer3d.renderOutline(event.getMatrices(), miningOutline.getValue(), init, size);
        }
    }



    static double easeOutCirc(double n) {
        return Math.sqrt(1 - Math.pow(n - 1, 2));
    }

    public enum AutoSwitch {
        None,
        Continously,
        OnBreak
    }

    public enum Easing {
        None,
        EaseOutCircular,
        // todo
    }

}
