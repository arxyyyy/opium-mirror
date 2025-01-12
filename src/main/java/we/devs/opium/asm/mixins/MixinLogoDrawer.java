package we.devs.opium.asm.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer {

    @Shadow
    @Final
    private boolean ignoreAlpha;
    private final List<Snowflake> snowflakes = new ArrayList<>();
    private static final int INITIAL_SNOWFLAKE_COUNT = 100;

    /**
     * @author Cxiy
     * @reason Drawing Logo with Snow Effect
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        if (snowflakes.isEmpty()) {
            initializeSnowflakes(screenWidth, screenHeight);
        }

        resizeSnowflakesIfNecessary(screenWidth, screenHeight);

        context.setShaderColor(1.0F, 1.0F, 1.0F, this.ignoreAlpha ? 1.0F : alpha);
        RenderSystem.enableBlend();
        int i = screenWidth / 2 - 253;
        context.drawTexture(Identifier.of("opium", "icons/title.png"), i, y, 0.0F, 0.0F, 506, 75, 506, 75);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        for (Snowflake snowflake : snowflakes) {
            snowflake.update(screenWidth, screenHeight);
            snowflake.draw(context);
        }
    }

    private void initializeSnowflakes(int screenWidth, int screenHeight) {
        for (int i = 0; i < INITIAL_SNOWFLAKE_COUNT; i++) {
            snowflakes.add(new Snowflake(screenWidth, screenHeight));
        }
    }

    private void resizeSnowflakesIfNecessary(int screenWidth, int screenHeight) {
        int currentSnowflakeCount = screenWidth / 10;
        if (snowflakes.size() < currentSnowflakeCount) {
            for (int i = snowflakes.size(); i < currentSnowflakeCount; i++) {
                snowflakes.add(new Snowflake(screenWidth, screenHeight));
            }
        } else if (snowflakes.size() > currentSnowflakeCount) {
            snowflakes.subList(currentSnowflakeCount, snowflakes.size()).clear();
        }
    }

    private static class Snowflake {
        private float x, y;
        private float speedY;
        private int size;
        private final Random random = new Random();

        public Snowflake(int screenWidth, int screenHeight) {
            reset(screenWidth, screenHeight);
        }

        public void update(int screenWidth, int screenHeight) {
            y += speedY;
            if (y > screenHeight) {
                reset(screenWidth, screenHeight);
            }
        }

        public void draw(DrawContext context) {
            context.fill((int) x, (int) y, (int) x + size, (int) y + size, 0x80FFFFFF);
        }

        private void reset(int screenWidth, int screenHeight) {
            x = random.nextInt(screenWidth);
            y = -random.nextInt(50);
            speedY = 0.5F + random.nextFloat();
            size = random.nextInt(3) + 2;
        }
    }
}