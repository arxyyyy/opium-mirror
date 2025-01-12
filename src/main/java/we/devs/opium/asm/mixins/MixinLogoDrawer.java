package we.devs.opium.asm.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer {

    @Shadow @Final private boolean ignoreAlpha;

    /**
     * @author Cxiy
     * @reason Drawing Logo
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.ignoreAlpha ? 1.0F : alpha);
        RenderSystem.enableBlend();
        int i = screenWidth / 2 - 253;
        context.drawTexture(Identifier.of("opium", "icons/title.png"), i, y, 0.0F, 0.0F, 506, 75, 506, 75);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

}
