package we.devs.opium.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import we.devs.opium.Opium;
import we.devs.opium.api.utilities.IMinecraft;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin implements IMinecraft {

    @Inject(method = "init()V", at = @At("TAIL"), cancellable = true)
    void init(CallbackInfo ci) {
        new Thread(() -> Opium.CONFIG_MANAGER.save()).start();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //l4j said hola amigos
        context.drawTexture(Identifier.of("opium", "textures/gayassbackground.png"), 0, 0, 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), mc.getWindow().getScaledWidth(),  mc.getWindow().getScaledHeight());
    }
}
