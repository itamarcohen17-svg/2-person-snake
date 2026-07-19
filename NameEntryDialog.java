/**
 * מצב חלונית הזנת השם – שכבה מצוירת בתוך המשחק (במקום JOptionPane).
 *
 * המחלקה שומרת רק את המצב: האם החלונית פתוחה, לאיזה שחקן,
 * מה הוקלד עד כה, והאם בסיום צריך להמשיך להתחלת משחק.
 * הציור נעשה ב-NameEntryScreen, והקלט מגיע מ-KeyboardHandler/Handler_Mouse.
 */
public class NameEntryDialog {

    /** אורך שם מקסימלי – שומר על תצוגה נקייה */
    public static final int MAX_LENGTH = 12;

    private boolean       active           = false;
    private int           player;                    // 1 או 2
    private GameMode      pendingStartMode = null;   // אם לא null – בסיום ממשיכים להתחלת משחק
    private StringBuilder text             = new StringBuilder();

    // ── פתיחה ────────────────────────────────────────────────────
    /** פתיחה מתוך מסך ההגדרות – בסיום פשוט נסגרת */
    public void open(int player, String currentName) {
        this.active           = true;
        this.player           = player;
        this.pendingStartMode = null;
        this.text             = new StringBuilder(currentName);
    }

    /** פתיחה לפני תחילת משחק – בסיום ממשיכים בתהליך ההתחלה */
    public void openForGameStart(int player, GameMode mode) {
        open(player, "");
        this.pendingStartMode = mode;
    }

    // ── סגירה ────────────────────────────────────────────────────
    public void close() {
        active           = false;
        pendingStartMode = null;
        text             = new StringBuilder();
    }

    // ── קלט הקלדה ────────────────────────────────────────────────
    /** מוסיף תו אם הוא אות/ספרה/רווח ולא חרגנו מהאורך; מחזיר אם השתנה */
    public boolean typeChar(char c) {
        if (!active) return false;
        if (text.length() >= MAX_LENGTH) return false;
        if (!Character.isLetterOrDigit(c) && c != ' ') return false;
        text.append(c);
        return true;
    }

    /** מוחק את התו האחרון */
    public void backspace() {
        if (active && text.length() > 0)
            text.deleteCharAt(text.length() - 1);
    }

    // ── Getters ──────────────────────────────────────────────────
    public boolean  isActive()            { return active; }
    public int      getPlayer()           { return player; }
    public GameMode getPendingStartMode() { return pendingStartMode; }
    public String   getText()             { return text.toString().trim(); }
}
