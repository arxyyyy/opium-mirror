package we.devs.opium.client.modules.combat;

import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import we.devs.opium.api.utilities.BlockUtils;
import we.devs.opium.api.utilities.HoleUtils;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.InventoryUtils;

@RegisterModule(name = "AntiCrawl", description = "Mines Blocks Over or Under you to get you out of a crawl state.", category = Module.Category.COMBAT)
public class ModuleAntiCrawl extends Module {
    //ValueEnum autoSwitch = new ValueEnum("AutoSwitch", "Auto Switch", "Automatically switches to your Pickaxe.", AutoSwitch.None);

    double progress = 0;

    @Override
    public void onTick() {
        super.onTick();
        if (super.nullCheck()) {
            return;
        }
        int slot = InventoryUtils.findItem(Items.NETHERITE_PICKAXE, 0, 8);
        BlockPos pos = new BlockPos((int) Math.floor(mc.player.getX()), (int) mc.player.getY(), (int) Math.floor(mc.player.getZ()));
        BlockState state = mc.world.getBlockState(pos);
        progress += BlockUtils.getBreakDelta(slot, state);

        if (HoleUtils.isInCrawlHole(mc.player)) {
            if ((BlockUtils.getBlockResistance(pos.up()) == BlockUtils.BlockResistance.Breakable || BlockUtils.getBlockResistance(pos.up()) == BlockUtils.BlockResistance.Resistant) || (BlockUtils.getBlockResistance(pos.down()) == BlockUtils.BlockResistance.Breakable || BlockUtils.getBlockResistance(pos.down()) == BlockUtils.BlockResistance.Resistant)) {
                if (progress >= 0.0) {
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, Direction.UP));
                }
                if (progress >= 1.0) {
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
                    progress = 0;
                }
            } else if (BlockUtils.getBlockResistance(pos) == BlockUtils.BlockResistance.Unbreakable) {
                this.getHudInfo();
            }
        }
    }

    @Override
    public String getHudInfo() {
        return "Not";
    }

    public enum AutoSwitch {
        None,
        Continously,
        OnBreak
    }

}
