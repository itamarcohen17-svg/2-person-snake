import java.awt.event.KeyEvent;

/**
 * כיווני תנועה של הנחש – מחלקה עם 4 קבועים בלבד.
 *
 * הבנאי private, ולכן אי אפשר ליצור כיוונים חדשים מבחוץ.
 * מכיוון שקיימים רק 4 מופעים קבועים, השוואה עם == עובדת בבטחה
 * (כמו: if (dir == Direction.UP)).
 */
public class Direction {

    // ── 4 הכיוונים היחידים שקיימים ───────────────────────────────
    public static final Direction UP    = new Direction( 0, -1, "UP");
    public static final Direction DOWN  = new Direction( 0,  1, "DOWN");
    public static final Direction LEFT  = new Direction(-1,  0, "LEFT");
    public static final Direction RIGHT = new Direction( 1,  0, "RIGHT");

    // ── שדות ─────────────────────────────────────────────────────
    private final int    dx;     // שינוי בציר X בכל צעד
    private final int    dy;     // שינוי בציר Y בכל צעד
    private final String name;   // שם לתצוגה / דיבוג

    /** בנאי פרטי – מונע יצירת כיוונים נוספים מחוץ למחלקה */
    private Direction(int dx, int dy, String name) {
        this.dx   = dx;
        this.dy   = dy;
        this.name = name;
    }

    /** שינוי בציר X בכל צעד */
    public int dx() { return dx; }

    /** שינוי בציר Y בכל צעד */
    public int dy() { return dy; }

    /** הכיוון ההפוך – משמש למניעת היפוך ישיר של הנחש */
    public Direction opposite() {
        if (this == UP)    return DOWN;
        if (this == DOWN)  return UP;
        if (this == LEFT)  return RIGHT;
        return LEFT;   // RIGHT
    }

    /** ממפה מקשי חצים (שחקן 1) לכיוון; מחזיר null אם המקש לא רלוונטי */
    public static Direction fromArrowKey(int keyCode) {
        if (keyCode == KeyEvent.VK_UP)    return UP;
        if (keyCode == KeyEvent.VK_DOWN)  return DOWN;
        if (keyCode == KeyEvent.VK_LEFT)  return LEFT;
        if (keyCode == KeyEvent.VK_RIGHT) return RIGHT;
        return null;
    }

    /** ממפה מקשי WASD (שחקן 2) לכיוון; מחזיר null אם המקש לא רלוונטי */
    public static Direction fromWasdKey(int keyCode) {
        if (keyCode == KeyEvent.VK_W) return UP;
        if (keyCode == KeyEvent.VK_S) return DOWN;
        if (keyCode == KeyEvent.VK_A) return LEFT;
        if (keyCode == KeyEvent.VK_D) return RIGHT;
        return null;
    }

    @Override
    public String toString() { return name; }
}

