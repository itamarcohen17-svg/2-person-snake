/**
 * מצבי המשחק – מחלקה עם קבועים בלבד (לא enum).
 */
public class GameMode {

    public static final GameMode SINGLE = new GameMode("SINGLE");  // שחקן יחיד – ניקוד וטבלת שיאים
    public static final GameMode VS     = new GameMode("VS");      // שני שחקנים – טיימר 3 דקות

    private final String name;

    private GameMode(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}

