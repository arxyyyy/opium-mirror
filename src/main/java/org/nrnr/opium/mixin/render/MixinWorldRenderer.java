package org.nrnr.opium.mixin.render;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.api.render.RenderBuffers;
import org.nrnr.opium.impl.event.PerspectiveEvent;
import org.nrnr.opium.impl.event.render.RenderWorldBorderEvent;
import org.nrnr.opium.impl.event.render.RenderWorldEvent;
import org.nrnr.opium.impl.module.render.ShadersModule;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.Globals;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author chronos
 * @since 1.0
 */
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements Globals {

    /**
     * @param matrices
     * @param tickDelta
     * @param limitTime
     * @param renderBlockOutline
     * @param camera
     * @param gameRenderer
     * @param lightmapTextureManager
     * @param positionMatrix
     * @param ci
     */
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void hookRender(MatrixStack matrices, float tickDelta,
                            long limitTime, boolean renderBlockOutline,
                            Camera camera, GameRenderer gameRenderer,
                            LightmapTextureManager lightmapTextureManager,
                            Matrix4f positionMatrix, CallbackInfo ci) {
        Vec3d pos = mc.getBlockEntityRenderDispatcher().camera.getPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        Module.render3D(matrices);
        RenderBuffers.preRender();



        final RenderWorldEvent renderWorldEvent =
                new RenderWorldEvent(matrices, tickDelta);
        Opium.EVENT_HANDLER.dispatch(renderWorldEvent);

        RenderBuffers.postRender();
    }

    /**
     * @param camera
     * @param ci
     */
    @Inject(method = "renderWorldBorder", at = @At(value = "HEAD"), cancellable = true)
    private void hookRenderWorldBorder(Camera camera, CallbackInfo ci) {
        RenderWorldBorderEvent renderWorldBorderEvent =
                new RenderWorldBorderEvent();
        Opium.EVENT_HANDLER.dispatch(renderWorldBorderEvent);
        if (renderWorldBorderEvent.isCanceled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
    public boolean hookRender(Camera instance) {
        PerspectiveEvent perspectiveEvent = new PerspectiveEvent(instance);
        Opium.EVENT_HANDLER.dispatch(perspectiveEvent);

        if (perspectiveEvent.isCanceled()) {
            return true;
        }
        return instance.isThirdPerson();
    }


//    /**
//     *
//     * @param builder
//     * @param f
//     * @param cir
//     */
//    @Inject(method = "renderSky(Lnet/minecraft/client/render/BufferBuilder;F)" +
//            "Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
//            at = @At(value = "HEAD"), cancellable = true)
//    private static void hookRenderSky(BufferBuilder builder, float f,
//                                      CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
//        cir.cancel();
//        float g = Math.signum(f) * 512.0f;
//        RenderSystem.setShader(GameRenderer::getPositionProgram);
//        builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
//        builder.vertex(0.0, -64.0f, 0.0).next();
//        for (int i = -180; i <= 180; i += 45) {
//            builder.vertex(g * MathHelper.cos((float)i * ((float)Math.PI / 180)), -64.0f, 512.0f * MathHelper.sin((float)i * ((float)Math.PI / 180))).next();
//        }
//        cir.setReturnValue(builder.end());
//    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal = 0))
    void replaceShaderHook(PostEffectProcessor instance, float tickDelta) {
        if (ShadersModule.INSTANCE.isEnabled() && mc.world != null) {
            if (Managers.SHADER.fullNullCheck()) return;
            Managers.SHADER.setupShader(ShadersModule.INSTANCE.mode.getValue(), Managers.SHADER.getShaderOutline(ShadersModule.INSTANCE.mode.getValue()));
        } else {
            instance.render(tickDelta);
        }
    }
}