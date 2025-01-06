package we.devs.opium.client.modules.visuals;

import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.client.values.impl.ValueNumber;

@RegisterModule(name = "CustomFOV", description = "Customize your field of range.", category = Module.Category.VISUALS)
public class ModuleCustomFOV extends Module {
    private final ValueNumber fovSetting = new ValueNumber("Amount", "Amount", "", 110.0f, 30.0f, 200.0f);
}
