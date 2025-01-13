package we.devs.opium.client.modules.player;

import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMember;
import we.devs.opium.api.utilities.BlockUtils;
import we.devs.opium.api.utilities.ChatUtils;
import we.devs.opium.api.utilities.HoleUtils;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.InventoryUtils;

@RegisterModule(name = "AntiCrawl", description = "Mines Blocks above or below you to get you out of a crawl state.", category = Module.Category.PLAYER)
public class ModuleAntiCrawl extends Module {
    //ValueEnum autoSwitch = new ValueEnum("AutoSwitch", "Auto Switch", "Automatically switches to your Pickaxe.", AutoSwitch.None);

    double progress = 0;
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

    public enum AutoSwitch {
        None,
        Continously,
        OnBreak
    }

}
