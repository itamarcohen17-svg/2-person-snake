import java.awt.Color;

/**
 * הגדרות המשחק: צבעי הנחשים ומצבי הצליל.
 * הופרד מ-GamePanel; כל שינוי צליל מוחל ישירות על SoundManager.
 */
public class GameSettings {

    private Color snake1Color = Color.GREEN;
    private Color snake2Color = Color.CYAN;

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private boolean sfxEnabled   = true;

    private final SoundManager sound;

    public GameSettings(SoundManager sound) {
        this.sound = sound;
    }

    // ── צבעים ────────────────────────────────────────────────────
    /** מנסה לקבוע צבע לשחקן 1; נכשל אם הצבע תפוס על-ידי שחקן 2 */
    public boolean trySetSnake1Color(Color c) {
        if (c.equals(snake2Color)) return false;
        snake1Color = c;
        return true;
    }

    /** מנסה לקבוע צבע לשחקן 2; נכשל אם הצבע תפוס על-ידי שחקן 1 */
    public boolean trySetSnake2Color(Color c) {
        if (c.equals(snake1Color)) return false;
        snake2Color = c;
        return true;
    }

    public Color getSnake1Color() { return snake1Color; }
    public Color getSnake2Color() { return snake2Color; }

    // ── צליל ─────────────────────────────────────────────────────
    /** Master: הפעלה/כיבוי כללי – גורר איתו את Music ו-SFX */
    public void toggleMaster() {
        soundEnabled = !soundEnabled;
        musicEnabled = soundEnabled;
        sfxEnabled   = soundEnabled;
        sound.setSound(soundEnabled);
        sound.setMusic(musicEnabled);
        sound.setSfx(sfxEnabled);
    }

    /** Music: פועל רק כאשר Master דלוק */
    public boolean toggleMusic() {
        if (!soundEnabled) return false;
        musicEnabled = !musicEnabled;
        sound.setMusic(musicEnabled);
        return true;
    }

    /** SFX: פועל רק כאשר Master דלוק */
    public boolean toggleSfx() {
        if (!soundEnabled) return false;
        sfxEnabled = !sfxEnabled;
        sound.setSfx(sfxEnabled);
        return true;
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSfxEnabled()   { return sfxEnabled; }
}
