package me.opium.features.modules.client;

import me.opium.Opium;
import me.opium.event.impl.Render2DEvent;
import me.opium.features.modules.Module;
import me.opium.features.settings.Setting;
import me.opium.util.traits.Util;

public class Watermarks extends Module {
    public static Watermarks INSTANCE;

    public enum Mode {
        Opium, CC, OpiumClient
    }

    public Setting<Mode> mode = this.register(new Setting<>("Notify", Mode.Opium));

    public Watermarks() {
        super("Watermarks", "More options for Watermarks", Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        switch (INSTANCE.mode.getValue()) {
            case Opium -> {
                event.getContext().drawTextWithShadow(
                        Util.mc.textRenderer,
                        Opium.NAME + Opium.VERSION,
                        1, 1,
                        -1
                );
            }
            case CC -> {
                event.getContext().drawTextWithShadow(
                        Util.mc.textRenderer,
                        "0piumh4ck.cc" + Opium.VERSION,
                        1, 1,
                        -1
                );
            }
            case OpiumClient -> {
                event.getContext().drawTextWithShadow(
                        Util.mc.textRenderer,
                        "Opium Client" + Opium.VERSION,
                        1, 1,
                        -1
                );
            }
            }
        }
    }
