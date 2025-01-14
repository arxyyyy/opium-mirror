package we.devs.opium.api.manager.music;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;

public class MusicStateManager {
    private static boolean isPlayingCustomMusic = false;
    private static final List<Identifier> CUSTOM_MUSIC_TRACKS = List.of(
            Identifier.of("opium", "kencarson_overseas"),
            Identifier.of("opium", "kencarson_ss"),
            Identifier.of("opium", "playboicarti_24songs"),
            Identifier.of("opium", "playboicarti_eviljordan")
    );
    private static final Random RANDOM = new Random();

    public static boolean isPlayingCustomMusic() {
        return isPlayingCustomMusic;
    }

    public static void setPlayingCustomMusic(boolean playing) {
        isPlayingCustomMusic = playing;
    }

    public static Identifier getRandomMusicTrack() {
        // Wähle einen zufälligen Track aus der Liste aus
        return CUSTOM_MUSIC_TRACKS.get(RANDOM.nextInt(CUSTOM_MUSIC_TRACKS.size()));
    }
}