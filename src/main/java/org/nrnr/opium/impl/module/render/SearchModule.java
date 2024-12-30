package org.nrnr.opium.impl.module.render;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.keyboard.KeyboardInputEvent;
import org.nrnr.opium.init.Modules;
import org.nrnr.opium.util.KeyboardUtil;
import org.nrnr.opium.util.chat.ChatUtil;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author chronos
 * @since 1.0
 */
public class SearchModule extends ToggleModule {

    public SearchModule() {
        super("Search", "Search Module", ModuleCategory.CLIENT);
    }

    public ArrayList<String> matchingModuleNames = new ArrayList<>();
    public String inputText = "";



    @Override
    public void onDisable() {
        inputText = "";
    }


    @EventListener
    public void onKeyboardInput(KeyboardInputEvent event) {
        if (Modules.CLICK_GUI.isEnabled()) {
            if (mc.player == null || mc.world == null || mc.currentScreen != null || this.isEnabled()) {
                event.cancel();
            }

            String character = "";

            if (event.getKeycode() == GLFW_KEY_BACKSPACE) {
                if (!inputText.isEmpty()) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                }
            }
            else if (event.getKeycode() == GLFW_KEY_ESCAPE) {
                this.disable();
            }
            else if (event.getKeycode() >= GLFW_KEY_SPACE && event.getKeycode() <= GLFW_KEY_Z) {
                character = getKeyName(event.getKeycode());

                if (event.getKeycode() == GLFW_KEY_SPACE) {
                    character = " ";
                }

                if (character != null && !character.isEmpty()) {
                    inputText += character;
                    ChatUtil.clientSendMessageRaw(inputText);
                }
            }
        }
    }


    public String getKeyName(int keycode) {
        if (keycode != GLFW_KEY_UNKNOWN) {
            final String name = KeyboardUtil.getKeyName(keycode);
            return name != null ? name.toUpperCase() : "";
        }
        return "";
    }

}
