package org.nrnr.opium.impl.module.client;

import org.lwjgl.glfw.GLFW;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ConcurrentModule;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.impl.event.network.GameJoinEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.init.Modules;
import org.nrnr.opium.util.KeyboardUtil;
import org.nrnr.opium.util.chat.ChatUtil;

/**
 * @author chronos
 * @since 1.0
 */
public class RotationsModule extends ConcurrentModule {
    //
    Config<Float> preserveTicksConfig = new NumberConfig<>("Limit Rotations", "Time to preserve rotations after reaching the target rotations", 0.0f, 10.0f, 20.0f);
    Config<Boolean> movementFixConfig = new BooleanConfig("Strict Direction(beta)", "Fixes movement on Grim when rotating", false);
    //
    private float prevYaw;

    /**
     *
     */
    public RotationsModule() {
        super("Rotations", "Manages client rotations",
                ModuleCategory.CLIENT);
    }

    @EventListener
    public void onGameJoin(GameJoinEvent event) {
        ChatUtil.clientSendMessageRaw("Welcome to 0piumh4ck.cc!");
        ChatUtil.clientSendMessageRaw("The current ClickGUI bind is " + getKeyName(Modules.CLICK_GUI.getKeybinding().getKeycode()));
        ChatUtil.clientSendMessageRaw("The current Prefix is " + Managers.COMMAND.getPrefix());
    }

    public String getKeyName(int keycode) {
        if (keycode != GLFW.GLFW_KEY_UNKNOWN) {
            final String name = KeyboardUtil.getKeyName(keycode);
            return name != null ? name.toUpperCase() : "NONE";
        }
        return "NONE";
    }

    public boolean getMovementFix() {
        return movementFixConfig.getValue();
    }

    /**
     * @return
     */
    public float getPreserveTicks() {
        return preserveTicksConfig.getValue();
    }
}
