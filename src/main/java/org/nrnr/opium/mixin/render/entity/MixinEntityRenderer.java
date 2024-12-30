package org.nrnr.opium.mixin.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.nrnr.opium.init.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    /**
     * @param entity
     * @param text
     * @param matrices
     * @param vertexConsumers
     * @param light
     * @param ci
     */
    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE"),
            cancellable = true)
    public void hookRenderLabelIfPresent(Entity entity, Text text, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof PlayerEntity && Modules.NAMETAGS.isEnabled()) {
            ci.cancel();
        }
    }
}
