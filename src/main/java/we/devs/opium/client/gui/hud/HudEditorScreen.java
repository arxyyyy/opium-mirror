package we.devs.opium.client.gui.hud;

import we.devs.opium.Opium;
import we.devs.opium.api.manager.element.Element;
import we.devs.opium.client.gui.click.manage.Frame;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class HudEditorScreen extends Screen {
    private final ArrayList<ElementFrame> elementFrames = new ArrayList<>();
    private final Frame frame = new Frame(20, 20);
    private ElementFrame draggingElement = null;

    public HudEditorScreen() {
        super(Text.literal(""));
        for (Element element : Opium.ELEMENT_MANAGER.getElements()) {
            this.addElement(element);
            element.setFrame(this.getFrame(element));
        }
    }

    public void addElement(Element element) {
        this.elementFrames.add(new ElementFrame(element, 10.0f, 10.0f, 80.0f, 15.0f, this));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);
        this.frame.render(context, mouseX, mouseY, partialTicks);
        for (ElementFrame frame : this.elementFrames) {
            frame.render(context, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.frame.mouseClicked((int) mouseX, (int) mouseY, button);

        if (draggingElement == null) { // Nur ein Element darf gezogen werden
            for (ElementFrame frame : this.elementFrames) {
                frame.mouseClicked((int) mouseX, (int) mouseY, button);
                if (frame.isDragging()) {
                    draggingElement = frame; // Setze das aktive Dragging-Element
                    break;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        this.frame.mouseReleased((int) mouseX, (int) mouseY, state);

        if (draggingElement != null) {
            draggingElement.mouseReleased((int) mouseX, (int) mouseY, state);
            draggingElement = null; // Zurücksetzen des Dragging-Elements
        }

        for (ElementFrame frame : this.elementFrames) {
            if (frame != draggingElement) {
                frame.mouseReleased((int) mouseX, (int) mouseY, state);
            }
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }


    public Frame getFrame() {
        return this.frame;
    }

    public ElementFrame getFrame(Element element) {
        for (ElementFrame frame : this.elementFrames) {
            if (!frame.getElement().equals(element)) continue;
            return frame;
        }
        return null;
    }
}
