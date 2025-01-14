package we.devs.opium.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import we.devs.opium.api.utilities.IMinecraft;
import we.devs.opium.api.manager.music.MusicStateManager;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin implements IMinecraft {

    @Inject(method = "init()V", at = @At("TAIL"))
    void init(CallbackInfo ci) {
        // Erhalte den Musik-Tracker
        MusicTracker musicTracker = mc.getMusicTracker();

        // Stoppe alle Standard-Minecraft-Musiktitel
        musicTracker.stop();

        // Prüfe, ob die benutzerdefinierte Musik bereits läuft
        if (!MusicStateManager.isPlayingCustomMusic()) {
            MusicStateManager.setPlayingCustomMusic(true); // Setze das Flag
            playNextTrack();
        }
    }

    private void playNextTrack() {
        // Wähle einen zufälligen Song aus
        Identifier randomMusic = MusicStateManager.getRandomMusicTrack();

        // Erstelle den SoundInstance
        SoundInstance musicInstance = PositionedSoundInstance.music(SoundEvent.of(randomMusic));

        // Speichere die aktuelle Instanz und starte sie
        MusicStateManager.setCurrentSong(musicInstance);
        mc.getSoundManager().play(musicInstance);
    }

    @Inject(method = "removed()V", at = @At("HEAD"))
    void removed(CallbackInfo ci) {
        // Behalte den Musikstatus, wenn die Titelseite verlassen wird
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Überprüfe, ob der aktuelle Song noch läuft
        SoundInstance currentSong = MusicStateManager.getCurrentSongInstance();
        if (currentSong != null && !mc.getSoundManager().isPlaying(currentSong)) {
            playNextTrack(); // Starte den nächsten Song
        }

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