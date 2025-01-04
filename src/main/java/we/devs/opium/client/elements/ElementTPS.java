package we.devs.opium.client.elements;

import net.minecraft.client.util.math.MatrixStack;
import we.devs.opium.api.manager.element.Element;
import we.devs.opium.api.manager.element.RegisterElement;
import we.devs.opium.api.utilities.RenderUtils;
import we.devs.opium.client.events.EventRender2D;
import we.devs.opium.client.modules.client.ModuleColor;

@RegisterElement(name = "TPS", tag = "TPS", description = "Shows your tps")
public class ElementTPS extends Element {
    @Override
    public void onRender2D(EventRender2D event) {
        super.onRender2D(event);
        this.frame.setWidth(mc.textRenderer.getWidth(this.getText()));
        this.frame.setHeight(mc.textRenderer.fontHeight);
        RenderUtils.drawString(new MatrixStack(),this.getText(), (int) this.frame.getX(), (int) this.frame.getY(), ModuleColor.getColor().getRGB());
    }

    String getText() {
        return "TPS: " + mc.getCurrentFps();
    }
}
