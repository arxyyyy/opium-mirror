package org.nrnr.opium.impl.event.network;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.mixin.network.MixinClientPlayerEntity;
import org.nrnr.opium.util.Globals;

/**
 * @author chronos
 * @see MixinClientPlayerEntity
 * @since 1.0
 */
public class SetCurrentHandEvent extends Event implements Globals {
    //
    private final Hand hand;

    public SetCurrentHandEvent(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public ItemStack getStackInHand() {
        return mc.player.getStackInHand(hand);
    }
}
