import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * All clips pre-loaded at startup.
 * EAT uses a pool of 4 clips to handle rapid consecutive plays without delay.
 * Theme is pre-loaded and starts instantly via setFramePosition(0).
 */
public class SoundManager {

    // ───── Settings ───────────────────────────────────────────────
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private boolean sfxEnabled   = true;

    // ───── File paths ─────────────────────────────────────────────
    private static final String THEME  = "C:/Users/itama/IdeaProjects/snake_project/src/theme song.wav";
    private static final String EAT    = "C:/Users/itama/IdeaProjects/snake_project/src/eat effect.wav";
    private static final String LOSE   = "C:/Users/itama/IdeaProjects/snake_project/src/lose effect.wav";
    private static final String START  = "C:/Users/itama/IdeaProjects/snake_project/src/start effect.wav";
    private static final String RECORD = "C:/Users/itama/IdeaProjects/snake_project/src/record effect.wav";

    // ───── SFX clips ──────────────────────────────────────────────
    // EAT pool: 4 clips so rapid eating never blocks
    private static final int EAT_POOL = 4;
    private Clip[] eatPool;
    private int    eatIndex = 0;

    private Clip startClip;
    private Clip loseClip;
    private Clip recordClip;

    // ───── Music ──────────────────────────────────────────────────
    private Clip musicClip = null;

    // ───── Constructor: pre-load everything ───────────────────────
    public SoundManager() {
        // EAT pool
        eatPool = new Clip[EAT_POOL];
        for (int i = 0; i < EAT_POOL; i++) eatPool[i] = loadClip(EAT);

        startClip  = loadClip(START);
        loseClip   = loadClip(LOSE);
        recordClip = loadClip(RECORD);

        // Pre-load theme into musicClip so first play has no disk delay
        musicClip = loadClip(THEME);
    }

    // ───── Settings ───────────────────────────────────────────────
    public void setSound(boolean on) { soundEnabled = on; if (!on) stopMusic(); }
    public void setMusic(boolean on) { musicEnabled = on; if (!on) stopMusic(); }
    public void setSfx(boolean on)   { sfxEnabled = on; }

    // ───── Music ──────────────────────────────────────────────────
    public void playTheme() {
        if (!soundEnabled || !musicEnabled) return;
        if (musicClip == null) musicClip = loadClip(THEME);
        if (musicClip == null) return;
        musicClip.stop();
        musicClip.setFramePosition(0);
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (musicClip != null) musicClip.stop();
    }

    // ───── SFX ────────────────────────────────────────────────────
    public void playEat() {
        if (!soundEnabled || !sfxEnabled) return;
        Clip clip = eatPool[eatIndex % EAT_POOL];
        eatIndex++;
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void playStart() { playSfxClip(startClip); }

    public void playLose(boolean isNewRecord) {
        playSfxClip(isNewRecord ? recordClip : loseClip);
    }

    // ───── Internal ───────────────────────────────────────────────
    private void playSfxClip(Clip clip) {
        if (!soundEnabled || !sfxEnabled || clip == null) return;
        clip.setFramePosition(0);
        clip.start();
    }

    private Clip loadClip(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) { System.err.println("Audio not found: " + path); return null; }
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Audio error [" + path + "]: " + e.getMessage());
            return null;
        }
    }
}