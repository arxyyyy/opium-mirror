package we.devs.opium.asm.mixins;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import we.devs.opium.Opium;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @Inject(method = "init()V", at = @At("TAIL"), cancellable = true)
    void init(CallbackInfo ci) {
        new Thread(() -> Opium.CONFIG_MANAGER.save()).start();
    }

}
