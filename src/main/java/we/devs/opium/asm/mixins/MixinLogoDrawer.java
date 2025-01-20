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
    private boolean ignoreAlpha; // Shadowed field for alpha transparency handling

    @Unique
    private static final int INITIAL_SNOWFLAKE_COUNT = 100; // Initial snowflake count
    @Unique
    private static final Random RANDOM = new Random(); // Random number generator for effects

    @Unique
    private long nextLightningTime = 0; // Time for the next lightning effect
    @Unique
    private long shakeEndTime = 0; // End time for screen shake effect
    @Unique
    private static String text = "0piumh4ck.cc"; // Text to display on the screen

    // List of Snowflake Data (used for rendering snowflakes)
    @Unique
    private final List<Map<String, Object>> snowflakes = new ArrayList<>();

    static {
        // Fetch the mod metadata to set the text dynamically if available
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("opium");
        if (modContainer.isPresent()) {
            StringBuilder builder = new StringBuilder("0piumh4ck.cc by ");
            Iterator<Person> i = modContainer.get().getMetadata().getAuthors().iterator();
            while (i.hasNext()) {
                Person next = i.next();
                builder.append(next.getName());
                if (i.hasNext()) builder.append(" & ");
            }
            text = builder.toString(); // Set the dynamic text to include authors
        }
    }

    /**
     * Custom draw method for the logo, effects, and snowflakes.
     * @param context The draw context for rendering
     * @param screenWidth The width of the screen
     * @param alpha Transparency factor for the logo
     * @param y The vertical position of the logo
     * @author Cxiy
     * @reason Because Looks Cool
     */
    @Overwrite
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight(); // Get screen height

        // Handle screen shake effect (random offset)
        int shakeOffsetX = 0, shakeOffsetY = 0;
        if (System.currentTimeMillis() < shakeEndTime) {
            shakeOffsetX = RANDOM.nextInt(10) - 5;
            shakeOffsetY = RANDOM.nextInt(10) - 5;
        }

        // Push the current transformation matrix to allow translations and scaling
        context.getMatrices().push();
        context.getMatrices().translate(shakeOffsetX, shakeOffsetY, 0); // Apply shake effect

        // Fill the screen with a semi-transparent gradient for a background effect
        context.fillGradient(0, 0, screenWidth, screenHeight, 0x55000000, 0x33000000);

        // Initialize or resize snowflakes if necessary
        if (snowflakes.isEmpty()) {
            initializeSnowflakes(screenWidth, screenHeight);
        } else {
            resizeSnowflakesIfNecessary(screenWidth);
        }

        drawGlowEffect(context, screenWidth, screenHeight);
        // Handle lightning effect (random time and screen shake)
        handleLightning(context, screenWidth, screenHeight);

        // Draw the logo and associated effects (pulsing and scaling)
        drawLogo(context, screenWidth, alpha, y);

        // Render additional effects such as snowflakes and lightning
        renderEffects(context, screenWidth, screenHeight);

        // Draw the custom text
        drawText(context, text, screenWidth);

        // Pop the matrix to restore previous transformations
        context.getMatrices().pop();
    }

    /**
     * Draw the logo with a pulsing effect
     * @param context The draw context for rendering
     * @param screenWidth The width of the screen
     * @param alpha Transparency factor for the logo
     * @param y The vertical position of the logo
     */
    @Unique
    private void drawLogo(DrawContext context, int screenWidth, float alpha, int y) {
        // Pulsation factor for the logo scaling effect
        float pulsationFactor = 1.0f + 0.05f * (float) Math.sin(System.currentTimeMillis() / 300.0);
        int width = 506, height = 75;
        float scaledWidth = width * pulsationFactor;
        float scaledHeight = height * pulsationFactor;
        float centerX = (screenWidth - scaledWidth) / 2.0f;

        // Push the matrix and apply scaling and translation for logo rendering
        context.getMatrices().push();
        context.getMatrices().translate(centerX + scaledWidth / 2.0f, y + scaledHeight / 2.0f, 0.0);
        context.getMatrices().scale(pulsationFactor, pulsationFactor, 1.0f);
        context.getMatrices().translate(-width / 2.0f, -height / 2.0f, 0.0);

        // Set shader color and draw the logo texture
        context.setShaderColor(1.0F, 1.0F, 1.0F, ignoreAlpha ? 1.0F : alpha);
        context.drawTexture(
                Identifier.of("opium", "icons/title.png"),
                0, 0, 0, 0, width, height, width, height
        );

        // Pop the matrix to restore previous transformations
        context.getMatrices().pop();
    }

    /**
     * Initialize the snowflakes with random positions and properties.
     * @param screenWidth The screen width for snowflake generation
     * @param screenHeight The screen height for snowflake generation
     */
    @Unique
    private void initializeSnowflakes(int screenWidth, int screenHeight) {
        snowflakes.clear(); // Clear any existing snowflakes
        for (int i = 0; i < INITIAL_SNOWFLAKE_COUNT; i++) {
            snowflakes.add(createSnowflake(screenWidth, screenHeight)); // Add new snowflakes
        }
    }

    /**
     * Create a single snowflake with random properties.
     * @param screenWidth The screen width
     * @param screenHeight The screen height
     * @return A map representing the snowflake's properties
     */
    @Unique
    private Map<String, Object> createSnowflake(int screenWidth, int screenHeight) {
        Map<String, Object> snowflake = new HashMap<>();
        snowflake.put("x", (float) RANDOM.nextInt(screenWidth)); // Random X position
        snowflake.put("y", (float) -RANDOM.nextInt(50)); // Random Y position above the screen
        snowflake.put("speedY", 0.5F + RANDOM.nextFloat() * 2); // Random Y-speed
        snowflake.put("size", RANDOM.nextInt(3) + 2); // Random size
        snowflake.put("rotationAngle", RANDOM.nextFloat() * 360); // Random rotation angle
        snowflake.put("rotationSpeed", RANDOM.nextFloat() * 2 - 1); // Random rotation speed
        snowflake.put("opacity", 1.0F); // Opacity of the snowflake
        snowflake.put("screenHeight", screenHeight); // Store the screen height
        return snowflake;
    }

    /**
     * Resize snowflakes based on screen width (adjust the number of snowflakes).
     * @param screenWidth The current screen width
     */
    @Unique
    private void resizeSnowflakesIfNecessary(int screenWidth) {
        int targetSnowflakeCount = screenWidth / 10; // Set target snowflake count based on screen width
        if (snowflakes.size() < targetSnowflakeCount) {
            for (int i = snowflakes.size(); i < targetSnowflakeCount; i++) {
                snowflakes.add(createSnowflake(screenWidth, getScreenHeight())); // Add snowflakes if needed
            }
        } else if (snowflakes.size() > targetSnowflakeCount) {
            snowflakes.subList(targetSnowflakeCount, snowflakes.size()).clear(); // Remove excess snowflakes
        }
    }

    /**
     * Render snowflakes and other effects.
     * @param context The draw context for rendering
     * @param screenWidth The screen width
     * @param screenHeight The screen height
     */
    @Unique
    private void renderEffects(DrawContext context, int screenWidth, int screenHeight) {
        for (Map<String, Object> snowflake : snowflakes) {
            updateSnowflake(snowflake, screenWidth, screenHeight); // Update snowflake position
            drawSnowflake(snowflake, context); // Draw the snowflake
        }
    }

    /**
     * Update the position of a snowflake based on its speed and check for wrapping.
     * @param snowflake The snowflake map
     * @param screenWidth The screen width
     * @param screenHeight The screen height
     */
    @Unique
    private void updateSnowflake(Map<String, Object> snowflake, int screenWidth, int screenHeight) {
        float y = (float) snowflake.get("y") + (float) snowflake.get("speedY"); // Update Y position
        if (y > screenHeight || (float) snowflake.get("opacity") <= 0) {
            snowflake.clear(); // Reset snowflake if it goes off-screen
            snowflake.putAll(createSnowflake(screenWidth, screenHeight)); // Create new snowflake
        } else {
            snowflake.put("y", y); // Update Y position
            snowflake.put("rotationAngle", (float) snowflake.get("rotationAngle") + (float) snowflake.get("rotationSpeed")); // Update rotation angle
        }
    }

    @Unique
    private void drawGlowEffect(DrawContext context, int screenWidth, int screenHeight) {
        // Define the gradient colors for the glow effect (lighter at the top and darker towards the middle)
        int topColor = 0x33808080; // Semi-transparent gray
        int bottomColor = 0x00808080; // More transparent gray



        // Draw the gradient rectangle at the top of the screen
        context.fillGradient(0, 0, screenWidth, screenHeight, topColor, bottomColor); // Height of glow can be adjusted
    }

    /**
     * Draw a snowflake on the screen.
     * @param snowflake The snowflake map
     * @param context The draw context
     */
    @Unique
    private void drawSnowflake(Map<String, Object> snowflake, DrawContext context) {
        int alpha = (int) ((float) snowflake.get("opacity") * 0x80); // Calculate alpha transparency
        int size = (int) snowflake.get("size"); // Get snowflake size
        int color = (alpha << 24) | 0xFFFFFF; // Set color with transparency
        float centerX = (float) snowflake.get("x") + size / 2f; // Calculate center X
        float centerY = (float) snowflake.get("y") + size / 2f; // Calculate center Y

        // Draw snowflake arms (4 arms at 90 degrees apart)
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians((float) snowflake.get("rotationAngle") + (i * 90)); // Calculate angle for each arm
            float armLength = size / 2f;
            float endX = centerX + (float) (armLength * Math.cos(angle)); // Calculate end point for arm
            float endY = centerY + (float) (armLength * Math.sin(angle));
            context.fill((int) centerX, (int) centerY, (int) endX, (int) endY, color); // Draw the arm
        }
    }

    /**
     * Handle the lightning effect (random time and position)
     * @param context The draw context
     * @param screenWidth The screen width
     * @param screenHeight The screen height
     */
    @Unique
    private void handleLightning(DrawContext context, int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextLightningTime) {
            nextLightningTime = currentTime + RANDOM.nextInt(5000) + 3000; // Set next lightning time

            int startX = RANDOM.nextInt(screenWidth); // Random start X position
            drawLightning(context, startX, screenHeight); // Draw lightning

            // Apply shake effect
            shakeEndTime = currentTime + 200;
        }
    }

    /**
     * Draw the lightning bolt from top to bottom
     *
     * @param context The draw context
     * @param startX  The starting X position
     * @param endY    The ending Y position
     */
    @Unique
    private void drawLightning(DrawContext context, int startX, int endY) {
        int boltWidth = 2; // Bolt width
        int currentX = startX;
        int currentY = 0;

        // Draw the lightning bolt with random offsets
        while (currentY < endY) {
            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(20) - 10, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = Math.min(currentY + RANDOM.nextInt(30), endY);
            context.fill(currentX, currentY, nextX + boltWidth, nextY + boltWidth, 0x60FFFFFF);

            // Draw branches occasionally
            if (RANDOM.nextFloat() < 0.3) {
                drawBranch(context, currentX, currentY, endY / 2);
            }

            currentX = nextX;
            currentY = nextY;
        }
    }

    /**
     * Draw a branch of the lightning bolt
     * @param context The draw context
     * @param startX The starting X position
     * @param startY The starting Y position
     * @param branchLength The length of the branch
     */
    @Unique
    private void drawBranch(DrawContext context, int startX, int startY, int branchLength) {
        int currentX = startX;
        int currentY = startY;
        for (int i = 0; i < branchLength; i++) {
            int nextX = Math.max(0, Math.min(currentX + RANDOM.nextInt(15) - 7, MinecraftClient.getInstance().getWindow().getScaledWidth()));
            int nextY = currentY + RANDOM.nextInt(15);
            context.fill(currentX, currentY, nextX, nextY, 0x40FFFFFF); // Draw branch

            currentX = nextX;
            currentY = nextY;

            if (RANDOM.nextFloat() < 0.1) {
                break; // Occasionally end the branch early
            }
        }
    }

    /**
     * Draw custom text on the screen
     * @param context The draw context
     * @param text The text to display
     * @param screenWidth The screen width
     */
    @Unique
    private void drawText(DrawContext context, String text, int screenWidth) {
        int x = 2; // Starting X position
        int yPosition = 10; // Starting Y position
        int color = 0xFF808080; // Gray color for text

        // Draw each character of the text
        for (int i = 0; i < text.length(); i++) {
            int charX = x + MinecraftClient.getInstance().textRenderer.getWidth(text.substring(0, i));
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, String.valueOf(text.charAt(i)), charX, yPosition, color);
        }

        // Add glint effect to the text over time
        long time = System.currentTimeMillis();
        int charIndex = (int) ((time / 100) % text.length()); // Calculate character index based on time
        int glintColor = 0xFFFFFFFF; // White color for glint

        for (int i = 0; i < 5; i++) {
            int currentIndex = (charIndex + i) % text.length();
            int glintCharX = x + MinecraftClient.getInstance().textRenderer.getWidth(text.substring(0, currentIndex));
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, String.valueOf(text.charAt(currentIndex)), glintCharX, yPosition, glintColor);
        }
    }

    /**
     * Get the screen height
     * @return The screen height
     */
    @Unique
    private int getScreenHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }
}
