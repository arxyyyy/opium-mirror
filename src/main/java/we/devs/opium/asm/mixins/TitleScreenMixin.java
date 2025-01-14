package we.devs.opium.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import we.devs.opium.api.utilities.IMinecraft;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin implements IMinecraft {

    private static final Identifier CUSTOM_MUSIC = Identifier.of("opium", "custom_music");
    private static boolean isPlayingCustomMusic = false;

    @Inject(method = "init()V", at = @At("TAIL"))
    void init(CallbackInfo ci) {
        // Stop die Minecraft-Standardmusik
        mc.getMusicTracker().stop();

        // Prüfen, ob die eigene Musik bereits läuft
        if (!isPlayingCustomMusic) {
            isPlayingCustomMusic = true; // Flag setzen, um zu verhindern, dass die Musik mehrfach gestartet wird
            SoundInstance musicInstance = PositionedSoundInstance.music(SoundEvent.of(CUSTOM_MUSIC));
            mc.getSoundManager().play(musicInstance);
        }
    }

    @Inject(method = "removed()V", at = @At("HEAD"))
    void removed(CallbackInfo ci) {
        // Musik nicht sofort stoppen, sondern Flag nicht zurücksetzen, es sei denn, dies ist absichtlich
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.drawTexture(
                Identifier.of("opium", "textures/gayassbackground.png"),
                0, 0, 0, 0,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight()
        );
    }
}
