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
        // get the Music Tracker
        MusicTracker musicTracker = mc.getMusicTracker();

        // stop all base minecraft music
        musicTracker.stop();

        // Check if the user defined music is already playing
        if (!MusicStateManager.isPlayingCustomMusic()) {
            MusicStateManager.setPlayingCustomMusic(true); // set the flag
            playNextTrack();
        }
    }

    private void playNextTrack() {
        // Choose a random music track
        Identifier randomMusic = MusicStateManager.getRandomMusicTrack();

        // Create the music instance
        SoundInstance musicInstance = PositionedSoundInstance.music(SoundEvent.of(randomMusic));

        // Safe the current song instance and start it
        MusicStateManager.setCurrentSong(musicInstance);
        mc.getSoundManager().play(musicInstance);
    }

    @Inject(method = "removed()V", at = @At("HEAD"))
    void removed(CallbackInfo ci) {
        // keep the current music status if the main menu is left
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // check if the current song is still playing
        SoundInstance currentSong = MusicStateManager.getCurrentSongInstance();
        if (currentSong != null && !mc.getSoundManager().isPlaying(currentSong)) {
            playNextTrack(); // Start the next track
        }

        // Draw the current track information on the left lower corner
        if (currentSong != null) {
            String baseText = "Playing: ";
            String trackName = currentSong.getId().getPath().replaceAll("_", " ");
            String fullText = baseText + trackName; // Whole Text "Playing: <Trackname>"
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();
            int x = 2; // left lower corner
            int y = screenHeight - 20;

            // Draw the base text
            context.drawTextWithShadow(mc.textRenderer, fullText, x, y, 0xFF808080);

            // add Glint effect to the text
            long time = System.currentTimeMillis();
            int charIndex = (int) ((time / 100) % fullText.length());
            int glintColor = 0xFFFFFF;

            for (int i = 0; i < 4; i++) { // highlight 4 characters
                int currentIndex = (charIndex + i) % fullText.length();
                int glintCharX = x + mc.textRenderer.getWidth(fullText.substring(0, currentIndex));
                context.drawTextWithShadow(mc.textRenderer, String.valueOf(fullText.charAt(currentIndex)), glintCharX, y, glintColor);
            }
        }

        // Optional: Draw the background image
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