package org.nrnr.opium.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.nrnr.opium.Opium;
import org.nrnr.opium.impl.event.MouseClickEvent;
import org.nrnr.opium.impl.event.MouseUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    /**
     * @param window
     * @param button
     * @param action
     * @param mods
     * @param ci
     */
    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods,
                               CallbackInfo ci) {
        MouseClickEvent mouseClickEvent = new MouseClickEvent(button, action);
        Opium.EVENT_HANDLER.dispatch(mouseClickEvent);
        if (mouseClickEvent.isCanceled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void onUpdate(ClientPlayerEntity instance, double cursorDeltaX, double cursorDeltaY) {
        MouseUpdateEvent mouseUpdateEvent = new MouseUpdateEvent(cursorDeltaX, cursorDeltaY);
        Opium.EVENT_HANDLER.dispatch(mouseUpdateEvent);

        if (!mouseUpdateEvent.isCanceled()) {
            instance.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }
}
