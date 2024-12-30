package org.nrnr.opium.api.module;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.MacroConfig;
import org.nrnr.opium.api.config.setting.ToggleConfig;
import org.nrnr.opium.api.macro.Macro;
import org.nrnr.opium.api.Hideable;
import org.nrnr.opium.init.Modules;
import org.nrnr.opium.util.chat.ChatUtil;
import org.nrnr.opium.util.render.animation.Animation;
import org.nrnr.opium.util.render.animation.Easing;
import org.lwjgl.glfw.GLFW;


public class ToggleModule extends Module implements Hideable {
    //
    private final Animation animation = new Animation(false, 300, Easing.CUBIC_IN_OUT);
    // Config representing the module enabled state. Cannot interact with
    // this configuration unless using #toggle() #enable() or #disable().
    Config<Boolean> enabledConfig = new ToggleConfig("Enabled", "The module" +
            " enabled state. This state is true when the module is running.", false);
    // Config for keybinding implementation. Module keybind is used to
    // interact with the #enabledConfig.
    Config<Macro> keybindingConfig = new MacroConfig("Keybind", "The module " +
            "keybinding. Pressing this key will toggle the module enabled " +
            "state. Press [BACKSPACE] to delete the keybind.",
            new Macro(getId(), GLFW.GLFW_KEY_UNKNOWN, () -> toggle()));
    // Arraylist rendering info
    Config<Boolean> hiddenConfig = new BooleanConfig("Hidden", "The hidden " +
            "state of the module in the Arraylist", false);

    public final Identifier ENABLE_SOUND = new Identifier("neverdies:enable");
    public final Identifier DISABLE_SOUND = new Identifier("neverdies:disable");
    //
    public SoundEvent ENABLE_SOUNDEVENT = SoundEvent.of(ENABLE_SOUND);
    public SoundEvent DISABLE_SOUNDEVENT = SoundEvent.of(DISABLE_SOUND);

    /**
     * @param name     The module unique identifier
     * @param desc     The module description
     * @param category The module category
     */
    public ToggleModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
        // Toggle settings
        register(keybindingConfig, enabledConfig, hiddenConfig);
    }


    public ToggleModule(String name, String desc, ModuleCategory category,
                        Integer keycode) {
        this(name, desc, category);
        keybind(keycode);
    }

    /**
     * @return
     */
    @Override
    public boolean isHidden() {
        return hiddenConfig.getValue();
    }

    /**
     * @param hidden
     */
    @Override
    public void setHidden(boolean hidden) {
        hiddenConfig.setValue(hidden);
    }

    /**
     * Toggles the module {@link #enabledConfig} state (i.e. If the module is
     * <tt>enabled</tt>, the module enabled state will now be <tt>disabled</tt>
     * and vice versa).
     *
     * @see #enable()
     * @see #disable()
     */
    public void toggle() {
        if (isEnabled()) {
            disable();
            if (Modules.CHAT_NOTIFIER.isEnabled()) {
                ChatUtil.clientSendMessage("" + this.getName() + Formatting.DARK_RED + " [-]");
            }
        } else {
            enable();
            if (Modules.CHAT_NOTIFIER.isEnabled()) {
                ChatUtil.clientSendMessage("" + this.getName() + Formatting.DARK_GREEN + " [+]");
            }
        }
    }

    /**
     * Sets the module {@link #enabledConfig} state to <tt>true</tt>. Runs
     * the {@link #onEnable()} callback.
     *
     * @see #onEnable()
     * @see ToggleConfig#setValue(Boolean)
     */
    public void enable() {
        enabledConfig.setValue(true);
        onEnable();
        if (Modules.SOUNDS.isEnabled()) {
            Modules.SOUNDS.playEnableSound();
        }
    }

    /**
     * Sets the module {@link #enabledConfig} state to <tt>false</tt>. Runs
     * the {@link #onDisable()} callback.
     *
     * @see #onDisable()
     * @see ToggleConfig#setValue(Boolean)
     */
    public void disable() {
        enabledConfig.setValue(false);
        onDisable();
        if (Modules.SOUNDS.isEnabled()) {
            Modules.SOUNDS.playDisableSound();
        }
    }

    /**
     * Runs callback after {@link #enable()}. Part of the module
     * implementation specifications.
     *
     * @see #enable()
     */
    protected void onEnable() {
    }


    /**
     * Runs callback after {@link #disable()}. Part of the module
     * implementation specifications.
     *
     * @see #disable()
     */
    protected void onDisable() {

    }

    /**
     * Sets the module keybinding to the param {@link GLFW} keycode. The
     * config {@link Macro#runMacro()} will invoke {@link #toggle()} when
     * keybind is pressed.
     *
     * @param keycode The keybind
     * @see Macro
     * @see #keybindingConfig
     */
    public void keybind(int keycode) {
        keybindingConfig.setContainer(this);
        ((MacroConfig) keybindingConfig).setValue(keycode);
    }

    /**
     * Returns <tt>true</tt> if the module is currently enabled and running.
     * Wrapper method for {@link ToggleConfig#getValue()}.
     *
     * @return <tt>true</tt> if the module is enabled
     * @see #enabledConfig
     */
    public boolean isEnabled() {
        return enabledConfig.getValue();
    }

    /**
     * @return
     */
    public Macro getKeybinding() {
        return keybindingConfig.getValue();
    }

    /**
     * @return
     */
    public Animation getAnimation() {
        return animation;
    }
}
