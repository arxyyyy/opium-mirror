package org.nrnr.opium.impl.event.render.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.render.entity.MixinPlayerEntityRenderer;

/**
 * @author chronos
 * @see MixinPlayerEntityRenderer
 * @since 1.0
 */
@Cancelable
public class RenderPlayerEvent extends Event {
    //
    private final AbstractClientPlayerEntity entity;
    //
    private float yaw;
    private float pitch;

    /**
     * @param entity
     */
    public RenderPlayerEvent(AbstractClientPlayerEntity entity) {
        this.entity = entity;
    }

    /**
     * @return
     */
    public AbstractClientPlayerEntity getEntity() {
        return entity;
    }


    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
