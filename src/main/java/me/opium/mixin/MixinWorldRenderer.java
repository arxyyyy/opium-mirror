package me.opium.mixin;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.opium.event.impl.Render3DEvent;
import me.opium.util.traits.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( WorldRenderer.class )
public class MixinWorldRenderer {
    @Inject(method = "render", at = @At("RETURN"))
    private void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                        LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci,
                        @Local MatrixStack stack) {
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Util.mc.gameRenderer.getCamera().getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Util.mc.gameRenderer.getCamera().getYaw() + 180f));

        MinecraftClient.getInstance().getProfiler().push("oyvey-render-3d");
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        Render3DEvent event = new Render3DEvent(stack, tickCounter.getTickDelta(true));
        Util.EVENT_BUS.post(event);
        MinecraftClient.getInstance().getProfiler().pop();
    }
}