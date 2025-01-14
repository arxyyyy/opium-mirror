package we.devs.opium.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
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

    // Track if we are in a world or not
    private static boolean isInWorld = false;

    @Inject(method = "init()V", at = @At("TAIL"))
    void init(CallbackInfo ci) {
        // Stop any current music before playing the custom one
        mc.getMusicTracker().stop();

        // Play music only if we are returning from a world
        if (!isInWorld && !isPlayingCustomMusic) {
            isPlayingCustomMusic = true;
            SoundInstance musicInstance = PositionedSoundInstance.music(SoundEvent.of(CUSTOM_MUSIC));
            mc.getSoundManager().play(musicInstance);
        }
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

    // Hook into MinecraftClient to track when entering or leaving a world
    @Inject(method = "setWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"))
    private void onWorldSet(ClientWorld clientWorld, CallbackInfo ci) {
        // When clientWorld is null, the player is leaving the world (coming back to title screen)
        if (clientWorld == null) {
            isInWorld = false; // Player is leaving the world
            isPlayingCustomMusic = false; // Allow music to restart on next title screen load
        } else {
            isInWorld = true; // Player is entering a world, stop custom music
            mc.getMusicTracker().stop(); // Stop the custom music if entering a world
        }
    }
}
