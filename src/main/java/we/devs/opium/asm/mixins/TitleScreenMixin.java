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

    // Identifier for your custom music file
    private static final Identifier CUSTOM_MUSIC = Identifier.of("opium", "custom_music");
    private static boolean isPlayingCustomMusic = false;

    @Inject(method = "init()V", at = @At("TAIL"))
    void init(CallbackInfo ci) {
        // Stop the default music system
        mc.getMusicTracker().stop();

        // Ensure custom music is not already playing
        if (!isPlayingCustomMusic) {
            isPlayingCustomMusic = true; // Set flag to avoid multiple instances
            SoundInstance musicInstance = PositionedSoundInstance.music(SoundEvent.of(CUSTOM_MUSIC));
            mc.getSoundManager().play(musicInstance);
        }
    }

    @Inject(method = "removed()V", at = @At("HEAD"))
    void removed(CallbackInfo ci) {
        // Reset the flag when leaving the title screen
        isPlayingCustomMusic = false;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Custom background rendering
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
