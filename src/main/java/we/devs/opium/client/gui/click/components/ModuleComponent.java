package we.devs.opium.client.gui.click.components;

import me.x150.renderer.render.Renderer2d;
import org.lwjgl.glfw.GLFW;
import we.devs.opium.Opium;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.utilities.ColorUtils;
import we.devs.opium.api.utilities.RenderUtils;
import we.devs.opium.api.utilities.font.FontRenderers;
import we.devs.opium.client.gui.click.manage.Component;
import we.devs.opium.client.gui.click.manage.Frame;
import we.devs.opium.client.modules.client.ModuleColor;
import we.devs.opium.client.modules.client.ModuleFont;
import we.devs.opium.client.modules.client.ModuleGUI;
import we.devs.opium.client.modules.client.ModuleOutline;
import we.devs.opium.client.values.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import we.devs.opium.client.values.impl.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleComponent extends Component {
    private final ArrayList<Component> components;
    private final Module module;
    private boolean open = false;
    public Map<Integer, Color> colorMap = new HashMap<>();

    public ModuleComponent(Module module, int offset, Frame parent) {
        super(offset, parent);
        this.module = module;
        this.components = new ArrayList<>();
        int valueOffset = offset;
        if (!module.getValues().isEmpty()) {
            for (Value value : module.getValues()) {
                if (value instanceof ValueBoolean) {
                    this.components.add(new BooleanComponent((ValueBoolean)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueNumber) {
                    this.components.add(new NumberComponent((ValueNumber)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueEnum) {
                    this.components.add(new EnumComponent((ValueEnum)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueString) {
                    this.components.add(new StringComponent((ValueString)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueColor) {
                    this.components.add(new ColorComponentTest((ValueColor)value, valueOffset, parent));
                    valueOffset += 14;
                }

                if (value instanceof ValueBind) {
                    this.components.add(new BindComponent((ValueBind)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (!(value instanceof ValueCategory)) continue;
                this.components.add(new CategoryComponent((ValueCategory)value, valueOffset, parent));
                valueOffset += 14;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int height = mc.getWindow().getScaledHeight();
        for (int i = 0; i <= height; ++i) {
            this.colorMap.put(i, ColorUtils.wave(Color.WHITE, ModuleGUI.INSTANCE.fadeOffset.getValue().intValue(), i * 2 + 10));
        }
        if (this.module.isToggled() && ModuleGUI.INSTANCE.rectEnabled.getValue()) {
            int radius = (int) ModuleGUI.INSTANCE.moduleRadius.getValue();
            int samples = 20;
            if (ModuleGUI.INSTANCE.roundedModules.getValue()) {
                Renderer2d.renderRoundedQuad(
                        context.getMatrices(),
                        Opium.CLICK_GUI.getColor(),
                        (float) this.getX() - 0.25f, (float) this.getY() - 0.2f, (float) (this.getX() + this.getWidth() + 0.25f), (float) this.getY() + 0.2f + 14.1f,
                        radius, radius, radius, radius,
                        samples
                );
                if(ModuleOutline.INSTANCE.moduleOutline.getValue()) {
                    Renderer2d.renderRoundedOutline(context.getMatrices(), ModuleOutline.INSTANCE.moduleOutlineColor.getValue(), (float)this.getX() - 0.25f, (float)this.getY() - 0.2f, (float)(this.getX() + this.getWidth() + 0.25f), (float)this.getY() + 0.2f + 14.1f, radius, radius, radius, radius, 0.5f, samples * 4);
                }
            } else {
                RenderUtils.drawRect(context.getMatrices(), (float) this.getX() - 0.25f, (float) this.getY() - 0.2f, (float) (this.getX() + this.getWidth() + 0.25f), (float) this.getY() + 0.2f + 14.1f, Opium.CLICK_GUI.getColor());
            }
            if(ModuleOutline.INSTANCE.moduleOutline.getValue()) {
                Renderer2d.renderRoundedOutline(context.getMatrices(), ModuleOutline.INSTANCE.moduleOutlineColor.getValue(), (float)this.getX() - 0.25f, (float)this.getY() - 0.2f, (float)(this.getX() + this.getWidth() + 0.25f), (float)this.getY() + 0.2f + 14.1f, 0,0,0,0, 0.5f, 20 * 4);
            }
        }
        // Render the module name
        String moduleName = (!this.module.isToggled() ? Formatting.GRAY : "") + this.module.getTag();
        int moduleNameWidth = mc.textRenderer.getWidth(moduleName);
        RenderUtils.drawString(
                context.getMatrices(),
                moduleName,
                this.getX() + 3,
                this.getY() + 3,
                ModuleGUI.INSTANCE.fadeText.getValue()
                        ? this.colorMap.get(MathHelper.clamp(this.getY() + 3, 0, height)).getRGB()
                        : -1
        );
        // Render the bind key to the right of the module name
        if (ModuleGUI.INSTANCE.displayKeybinds.getValue())
            if (this.module.getBind() != 0) {
                String bindKey = (!this.module.isToggled() ? Formatting.GRAY : "") + "[" + GLFW.glfwGetKeyName(this.module.getBind(), 0).toUpperCase() + "]";
                int bindKeyWidth;
                float offSet;
                if (ModuleFont.INSTANCE.customFonts.getValue()) {
                    bindKeyWidth = (int) FontRenderers.fontRenderer.getStringWidth(bindKey);
                    offSet = 0.5f;
                } else {
                    bindKeyWidth = mc.textRenderer.getWidth(bindKey);
                    offSet = 3f;
                }
                bindKeyWidth = mc.textRenderer.getWidth(bindKey);
                RenderUtils.drawString(
                        context.getMatrices(),
                        bindKey,
                        this.getX() + this.getWidth() - bindKeyWidth - offSet, // Align to the right with padding
                        this.getY() + 3,
                        ModuleGUI.INSTANCE.fadeText.getValue()
                                ? this.colorMap.get(MathHelper.clamp(this.getY() + 3, 0, height)).getRGB()
                                : -1
                );
            }
        if (this.isOpen()) {
            for (Component component : this.components) {
                Component c;
                if (!component.isVisible()) continue;
                component.render(context, mouseX, mouseY, delta);
                if (component instanceof BooleanComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((BooleanComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((BooleanComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((BooleanComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    RenderUtils.drawString(context.getMatrices(), ((BooleanComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (component instanceof NumberComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((NumberComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((NumberComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((NumberComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    RenderUtils.drawString(context.getMatrices(), ((NumberComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (component instanceof EnumComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((EnumComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    RenderUtils.drawString(context.getMatrices(), ((EnumComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (component instanceof EnumComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((EnumComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    RenderUtils.drawString(context.getMatrices(), ((EnumComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (!(component instanceof StringComponent)) continue;
                c = component;
                if (!component.isHovering(mouseX, mouseY) || ((StringComponent)c).getValue().getDescription().isEmpty()) continue;
                RenderUtils.drawRect(context.getMatrices(), mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((StringComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                RenderUtils.drawOutline(context.getMatrices(), mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((StringComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                RenderUtils.drawString(context.getMatrices(), ((StringComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.module.toggle(true);
            }
            if (mouseButton == 1) {
                this.setOpen(!this.open);
                this.getParent().refresh();
            }
        }
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void charTyped(char typedChar, int keyCode) {
        super.charTyped(typedChar, keyCode);
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.charTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.isOpen()) {
            for (Component component : this.components) {
                component.onClose();
            }
        }
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }

    public Module getModule() {
        return this.module;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
