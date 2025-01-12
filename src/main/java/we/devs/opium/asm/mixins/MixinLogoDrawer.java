package we.devs.opium.asm.mixins;

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
    private static final Random RANDOM = new Random();

    private long nextLightningTime = 0; // Next lightning event time

    /**
     * @author Cxiy
     * @reason Drawing Logo with Snow Effect and Lightning
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        // Background gradient
        context.fillGradient(0, 0, screenWidth, screenHeight, 0x55000000, 0x33000000);

        if (snowflakes.isEmpty()) {
            initializeSnowflakes(screenWidth, screenHeight);
        } else {
            resizeSnowflakesIfNecessary(screenWidth);
        }

        drawLogo(context, screenWidth, alpha, y);
        renderEffects(context, screenWidth, screenHeight);
    }

    private void initializeSnowflakes(int screenWidth, int screenHeight) {
        for (int i = 0; i < INITIAL_SNOWFLAKE_COUNT; i++) {
            snowflakes.add(new Snowflake(screenWidth, screenHeight));
        }
    }

    private void resizeSnowflakesIfNecessary(int screenWidth) {
        int targetSnowflakeCount = screenWidth / 10;
        if (snowflakes.size() < targetSnowflakeCount) {
            for (int i = snowflakes.size(); i < targetSnowflakeCount; i++) {
                snowflakes.add(new Snowflake(screenWidth, snowflakes.get(0).getScreenHeight()));
            }
        } else if (snowflakes.size() > targetSnowflakeCount) {
            snowflakes.subList(targetSnowflakeCount, snowflakes.size()).clear();
        }
    }

    private void drawLogo(DrawContext context, int screenWidth, float alpha, int y) {
        float pulsationFactor = 1.0f + 0.05f * (float) Math.sin(System.currentTimeMillis() / 300.0);
        int width = 506, height = 75;
        float scaledWidth = width * pulsationFactor;
        float scaledHeight = height * pulsationFactor;
        float centerX = (screenWidth - scaledWidth) / 2.0f;

        context.getMatrices().push();
        context.getMatrices().translate(centerX + scaledWidth / 2.0f, y + scaledHeight / 2.0f, 0.0);
        context.getMatrices().scale(pulsationFactor, pulsationFactor, 1.0f);
        context.getMatrices().translate(-width / 2.0f, -height / 2.0f, 0.0);

        context.setShaderColor(1.0F, 1.0F, 1.0F, ignoreAlpha ? 1.0F : alpha);
        context.drawTexture(
                Identifier.of("opium", "icons/title.png"),
                0, 0, 0, 0, width, height, width, height
        );

        context.getMatrices().pop();
    }

    private void renderEffects(DrawContext context, int screenWidth, int screenHeight) {
        snowflakes.forEach(snowflake -> {
            snowflake.update(screenWidth, screenHeight);
            snowflake.draw(context);
        });
        handleLightning(context, screenWidth, screenHeight);
    }

    private void handleLightning(DrawContext context, int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextLightningTime) {
            nextLightningTime = currentTime + RANDOM.nextInt(5000) + 3000;
            int startX = RANDOM.nextInt(screenWidth);
            drawLightning(context, startX, 0, screenHeight);
        }
    }

    private void drawLightning(DrawContext context, int startX, int startY, int endY) {
        int boltWidth = 2;
        int color = 0xFFFFFFFF;
        int currentX = startX, currentY = startY;

        while (currentY < endY) {
            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(20) - 10, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = Math.min(currentY + RANDOM.nextInt(30), endY);
            context.fill(currentX, currentY, nextX + boltWidth, nextY + boltWidth, color);
            currentX = nextX;
            currentY = nextY;
        }
    }

    private static class Snowflake {
        private float x, y, speedY, rotationAngle, rotationSpeed, opacity;
        private int baseSize, screenHeight;
        private final Random random = new Random();
        private final float glowOffset;

        public Snowflake(int screenWidth, int screenHeight) {
            this.screenHeight = screenHeight;
            this.glowOffset = random.nextFloat() * 1000;
            reset(screenWidth);
        }

        public void update(int screenWidth, int screenHeight) {
            this.screenHeight = screenHeight; // Update screenHeight
            y += speedY;
            rotationAngle += rotationSpeed;

            if (y > screenHeight || opacity <= 0) {
                reset(screenWidth);
            }
        }

        public void draw(DrawContext context) {
            float glowFactor = (float) (0.5 + 0.5 * Math.sin((System.currentTimeMillis() + glowOffset) / 300.0));
            int alpha = (int) (opacity * (0x80 + glowFactor * 0x7F));
            int size = (int) (baseSize + glowFactor * 2);
            int color = (alpha << 24) | 0xFFFFFF;

            drawRotatingSnowflake(context, x + size / 2f, y + size / 2f, size, color);
        }

        private void reset(int screenWidth) {
            x = random.nextInt(screenWidth);
            y = -random.nextInt(50);
            speedY = 0.5F + random.nextFloat() * 2;
            baseSize = random.nextInt(3) + 2;
            rotationAngle = random.nextFloat() * 360;
            rotationSpeed = random.nextFloat() * 2 - 1;
            opacity = 1.0F;
        }

        private void drawRotatingSnowflake(DrawContext context, float centerX, float centerY, int size, int color) {
            for (int i = 0; i < 4; i++) {
                double angle = Math.toRadians(rotationAngle + (i * 90));
                float armLength = size / 2f;
                float endX = centerX + (float) (armLength * Math.cos(angle));
                float endY = centerY + (float) (armLength * Math.sin(angle));
                context.fill((int) centerX, (int) centerY, (int) endX, (int) endY, color);
            }
        }

        public int getScreenHeight() {
            return screenHeight;
        }
    }
}