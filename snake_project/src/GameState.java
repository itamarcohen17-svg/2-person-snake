/**
 * מצב המשחק הפעיל בתוך מסך PLAYING – מחלקה עם קבועים בלבד (לא enum).
 */
public class GameState {

    public static final GameState COUNTDOWN = new GameState("COUNTDOWN");  // ספירה לאחור 3-2-1
    public static final GameState RUNNING   = new GameState("RUNNING");    // המשחק רץ
    public static final GameState PAUSED    = new GameState("PAUSED");     // מושהה
    public static final GameState GAME_OVER = new GameState("GAME_OVER");  // המשחק הסתיים

    private final String name;

    private GameState(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
