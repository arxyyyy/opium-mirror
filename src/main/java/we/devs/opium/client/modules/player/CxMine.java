package we.devs.opium.client.modules.player;

import net.minecraft.util.math.BlockPos;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.client.values.impl.ValueNumber;

@RegisterModule(name = "CxMine", tag = "CxMine", description = "cev any block", category = Module.Category.PLAYER)
public class CxMine extends Module
{
    private final ValueNumber delay = new ValueNumber("Delay", "Delay", "", 50, 1, 500);

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null || mc.world == null) {
            this.disable(false);
            return;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}