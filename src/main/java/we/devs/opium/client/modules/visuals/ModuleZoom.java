package we.devs.opium.client.modules.visuals;

import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.client.values.impl.ValueNumber;

@RegisterModule(name = "Zoom", description = "Zooms lol.", category = Module.Category.VISUALS)
public class ModuleFOV extends Module {
    private final ValueNumber fovSetting = new ValueNumber("Amount", "Amount", "", 30, 10, 130);


    private int oldFov;

    @Override
    public void onEnable() {
        if(mc.options == null) return;
        oldFov = mc.options.getFov().getValue();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        mc.options.getFov().setValue(fovSetting.getValue().intValue());
        System.out.println("Current FOV: " + mc.options.getFov().getValue());
    }
    public void onDisable() {
        mc.options.getFov().setValue(oldFov);
    }
}
