package org.nrnr.opium.impl.event.render.item;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;

@Cancelable
public class RenderArmEvent extends Event {

    public final MatrixStack matrices;
    public final VertexConsumerProvider vertexConsumers;
    public final int light;
    public final Arm arm;
    public final float equipProgress;
    public final float swingProgress;
    public final PlayerEntityRenderer playerEntityRenderer;

    public RenderArmEvent(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, PlayerEntityRenderer playerEntityRenderer) {
        this.matrices = matrices;
        this.vertexConsumers = vertexConsumers;
        this.light = light;
        this.equipProgress = equipProgress;
        this.swingProgress = swingProgress;
        this.arm = arm;
        this.playerEntityRenderer = playerEntityRenderer;
    }
}
