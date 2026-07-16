import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
/**
 * מנהל את כל הצלילים במשחק.
 * כל הקליפים נטענים מראש בהפעלה.
 * EAT משתמש בבריכת 4 קליפים כדי לאפשר ניגון מהיר ורצוף ללא עיכוב.
 */
public class SoundManager {

    // ───── הגדרות פעיל/כבוי ───────────────────────────────────────
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private boolean sfxEnabled   = true;

    // ───── נתיבי קבצי שמע ────────────────────────────────────────
    private static final String THEME  ="C:/Users/itama/IdeaProjects/2-person-snake/theme song.wav" ;
    private static final String EAT    = "C:/Users/itama/IdeaProjects/2-person-snake/eat effect.wav";
    private static final String LOSE   = "C:/Users/itama/IdeaProjects/2-person-snake/lose effect.wav";
    private static final String START  = "C:/Users/itama/IdeaProjects/2-person-snake/start effect.wav";
    private static final String RECORD = "C:/Users/itama/IdeaProjects/2-person-snake/record effect.wav";

    // ───── בריכת קליפי אכילה ─────────────────────────────────────
    private static final int EAT_POOL = 4;
    private Clip[] eatPool;
    private int    eatIndex = 0;

    // ───── קליפי אפקטים ──────────────────────────────────────────
    private Clip startClip;
    private Clip loseClip;
    private Clip recordClip;

    // ───── מוזיקת רקע ─────────────────────────────────────────────
    private Clip musicClip = null;

    // ───── בנאי: טעינה מוקדמת של הכל ────────────────────────────
    public SoundManager() {
        eatPool = new Clip[EAT_POOL];
        for (int i = 0; i < EAT_POOL; i++) eatPool[i] = loadClip(EAT);

        startClip  = loadClip(START);
        loseClip   = loadClip(LOSE);
        recordClip = loadClip(RECORD);
        musicClip  = loadClip(THEME);
    }

    // ───── הגדרות ─────────────────────────────────────────────────
    public void setSound(boolean on) { soundEnabled = on; if (!on) stopMusic(); }
    public void setMusic(boolean on) { musicEnabled = on; if (!on) stopMusic(); }
    public void setSfx(boolean on)   { sfxEnabled   = on; }

    // ───── מוזיקת רקע ─────────────────────────────────────────────
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

    // ───── אפקטי שמע ─────────────────────────────────────────────
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

    // ───── עזר פנימי ──────────────────────────────────────────────
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