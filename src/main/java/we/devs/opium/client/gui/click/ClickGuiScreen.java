package we.devs.opium.client.gui.click;

import we.devs.opium.Opium;
import we.devs.opium.api.manager.event.EventListener;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.utilities.RenderUtils;
import we.devs.opium.client.gui.click.components.ModuleComponent;
import we.devs.opium.client.gui.click.manage.Component;
import we.devs.opium.client.gui.click.manage.Frame;
import we.devs.opium.client.modules.client.ModuleColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;

public class ClickGuiScreen extends Screen implements EventListener {
    private final ArrayList<Frame> frames = new ArrayList<>();

    public ClickGuiScreen() {
        super(Text.literal("Click GUI"));
        Opium.EVENT_MANAGER.register(this);
        int offset = 30;
        for (Module.Category category : Module.Category.values()) {
            this.frames.add(new Frame(category, offset, 20));
            offset += 110;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        for (Frame frame : this.frames) {
            frame.render(context, mouseX, mouseY, delta);
        }
        for (Frame frame : this.frames) {
            for (Component c : frame.getComponents()) {
                if (c instanceof ModuleComponent component) {
                    if (component.isHovering(mouseX, mouseY) && frame.isOpen() && !component.getModule().getDescription().isEmpty()) {
                        RenderUtils.drawRect(context.getMatrices(), mouseX + 5, mouseY - 2,
                                mouseX + MinecraftClient.getInstance().textRenderer.getWidth(component.getModule().getDescription()) + 7.0f,
                                mouseY + 11, new Color(40, 40, 40));
                        RenderUtils.drawOutline(context.getMatrices(), mouseX + 5, mouseY - 2,
                                mouseX + MinecraftClient.getInstance().textRenderer.getWidth(component.getModule().getDescription()) + 7.0f,
                                mouseY + 11, 1.0f, ModuleColor.getColor());

                        RenderUtils.drawString(context.getMatrices(),
                                component.getModule().getDescription(), mouseX + 7, mouseY, -1);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Frame frame : this.frames) {
            frame.mouseClicked((int) mouseX, (int) mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        for (Frame frame : this.frames) {
            frame.mouseReleased((int) mouseX, (int) mouseY, state);
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        for (Frame frame : this.frames) {
            frame.charTyped(typedChar, keyCode);
        }
        return super.charTyped(typedChar, keyCode);
    }

    public Color getColor() {
        return new Color(ModuleColor.getColor().getRed(), ModuleColor.getColor().getGreen(), ModuleColor.getColor().getBlue(), 160);
    }
}
