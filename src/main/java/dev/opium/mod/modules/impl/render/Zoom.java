package dev.opium.mod.modules.impl.render;

import dev.opium.Opium;
import dev.opium.api.events.eventbus.EventHandler;
import dev.opium.api.events.impl.Render3DEvent;
import dev.opium.api.utils.math.Animation;
import dev.opium.api.utils.math.Easing;
import dev.opium.mod.modules.Module;
import dev.opium.mod.modules.settings.impl.EnumSetting;
import dev.opium.mod.modules.settings.impl.SliderSetting;

public class Zoom extends Module {
    public static Zoom INSTANCE;
    public double currentFov;
    private final SliderSetting animTime = add(new SliderSetting("AnimTime", 300, 0, 1000));
    public final EnumSetting<Easing> ease = add(new EnumSetting<>("Ease", Easing.CubicInOut));
    final SliderSetting fov = add(new SliderSetting("ZoomFov", 60, 0, 130, 1));
    public Zoom() {
        super("Zoom", Category.Render);
        setChinese("放大");
        INSTANCE = this;
        Opium.EVENT_BUS.subscribe(new ZoomAnim());
    }

    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
        }
    }
    Animation animation = new Animation();

    public static boolean on = false;
    public class ZoomAnim {
        @EventHandler
        public void onRender3D(Render3DEvent event) {
            if (isOn()) {
                currentFov = animation.get(fov.getValue(), animTime.getValueInt(), ease.getValue());
                on = true;
            } else if (on) {
                currentFov = animation.get(0, animTime.getValueInt(), ease.getValue());
                if ((int) currentFov == 0) {
                    on = false;
                }
            }
        }
    }
}
