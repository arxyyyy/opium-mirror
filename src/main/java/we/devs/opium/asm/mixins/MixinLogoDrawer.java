package we.devs.opium.asm.mixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer {

    @Shadow
    @Final
    private boolean ignoreAlpha;

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

    // Liste von Schneeflocken-Daten
    @Unique
    private final List<Map<String, Object>> snowflakes = new ArrayList<>();

    static {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("opium");
        if (modContainer.isPresent()) {
            StringBuilder builder = new StringBuilder("0piumh4ck.cc by ");
            Iterator<Person> i = modContainer.get().getMetadata().getAuthors().iterator();
            while (i.hasNext()) {
                Person next = i.next();
                builder.append(next.getName());
                if (i.hasNext()) builder.append(" & ");
            }
            text = builder.toString();
        }
    }

    /**
     * @author Cxiy
     * @reason Cxxxxx
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        // Bildschirmzittern
        int shakeOffsetX = 0, shakeOffsetY = 0;
        if (System.currentTimeMillis() < shakeEndTime) {
            shakeOffsetX = RANDOM.nextInt(10) - 5;
            shakeOffsetY = RANDOM.nextInt(10) - 5;
        }

        context.getMatrices().push();
        context.getMatrices().translate(shakeOffsetX, shakeOffsetY, 0);
        context.fillGradient(0, 0, screenWidth, screenHeight, 0x55000000, 0x33000000);

        // Falls Schneeflocken noch nicht initialisiert sind
        if (snowflakes == null || snowflakes.isEmpty()) {
            initializeSnowflakes(screenWidth, screenHeight);
        } else {
            resizeSnowflakesIfNecessary(screenWidth);
        }

        // Blitzeffekt
        handleLightning(context, screenWidth, screenHeight);

        // Zeichne Logo, Effekte und Schnee
        drawLogo(context, screenWidth, alpha, y);
        renderEffects(context, screenWidth, screenHeight);

        // Text zeichnen
        drawText(context, text, screenWidth);

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
        snowflakes.clear();
        for (int i = 0; i < INITIAL_SNOWFLAKE_COUNT; i++) {
            snowflakes.add(createSnowflake(screenWidth, screenHeight));
        }
    }

    @Unique
    private Map<String, Object> createSnowflake(int screenWidth, int screenHeight) {
        Map<String, Object> snowflake = new HashMap<>();
        snowflake.put("x", (float) RANDOM.nextInt(screenWidth));
        snowflake.put("y", (float) -RANDOM.nextInt(50));
        snowflake.put("speedY", 0.5F + RANDOM.nextFloat() * 2);
        snowflake.put("size", RANDOM.nextInt(3) + 2);
        snowflake.put("rotationAngle", RANDOM.nextFloat() * 360);
        snowflake.put("rotationSpeed", RANDOM.nextFloat() * 2 - 1);
        snowflake.put("opacity", 1.0F);
        snowflake.put("screenHeight", screenHeight);
        return snowflake;
    }

    @Unique
    private void resizeSnowflakesIfNecessary(int screenWidth) {
        int targetSnowflakeCount = screenWidth / 10;
        if (snowflakes.size() < targetSnowflakeCount) {
            for (int i = snowflakes.size(); i < targetSnowflakeCount; i++) {
                snowflakes.add(createSnowflake(screenWidth, getScreenHeight()));
            }
        } else if (snowflakes.size() > targetSnowflakeCount) {
            snowflakes.subList(targetSnowflakeCount, snowflakes.size()).clear();
        }
    }

    @Unique
    private void renderEffects(DrawContext context, int screenWidth, int screenHeight) {
        for (Map<String, Object> snowflake : snowflakes) {
            updateSnowflake(snowflake, screenWidth, screenHeight);
            drawSnowflake(snowflake, context);
        }
    }

    @Unique
    private void updateSnowflake(Map<String, Object> snowflake, int screenWidth, int screenHeight) {
        float y = (float) snowflake.get("y") + (float) snowflake.get("speedY");
        if (y > screenHeight || (float) snowflake.get("opacity") <= 0) {
            snowflake.clear();
            snowflake.putAll(createSnowflake(screenWidth, screenHeight));
        } else {
            snowflake.put("y", y);
            snowflake.put("rotationAngle", (float) snowflake.get("rotationAngle") + (float) snowflake.get("rotationSpeed"));
        }
    }

    @Unique
    private void drawSnowflake(Map<String, Object> snowflake, DrawContext context) {
        int alpha = (int) ((float) snowflake.get("opacity") * 0x80);
        int size = (int) snowflake.get("size");
        int color = (alpha << 24) | 0xFFFFFF;
        float centerX = (float) snowflake.get("x") + size / 2f;
        float centerY = (float) snowflake.get("y") + size / 2f;

        // Zeichne Schneeflockenarme
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians((float) snowflake.get("rotationAngle") + (i * 90));
            float armLength = size / 2f;
            float endX = centerX + (float) (armLength * Math.cos(angle));
            float endY = centerY + (float) (armLength * Math.sin(angle));
            context.fill((int) centerX, (int) centerY, (int) endX, (int) endY, color);
        }
    }

    @Unique
    private void handleLightning(DrawContext context, int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextLightningTime) {
            nextLightningTime = currentTime + RANDOM.nextInt(5000) + 3000;

            int startX = RANDOM.nextInt(screenWidth);
            drawLightning(context, startX, 0, screenHeight);

            // Zitter-Effekt
            shakeEndTime = currentTime + 200;
        }
    }

    @Unique
    private void drawLightning(DrawContext context, int startX, int startY, int endY) {
        int boltWidth = 2;
        int currentX = startX;
        int currentY = startY;

        while (currentY < endY) {
            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(20) - 10, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = Math.min(currentY + RANDOM.nextInt(30), endY);
            context.fill(currentX, currentY, nextX + boltWidth, nextY + boltWidth, 0x60FFFFFF);

            if (RANDOM.nextFloat() < 0.3) {
                drawBranch(context, currentX, currentY, endY / 2);
            }

            currentX = nextX;
            currentY = nextY;
        }
    }

    @Unique
    private void drawBranch(DrawContext context, int startX, int startY, int branchLength) {
        int currentX = startX;
        int currentY = startY;
        for (int i = 0; i < branchLength; i++) {
            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(15) - 7, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = currentY + RANDOM.nextInt(15);
            context.fill(currentX, currentY, nextX, nextY, 0x40FFFFFF);

            currentX = nextX;
            currentY = nextY;

            if (RANDOM.nextFloat() < 0.1) {
                break;
            }
        }
    }

    @Unique
    private void drawText(DrawContext context, String text, int screenWidth) {
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
    }

    @Unique
    private int getScreenHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }
}