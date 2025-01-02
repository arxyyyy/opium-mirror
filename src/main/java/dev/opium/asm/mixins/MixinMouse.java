package dev.opium.asm.mixins;

import dev.opium.Opium;
import dev.opium.api.events.impl.MouseUpdateEvent;
import dev.opium.mod.gui.clickgui.ClickGuiScreen;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.opium.api.utils.Wrapper.mc;
@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouse(long window, int button, int action, int mods, CallbackInfo ci) {
        int key = -(button + 2);
        if (mc.currentScreen instanceof ClickGuiScreen && action == 1 && Opium.MODULE.setBind(key)) {
            return;
        }
        if (action == 1) {
            Opium.MODULE.onKeyPressed(key);
        }
        if (action == 0) {
            Opium.MODULE.onKeyReleased(key);
        }
    }

    @Inject(method = "updateMouse", at = @At("RETURN"))
    private void updateHook(CallbackInfo ci) {
        Opium.EVENT_BUS.post(new MouseUpdateEvent());
    }
}
