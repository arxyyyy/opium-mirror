package we.devs.opium.api.events;

import net.minecraft.entity.LivingEntity;
import we.devs.opium.api.manager.event.Event;

/**
 * Represents an event triggered when a living entity dies.
 */
public class DeathEvent extends Event {
    private final LivingEntity entity;

    /**
     * Constructs a new DeathEvent.
     *
     * @param entity The entity that died.
     */
    public DeathEvent(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets the entity involved in the death event.
     *
     * @return The deceased entity.
     */
    public LivingEntity getEntity() {
        return entity;
    }
}
