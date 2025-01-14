package we.devs.opium.api.manager.music;

public class MusicStateManager {
    private static boolean isPlayingCustomMusic = false;

    public static boolean isPlayingCustomMusic() {
        return isPlayingCustomMusic;
    }

    public static void setPlayingCustomMusic(boolean playing) {
        isPlayingCustomMusic = playing;
    }
}