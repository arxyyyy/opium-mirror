package we.devs.opium.api.utilities;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.font.FontRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import we.devs.opium.api.utilities.ColorUtils;
import we.devs.opium.api.utilities.font.FontRenderers;
import we.devs.opium.api.utilities.font.ShadowFontRenderer;
import we.devs.opium.api.utilities.font.fxFontRenderer;
import we.devs.opium.client.modules.client.ModuleFont;

import java.awt.*;
import java.util.Objects;

public class RenderUtils implements IMinecraft {
    public static Frustum camera = new Frustum(new Matrix4f(), new Matrix4f());
    public static DrawContext drawContext;

    public static void setDrawContext(DrawContext drawContext) {
        RenderUtils.drawContext = drawContext;
    }

    public static void prepare() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void release() {
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.vertex(x, height, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(width, height, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(width, y, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(x, y, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    public static void drawOutline(MatrixStack matrices, float x, float y, float width, float height, float lineWidth, Color color) {
        drawRect(matrices, x + lineWidth, y, x - lineWidth, y + lineWidth, color);
        drawRect(matrices, x + lineWidth, y, width - lineWidth, y + lineWidth, color);
        drawRect(matrices, x, y, x + lineWidth, height, color);
        drawRect(matrices, width - lineWidth, y, width, height, color);
        drawRect(matrices, x + lineWidth, height - lineWidth, width - lineWidth, height, color);
    }

    public static void drawSidewaysGradient(MatrixStack matrices, float x, float y, float width, float height, Color startColor, Color endColor) {
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        drawSidewaysPart(bufferBuilder, matrix, x, y, width, height, startColor, endColor);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        release();
    }

    private static void drawSidewaysPart(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float width, float height, Color startColor, Color endColor) {
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f);
    }

    public static void drawCircle(float x, float y, float radius, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255f);
        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1) {
            bufferBuilder.vertex((float) (x + (Math.sin(i * degree) * radius)), (float) (y + (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x + (Math.sin(i * degree) * radius)), (float) (y - (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x - (Math.sin(i * degree) * radius)), (float) (y - (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x - (Math.sin(i * degree) * radius)), (float) (y + (Math.cos(i * degree) * radius)), 0.0F);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawBlock(BlockPos position, Color color) {
        RenderUtils.drawBlock(RenderUtils.getRenderBB(position), color);
    }

    public static void drawBlock(Box bb, Color color) {
        camera.setPosition(Objects.requireNonNull(mc.getCameraEntity()).getX(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ());
        if (camera.isVisible(new Box(bb.minX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.minY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.minZ + mc.getEntityRenderDispatcher().camera.getPos().z, bb.maxX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.maxY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.maxZ + mc.getEntityRenderDispatcher().camera.getPos().z))) {
            prepare();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            release();
        }
    }

    public static void drawBlockOutline(BlockPos position, Color color, float width) {
        drawBlockOutline(RenderUtils.getRenderBB(position), color, width);
    }

    public static void drawBlockOutline(Box bb, Color color, float width) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        camera.setPosition(Objects.requireNonNull(mc.getCameraEntity()).getX(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ());
        if (camera.isVisible(new Box(bb.minX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.minY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.minZ + mc.getEntityRenderDispatcher().camera.getPos().z, bb.maxX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.maxY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.maxZ + mc.getEntityRenderDispatcher().camera.getPos().z))) {
            prepare();
            RenderSystem.lineWidth(width);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            release();
        }
    }

    public static void scaleAndPosition(MatrixStack matrices, float x, float y, float scale) {
        matrices.push(); // Save the current transformation state

        // Translate the origin back to the desired position
        matrices.translate(x, y, 0);

        // Scale the matrix
        matrices.scale(scale, scale, 1.0F);

        matrices.translate(-x, -y, 0);
    }

    public static void stopScaling(MatrixStack matrices) {
        matrices.pop();
    }


    public static Box getRenderBB(Object position) {
        if (position instanceof BlockPos) {
            return new Box((double)((BlockPos)position).getX() - mc.getEntityRenderDispatcher().camera.getPos().x, (double)((BlockPos)position).getY() - mc.getEntityRenderDispatcher().camera.getPos().y, (double)((BlockPos)position).getZ() - mc.getEntityRenderDispatcher().camera.getPos().z, (double)(((BlockPos)position).getX() + 1) - mc.getEntityRenderDispatcher().camera.getPos().x, (double)(((BlockPos)position).getY() + 1) - mc.getEntityRenderDispatcher().camera.getPos().y, (double)(((BlockPos)position).getZ() + 1) - mc.getEntityRenderDispatcher().camera.getPos().z);
        }
        if (position instanceof Box) {
            return new Box(((Box)position).minX - mc.getEntityRenderDispatcher().camera.getPos().x, ((Box)position).minY - mc.getEntityRenderDispatcher().camera.getPos().y, ((Box)position).minZ - mc.getEntityRenderDispatcher().camera.getPos().z, ((Box)position).maxX - mc.getEntityRenderDispatcher().camera.getPos().x, ((Box)position).maxY - mc.getEntityRenderDispatcher().camera.getPos().y, ((Box)position).maxZ - mc.getEntityRenderDispatcher().camera.getPos().z);
        }
        return null;
    }

    public static FontRenderer getFontRenderer() {
        return FontRenderers.fontRenderer;
    }
    public static fxFontRenderer getFxFontRenderer() {
        return FontRenderers.fxfontRenderer;
    }

    public static void drawString(MatrixStack matrixStack, String text, float x, float y, int color) {
        if (!ModuleFont.INSTANCE.customFonts.getValue()) {
            color = fixColorValue(color);
            if(!ModuleFont.INSTANCE.textShadows.getValue()) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, (int) x, (int) y, color, false);
            } else if(ModuleFont.INSTANCE.textShadows.getValue()) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, (int) x, (int) y, color, true);
            }
        } else if (ModuleFont.INSTANCE.customFonts.getValue()) {
            if(getFontRenderer() == null) return;
            if(!ModuleFont.INSTANCE.textShadows.getValue()) {
                getFontRenderer().drawString(matrixStack, text, x, y, 256 - ColorUtils.getRed(color), 256 - ColorUtils.getGreen(color), 256 - ColorUtils.getBlue(color), 256 - ColorUtils.getAlpha(color));
            } else if(ModuleFont.INSTANCE.textShadows.getValue()) {
                ShadowFontRenderer shadowFont = new ShadowFontRenderer(getFontRenderer());
                shadowFont.drawStringWithShadow(matrixStack, text, x, y, 256 - ColorUtils.getRed(color), 256 - ColorUtils.getGreen(color), 256 - ColorUtils.getBlue(color), 256 - ColorUtils.getAlpha(color));
            }
        }
    }

    private static int fixColorValue(int color){
        return ColorUtils.setAlpha(color,5f,5).getRGB();
    }
}
