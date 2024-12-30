package org.nrnr.opium.impl.module.client;

import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.gui.click.ClickGuiScreen;
import org.nrnr.opium.init.Modules;
import org.nrnr.opium.util.render.animation.Animation;
import org.nrnr.opium.util.render.animation.Easing;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * @author chronos
 * @see ClickGuiScreen
 * @since 1.0
 */
public class ClickGuiModule extends ToggleModule {

    //    Config<Integer> hueConfig = new NumberConfig<>("Hue", "The saturation of colors", 0, 0, 360);
//    Config<Integer> saturationConfig = new NumberConfig<>("Saturation", "The saturation of colors", 0, 50, 100);
//    Config<Integer> brightnessConfig = new NumberConfig<>("Brightness", "The brightness of colors", 0, 50, 100);
//    Config<Integer> hue1Config = new NumberConfig<>("Hue1", "The saturation of colors", 0, 0, 360);
//    Config<Integer> saturation1Config = new NumberConfig<>("Saturation1", "The saturation of colors", 0, 50, 100);
//    Config<Integer> brightness1Config = new NumberConfig<>("Brightness1", "The brightness of colors", 0, 50, 100);
//    Config<Integer> alphaConfig = new NumberConfig<>("Alpha", "The alpha of colors", 0, 100, 100);
    //
    public static ClickGuiScreen CLICK_GUI_SCREEN;
    private final Animation openCloseAnimation = new Animation(false, 300, Easing.QUINT_IN_OUT);
    public final BooleanConfig renderDescription = new BooleanConfig("Description", "Renders a Description of Each Module in Clickgui", true);

    // TODO: Fix Gui scaling
    public float scaleConfig = 1.0f;
    public String inputText;
    /**
     *
     */
    public ClickGuiModule() {
        super("ClickGui", "Opens the clickgui screen", ModuleCategory.CLIENT,
                GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        // initialize the null gui screen instance
        if (CLICK_GUI_SCREEN == null) {
            CLICK_GUI_SCREEN = new ClickGuiScreen(this);
        }
        mc.setScreen(CLICK_GUI_SCREEN);
        openCloseAnimation.setState(true);
    }
/*
    @EventListener
    public void onTick(TickEvent event){
        String inputText = Modules.CLICK_GUI.inputText;
        ArrayList<String> modulesArray = Modules.CLICK_GUI.matchingModuleNames;
        for (Module module: Managers.MODULE.getModules()){
        }

    }

    @EventListener
    public void onKeyboardInput(KeyboardInputEvent event) {
        if (mc.player == null || mc.world == null
                || mc.currentScreen != null || this.isEnabled()) {
            event.cancel();
        }
        if (event.getKeycode() == GLFW_KEY_BACKSPACE) {
            if (!inputText.isEmpty()) {
                inputText = inputText.substring(0, inputText.length() - 1);
            }
        }
        else if (event.getKeycode() == GLFW_KEY_ESCAPE){
            this.disable();
        }
        else if (event.getKeycode() >= GLFW_KEY_SPACE && event.getKeycode() <= GLFW_KEY_Z) {
            String character = getKeyName(event.getKeycode());

            if (event.getKeycode() == GLFW_KEY_SPACE){
                character = " ";
            }
            if (!character.isEmpty() && !inputText.endsWith(character) && !(inputText == null)) {
                inputText += character;
                ChatUtil.clientSendMessage(inputText);
            }
            
        }


        StringBuilder modulesList = new StringBuilder();
        for (Module module : Managers.MODULE.getModules()) {
            if (!(inputText == null)){
                if (module.getName().startsWith(inputText)) {
                    matchingModuleNames.add(module.getName());
                }
            }
        }

    }
    public String getKeyName(int keycode) {
        if (keycode != GLFW.GLFW_KEY_UNKNOWN) {
            final String name = KeyboardUtil.getKeyName(keycode);
            return name != null ? name.toUpperCase() : "";
        }
        return "";
    }*/


    public ArrayList<String> matchingModuleNames = new ArrayList<>();

    public ArrayList<String> getArray(){
        return matchingModuleNames;
    }

    public String getInputText(){
        return inputText;
    }


    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        mc.player.closeScreen();
        openCloseAnimation.setState(false);
    }

    public boolean getDescriptionConfig(){
        return Modules.CLICK_GUI.renderDescription.getValue();
    }

    public int getColor() {
        return Modules.COLORS.getColor((int) (100 * openCloseAnimation.getFactor())).getRGB();
        // return ColorUtil.hslToColor(hueConfig.getValue(), saturationConfig.getValue(), brightnessConfig.getValue(), alphaConfig.getValue() / 100.0f).getRGB();
    }

    public int getColor1() {
        return Modules.COLORS.getColor((int) (100 * openCloseAnimation.getFactor())).getRGB();
        // return ColorUtil.hslToColor(hue1Config.getValue(), saturation1Config.getValue(), brightness1Config.getValue(), alphaConfig.getValue() / 100.0f).getRGB();
    }

    public int getColor(float alpha) {
        return Modules.COLORS.getColor((int) (100 * alpha * openCloseAnimation.getFactor())).getRGB();
        // return ColorUtil.hslToColor(hueConfig.getValue(), saturationConfig.getValue(), brightnessConfig.getValue(), MathHelper.clamp(alphaConfig.getValue() * alpha / 100.0f, 0.0f, 1.0f)).getRGB();
    }

    public int getColor1(float alpha) {
        return Modules.COLORS.getColor((int) (100 * alpha * openCloseAnimation.getFactor())).getRGB();
        // return ColorUtil.hslToColor(hue1Config.getValue(), saturation1Config.getValue(), brightness1Config.getValue(), MathHelper.clamp(alphaConfig.getValue() * alpha / 100.0f, 0.0f, 1.0f)).getRGB();
    }

    /**
     * @return
     */
    public Float getScale() {
        return scaleConfig;
    }
}
