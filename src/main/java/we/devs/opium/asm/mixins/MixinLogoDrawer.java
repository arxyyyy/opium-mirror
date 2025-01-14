package we.devs.opium.asm.mixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import we.devs.opium.api.utilities.Snowflake;
import we.devs.opium.Opium;

import java.util.*;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer {

    @Shadow
    @Final
    private boolean ignoreAlpha;

    @Unique
    private final List<Snowflake> snowflakes = new ArrayList<>();
    @Unique
    private static final int INITIAL_SNOWFLAKE_COUNT = 100;
    @Unique
    private static final Random RANDOM = new Random();

    @Unique
    private long nextLightningTime = 0;
    @Unique
    private long shakeEndTime = 0;

    @Unique
    private static String text = "0piumh4ck.cc";
    static {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("opium");
        if(modContainer.isPresent()) {
            StringBuilder builder = new StringBuilder("0piumh4ck.cc by ");
            Iterator<Person> i = modContainer.get().getMetadata().getAuthors().iterator();
            while (i.hasNext()) {
                Person next = i.next();
                builder.append(next.getName());
                if(i.hasNext()) builder.append(" & ");
            }
            text = builder.toString();
        }
    }
    /**
     * Makes Menu Hot
     * @author Cxiy
     * @reason cz why not
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        int shakeOffsetY;
        int shakeOffsetX;
        if (System.currentTimeMillis() < shakeEndTime) {
            shakeOffsetX = RANDOM.nextInt(10) - 5;
            shakeOffsetY = RANDOM.nextInt(10) - 5;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        context.getMatrices().push();
        context.getMatrices().translate(shakeOffsetX, shakeOffsetY, 0);
        context.fillGradient(0, 0, screenWidth, screenHeight, 0x55000000, 0x33000000);

        if (snowflakes.isEmpty()) {
            initializeSnowflakes(screenWidth, screenHeight);
        }

        drawLogo(context, screenWidth, alpha, y);
        renderEffects(context, screenWidth, screenHeight);

        int x = 10;
        int yPosition = 10;
        int color = 0xFF808080;

        for (int i = 0; i < text.length(); i++) {
            int charX = x + MinecraftClient.getInstance().textRenderer.getWidth(text.substring(0, i));
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, String.valueOf(text.charAt(i)), charX, yPosition, color);
        }

        long time = System.currentTimeMillis();
        int charIndex = (int) ((time / 100) % text.length());
        int glintColor = 0xFFFFFFFF;

        for (int i = 0; i < 5; i++) {
            int currentIndex = (charIndex + i) % text.length();
            int glintCharX = x + MinecraftClient.getInstance().textRenderer.getWidth(text.substring(0, currentIndex));
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, String.valueOf(text.charAt(currentIndex)), glintCharX, yPosition, glintColor);
        }

        context.getMatrices().pop();
    }

    @Unique
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

    @Unique
    private void initializeSnowflakes(int screenWidth, int screenHeight) {
        for (int i = 0; i < INITIAL_SNOWFLAKE_COUNT; i++) {
            snowflakes.add(new Snowflake(screenWidth, screenHeight));
        }
    }

    @Unique
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

    @Unique
    private void renderEffects(DrawContext context, int screenWidth, int screenHeight) {
        snowflakes.forEach(snowflake -> {
            snowflake.update(screenWidth, screenHeight);
            snowflake.draw(context);
        });
        handleLightning(context, screenWidth, screenHeight);
    }

    @Unique
    private void handleLightning(DrawContext context, int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextLightningTime) {
            nextLightningTime = currentTime + RANDOM.nextInt(5000) + 3000;

            int startX = RANDOM.nextInt(screenWidth);
            drawLightning(context, startX, 0, screenHeight);

            // Start Shake-Effekt
            shakeEndTime = System.currentTimeMillis() + 200; // Shake für 200ms aktiv
        }
    }

    @Unique
    private void drawLightning(DrawContext context, int startX, int startY, int endY) {
        int boltWidth = 2;
        int currentX = startX, currentY = startY;
        int baseAlpha = 0x80; // Transparenter Blitz
        int baseColor = 0xFFFFFF;

        while (currentY < endY) {
            float alphaFactor = 0.8f - ((float) currentY / (endY * 1.2f));
            int currentAlpha = (int) (baseAlpha * alphaFactor);
            int color = (currentAlpha << 24) | baseColor;

            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(20) - 10, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = Math.min(currentY + RANDOM.nextInt(30), endY);

            context.fill(currentX, currentY, nextX + boltWidth, nextY + boltWidth, color);

            if (RANDOM.nextFloat() < 0.3) {
                drawBranch(context, currentX, currentY, baseColor, baseAlpha, endY / 2);
            }

            currentX = nextX;
            currentY = nextY;
        }
    }

    @Unique
    private void drawBranch(DrawContext context, int startX, int startY, int baseColor, int baseAlpha, int branchLength) {
        int boltWidth = 1;
        int currentX = startX, currentY = startY;

        for (int i = 0; i < branchLength; i++) {
            float alphaFactor = 0.7f - ((float) i / (branchLength * 1.3f));
            int currentAlpha = (int) (baseAlpha * alphaFactor);
            int color = (currentAlpha << 24) | baseColor;

            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(15) - 7, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = currentY + RANDOM.nextInt(15);

            context.fill(currentX, currentY, nextX + boltWidth, nextY + boltWidth, color);

            currentX = nextX;
            currentY = nextY;

            if (RANDOM.nextFloat() < 0.1) {
                break;
            }
        }
    }
}
