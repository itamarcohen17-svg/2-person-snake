/**
 * המסכים במשחק – מחלקה עם קבועים בלבד (לא enum).
 * הבנאי private ולכן קיימים בדיוק 5 מסכים; השוואה עם == בטוחה.
 */
public class Screen {

    public static final Screen MAIN_MENU   = new Screen("MAIN_MENU");    // תפריט ראשי
    public static final Screen SETTINGS    = new Screen("SETTINGS");     // הגדרות (צבעים + צליל)
    public static final Screen HIGH_SCORES = new Screen("HIGH_SCORES");  // טבלת שיאים
    public static final Screen PLAYING     = new Screen("PLAYING");      // משחק פעיל (Single או VS)
    public static final Screen PAUSE_MENU  = new Screen("PAUSE_MENU");   // תפריט עצירה בתוך משחק

    private final String name;

    private Screen(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
