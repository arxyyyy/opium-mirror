package we.devs.opium.api.utilities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class IconLoader {
    private static final Identifier ICON_PATH = Identifier.of("opium", "icons/logo.png");
    /**
     * Draws the icon at the specified position with the given dimensions.
     *
     * @param context The DrawContext used for rendering.
     * @param x       The x-coordinate for the icon's top-left corner.
     * @param y       The y-coordinate for the icon's top-left corner.
     * @param width   The width of the icon.
     * @param height  The height of the icon.
     */
    public static void drawIcon(DrawContext context, int x, int y, int width, int height) {
        // Bind and draw the texture
        context.drawTexture(ICON_PATH, x, y, 0, 0, width, height, width, height);
    }
    /**
     * Checks if the icon resource exists.
     * Logs an error message if the resource is missing.
     */
    public static void checkIconExists() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourceManager().getResource(ICON_PATH).isEmpty()) {
            System.err.println("IconLoader: Failed to load icon at " + ICON_PATH);
        }
    }
}