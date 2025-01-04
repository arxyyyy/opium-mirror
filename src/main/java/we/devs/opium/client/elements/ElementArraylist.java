package we.devs.opium.client.elements;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import we.devs.opium.Opium;
import we.devs.opium.api.manager.element.Element;
import we.devs.opium.api.manager.element.RegisterElement;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.utilities.ColorUtils;
import we.devs.opium.api.utilities.RenderUtils;
import we.devs.opium.client.events.EventRender2D;
import we.devs.opium.client.modules.client.ModuleColor;
import we.devs.opium.client.modules.client.ModuleHUD;
import we.devs.opium.client.values.impl.ValueEnum;

import java.awt.image.renderable.RenderContext;
import java.util.ArrayList;
import java.util.Comparator;

@RegisterElement(name = "Arraylist", tag = "Arraylist", description = "Shows all enabled modules")
public class ElementArraylist extends Element {

    ArrayList<Module> modules = new ArrayList<>();
    ValueEnum ordering = new ValueEnum("Ordering", "Ordering", "", orderings.Length);
    ValueEnum rendering = new ValueEnum("Rendering", "Rendering", "", renderings.Up);
    ValueEnum modulesColor = new ValueEnum("ModulesColor", "Modules Color", "Color mode for array list.", modulesColors.Normal);

    Formatting reset = Formatting.RESET;
    Formatting gray = Formatting.GRAY;

    @Override
    public void onRender2D(EventRender2D event) {
        DrawContext context = event.getContext();
        float sWidth = mc.getWindow().getScaledWidth();
        float sHeight = mc.getWindow().getScaledHeight();
        modules.clear();
        for (we.devs.opium.api.manager.module.Module module : Opium.MODULE_MANAGER.getModules()) {
            if (module.isToggled() && module.isDrawn()) {
                modules.add(module);
            }
        }
        if (!modules.isEmpty()) {
            int addY = 0;
            if (this.ordering.getValue().equals(orderings.Length)) {
                for (we.devs.opium.api.manager.module.Module m : modules.stream().sorted(Comparator.comparing(s -> mc.textRenderer.getWidth(s.getTag() + (s.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + s.getHudInfo() + this.gray + "]")) * -1.0f)).toList()) {
                    String string = m.getTag() + (m.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + m.getHudInfo() + this.gray + "]");
                    float x = sWidth - 2.0f - mc.textRenderer.getWidth(string);
                    float y = this.rendering.getValue().equals(renderings.Up) ? (float) (2 + addY * 10 + (ModuleHUD.INSTANCE.effectHud.getValue().equals(ModuleHUD.effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (addY * 10);
                    context.drawTextWithShadow(mc.textRenderer, string, (int) x, (int) y, this.modulesColor.getValue().equals(modulesColors.Normal) ? ModuleColor.getColor().getRGB() : (this.modulesColor.getValue().equals(modulesColors.Random) ? m.getRandomColor().getRGB() : ColorUtils.rainbow(addY).getRGB()));
                    ++addY;
                }
            } else {
                for (we.devs.opium.api.manager.module.Module m : modules.stream().sorted(Comparator.comparing(Module::getName)).toList()) {
                    String string = m.getTag() + (m.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + m.getHudInfo() + this.gray + "]");
                    float x = sWidth - 2.0f - mc.textRenderer.getWidth(string);
                    float y = this.rendering.getValue().equals(renderings.Up) ? (float) (2 + addY * 10 + (ModuleHUD.INSTANCE.effectHud.getValue().equals(ModuleHUD.effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (addY * 10);
                    context.drawTextWithShadow(mc.textRenderer, string, (int) x, (int) y, (Integer) (this.modulesColor.getValue().equals(modulesColors.Normal) ? ModuleColor.getColor() : (this.modulesColor.getValue().equals(modulesColors.Random) ? m.getRandomColor() : ColorUtils.rainbow(addY).getRGB())));
                    ++addY;
                }
            }
        }
    }

    public enum orderings {
        Length,
        ABC
    }

    public enum renderings {
        Up,
        Down
    }

    public enum modulesColors {
        Normal,
        Random,
        Rainbow
    }
}
