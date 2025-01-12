package we.devs.opium.client.modules.visuals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.FastHoleUtil;
import we.devs.opium.api.utilities.Renderer3d;
import we.devs.opium.client.events.EventRender3D;
import we.devs.opium.client.events.EventTick;

import java.awt.*;

/**
 * todo color settings, height settings, fix double hole detection, range settings
 */
@RegisterModule(name = "HoleEsp", description = "HoleEsp", tag = "Hole ESP", category = Module.Category.VISUALS)
public class ModuleHoleESP extends Module {

    @Override
    public void onTick(EventTick event) {
        FastHoleUtil.INSTANCE.onTick(event);
    }

    @Override
    public void onRender3D(EventRender3D event) {
        for (FastHoleUtil.Hole hole : FastHoleUtil.holes) {
            if(hole.safety() == FastHoleUtil.HoleSafety.UNSAFE) continue;
            Color color = switch (hole.safety()) {
                case UNBREAKABLE -> Color.GREEN;
                case PARTIALLY_UNBREAKABLE -> Color.YELLOW;
                case BREAKABLE -> Color.RED;
                default -> throw new IllegalStateException("Unexpected value: " + hole.safety());
            };

            for (BlockPos blockPos : hole.air()) {
                Renderer3d.renderEdged(event.getMatrices(), injectAlpha(color), injectAlpha(color.darker()), Vec3d.of(blockPos), new Vec3d(1, 0.05, 1));
            }
        }
    }

    Color injectAlpha(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 40);
    }
}
