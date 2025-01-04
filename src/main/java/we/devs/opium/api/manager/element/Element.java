package we.devs.opium.api.manager.element;

import we.devs.opium.api.manager.module.Module;
import we.devs.opium.client.gui.hud.ElementFrame;
import we.devs.opium.client.values.Value;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public abstract class Element extends Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private final ArrayList<Value> values;
    public ElementFrame frame;

    public Element() {
        RegisterElement annotation = this.getClass().getAnnotation(RegisterElement.class);
        this.name = annotation.name();
        this.tag.setValue(annotation.tag().equals("4GquuoBHl7gkSDaNeMb5") ? annotation.name() : annotation.tag());
        this.description = annotation.description();
        this.values = new ArrayList<>();
    }

    public void setFrame(ElementFrame frame) {
        this.frame = frame;
    }

    @Override
    public ArrayList<Value> getValues() {
        return this.values;
    }
}