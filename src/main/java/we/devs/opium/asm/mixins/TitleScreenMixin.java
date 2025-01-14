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

        // Zeichne die aktuelle Track-Info unten in der Ecke
        if (currentSong != null) {
            String baseText = "Playing: ";
            String trackName = currentSong.getId().getPath().replaceAll("_", " ");
            String fullText = baseText + trackName; // Gesamter Text "Playing: <Trackname>"
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();
            int x = 2; // Links unten
            int y = screenHeight - 20;

            // Zeichne den Basis-Text
            context.drawTextWithShadow(mc.textRenderer, fullText, x, y, 0xFF808080);

            // Füge Glint-Effekt hinzu
            long time = System.currentTimeMillis();
            int charIndex = (int) ((time / 100) % fullText.length());
            int glintColor = 0xFFFFFF;

            for (int i = 0; i < 4; i++) { // 4 Charaktere hervorheben
                int currentIndex = (charIndex + i) % fullText.length();
                int glintCharX = x + mc.textRenderer.getWidth(fullText.substring(0, currentIndex));
                context.drawTextWithShadow(mc.textRenderer, String.valueOf(fullText.charAt(currentIndex)), glintCharX, y, glintColor);
            }
        }

        // Optional: Hintergrundtextur darstellen
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