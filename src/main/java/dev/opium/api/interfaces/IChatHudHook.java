package dev.opium.api.interfaces;

import net.minecraft.text.Text;

public interface IChatHudHook {
    void addMessage(Text message, int id);
}
