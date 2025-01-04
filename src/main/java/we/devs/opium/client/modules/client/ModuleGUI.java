package we.devs.opium.client.modules.client;

import we.devs.opium.Opium;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.client.values.impl.ValueBoolean;
import we.devs.opium.client.values.impl.ValueColor;
import we.devs.opium.client.values.impl.ValueNumber;

import java.awt.*;

@RegisterModule(name="GUI", description="The client's GUI interface for interacting with modules and settings.", category=Module.Category.CLIENT, bind=54)
public class ModuleGUI extends Module {
    public static ModuleGUI INSTANCE;
    public final ValueColor categoryColor = new ValueColor("CategoryColor", "Category Color", "Color of the category panes.", new Color(29, 29, 29,255));
    public ValueBoolean roundedCorners = new ValueBoolean("RoundedCorners", "Rounded Corners", "Make the category panes rounded.", true);
    public ValueNumber cornerRadius = new ValueNumber("cornerRadius", "Corner Radius", "The radius of the category pane corners", 4, 1, 20);
    public ValueNumber scrollSpeed = new ValueNumber("ScrollSpeed", "Scroll Speed", "The speed for scrolling through the GUI.", 10, 1, 50);
    public ValueBoolean rectEnabled = new ValueBoolean("RectEnabled", "Rect Enabled", "Render a rectangle behind enabled modules.", true);
    public ValueBoolean fadeText = new ValueBoolean("FadeText", "Fade Text", "Add cool animation to the text of the GUI.", false);
    public ValueNumber fadeOffset = new ValueNumber("FadeOffset", "Fade Offset", "Offset for the text animation of the GUI.", 100, 0, 255);

    public ModuleGUI() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null || Opium.CLICK_GUI == null) {
            this.disable(false);
            return;
        }
        mc.setScreen(Opium.CLICK_GUI);
    }
}
