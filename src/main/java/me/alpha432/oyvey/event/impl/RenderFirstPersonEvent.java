package me.alpha432.oyvey.event.impl;


import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import me.alpha432.oyvey.event.Event;

public class RenderFirstPersonEvent extends Event {
    public final Hand hand;
    public final ItemStack item;
    public final float equipProgress;
    public final MatrixStack matrices;

    public RenderFirstPersonEvent(Hand hand, ItemStack item, float equipProgress, MatrixStack matrices) {
        this.hand = hand;
        this.item = item;
        this.equipProgress = equipProgress;
        this.matrices = matrices;
    }
}