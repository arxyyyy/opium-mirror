package we.devs.opium.api.utilities;

import net.minecraft.client.gui.DrawContext;

import java.util.Random;

public class Snowflake {
    private float x, y, speedY, rotationAngle, rotationSpeed, opacity;
    private int baseSize, screenHeight;
    private final Random random = new Random();

    public Snowflake(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        reset(screenWidth);
    }

    public void update(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        y += speedY;
        rotationAngle += rotationSpeed;

        if (y > screenHeight || opacity <= 0) {
            reset(screenWidth);
        }
    }

    public void draw(DrawContext context) {
        int alpha = (int) (opacity * 0x80);
        int size = baseSize;
        int color = (alpha << 24) | 0xFFFFFF;

        drawRotatingSnowflake(context, x + size / 2f, y + size / 2f, size, color);
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

    private void reset(int screenWidth) {
        x = random.nextInt(screenWidth);
        y = -random.nextInt(50);
        speedY = 0.5F + random.nextFloat() * 2;
        baseSize = random.nextInt(3) + 2;
        rotationAngle = random.nextFloat() * 360;
        rotationSpeed = random.nextFloat() * 2 - 1;
        opacity = 1.0F;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}