package we.devs.opium.client.gui.config;

import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import we.devs.opium.api.manager.miscellaneous.ConfigManager;
import we.devs.opium.api.utilities.RenderUtils;
import we.devs.opium.api.utilities.SnowflakeRenderer;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManagerScreen extends Screen {

    private int windowX, windowY; // Window's current position
    private int windowWidth, windowHeight; // Window's dimensions
    private int dragOffsetX, dragOffsetY; // Offset to make dragging smooth
    private boolean isDragging = false; // Flag to track if the window is being dragged

    private static final int TITLE_BAR_HEIGHT = 20; // Height of the title bar
    private static final int CLOSE_BUTTON_SIZE = 15; // Size of the close button
    private List<String> configFiles;  // List to hold the file names

    private SnowflakeRenderer snowflakeRenderer; // Snowflake renderer

    public ConfigManagerScreen() {
        super(Text.literal("Config Manager"));
    }

    @Override
    public void init() {
        File configDirectory = new File(String.valueOf(Path.of(ConfigManager.CONFIG_DIRECTORY)));  // Specify the correct path
        if (configDirectory.exists() && configDirectory.isDirectory()) {
            String[] files = configDirectory.list((dir, name) -> name.endsWith(".json"));
            configFiles = new ArrayList<>();
            if (files != null) {
                for (String file : files) {
                    configFiles.add(file.replace(".json", ""));  // Remove .json extension for display
                }
            }
        }
        // Dynamically scale the window based on the screen size
        windowWidth = (int) (this.width * 0.6); // 60% of screen width
        windowHeight = (int) (this.height * 0.6); // 60% of screen height
        windowX = (this.width - windowWidth) / 2;
        windowY = (this.height - windowHeight) / 2;

        // Initialize snowflakes
        snowflakeRenderer = new SnowflakeRenderer();
        snowflakeRenderer.initializeSnowflakes(this.width, this.height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);

        // Render the snowflakes first
        snowflakeRenderer.renderSnowflakes(context, this.width, this.height);

        // Draw the window background
        context.fill(windowX, windowY, windowX + windowWidth, windowY + windowHeight, 0xFF2F2F2F);

        // Draw the title bar
        context.fill(windowX, windowY, windowX + windowWidth, windowY + TITLE_BAR_HEIGHT, 0xFF444444);

        // Draw the Config Manager text
        int textX = windowX + 10;
        int textY = windowY + 5;
        int closeButtonX = windowX + windowWidth - CLOSE_BUTTON_SIZE - 5;
        int closeButtonY = windowY + (TITLE_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;

        RenderUtils.drawString(context.getMatrices(), "X", closeButtonX + 5, closeButtonY + 3, 0xFFFFFFFF); // White "X"
        RenderUtils.drawString(context.getMatrices(), "Config Manager", textX, textY, 0xFFFFFFFF);

        // Calculate the starting Y position for the config list
        int itemY = windowY + TITLE_BAR_HEIGHT + 10;  // Add some padding below the title bar

        // Loop through the config files and display them
        for (String configFile : configFiles) {
            // Draw the config name box (rounded)
            int boxWidth = 200;  // Width of each config box
            int boxHeight = 30;  // Height of each config box
            int boxX = windowX + 10;  // Start drawing from left of window
            Renderer2d.renderRoundedOutline(context.getMatrices(), Color.GRAY, boxX, itemY, boxX + windowWidth - 20, itemY + boxHeight,10,1,100);
            RenderUtils.drawCircle(boxX, (float) itemY + 5, 5, Color.WHITE);


            // Draw the config file name
            RenderUtils.drawString(context.getMatrices(), "Config: " + configFile, boxX + 5, itemY + 3, 0xFFFFFFFF);



//            // Draw the buttons (Edit, Delete)
//            int buttonWidth = 50;
//            int buttonHeight = 20;
//            // Edit Button
//            //RenderUtils.drawOutline(context.getMatrices(), boxX + boxWidth + 10, itemY + 5, boxX + boxWidth + 10, itemY + 5 + buttonHeight, 10, Color.WHITE);
//            context.fill(boxX + boxWidth + 10, itemY + 5, boxX + boxWidth + 10 + buttonWidth, itemY + 5 + buttonHeight, 0xFF00FF00);
//            RenderUtils.drawString(context.getMatrices(), "Edit", boxX + boxWidth + 15, itemY + 10, 0xFFFFFF);
//
//            // Delete Button
//            context.fill(boxX + boxWidth + 70, itemY + 5, boxX + boxWidth + 70 + buttonWidth, itemY + 5 + buttonHeight, 0xFFFF0000);
//            RenderUtils.drawString(context.getMatrices(), "Delete", boxX + boxWidth + 75, itemY + 10, 0xFFFFFF);

            // Move to the next row
            itemY += boxHeight + 10;  // Add space between boxes
        }
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the close button was clicked
        int closeButtonX = windowX + windowWidth - CLOSE_BUTTON_SIZE - 5;
        int closeButtonY = windowY + (TITLE_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
        if (mouseX >= closeButtonX && mouseX <= closeButtonX + CLOSE_BUTTON_SIZE &&
                mouseY >= closeButtonY && mouseY <= closeButtonY + CLOSE_BUTTON_SIZE) {
            this.close(); // Close the screen
            return true;
        }

        // If the click is inside the title bar, start dragging
        if (mouseX >= windowX && mouseX <= windowX + windowWidth && mouseY >= windowY && mouseY <= windowY + TITLE_BAR_HEIGHT) {
            dragOffsetX = (int) mouseX - windowX;
            dragOffsetY = (int) mouseY - windowY;
            isDragging = true;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        // Stop dragging when the mouse button is released
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // If the window is being dragged, update its position
        if (isDragging) {
            windowX = (int) mouseX - dragOffsetX;
            windowY = (int) mouseY - dragOffsetY;
        }
        return false;
    }
}
