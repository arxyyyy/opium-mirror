package we.devs.opium.client.modules.visuals;

import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.asm.ducks.ISimpleOption;

@RegisterModule(name="FullBright", description="more mcswag.", category=Module.Category.VISUALS)
public class ModuleFullBright extends Module {

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        //noinspection unchecked
        ISimpleOption<Double> gammaOption = (ISimpleOption<Double>) (Object) mc.options.getGamma();
        gammaOption.opium$setValue(1000.0);
    }
}
