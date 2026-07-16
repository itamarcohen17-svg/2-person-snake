import java.awt.*;
import java.util.Random;

/**
 * מנהל את האוכל: סוג (תפוח / אגס) ומיקום על הגריד.
 * ציור האוכל מבוצע על-ידי FoodRenderer.
 */
public class Food {

    /** סוגי האוכל – מחלקה מקוננת עם קבועים בלבד */
    public static class Type {
        public static final Type APPLE = new Type("APPLE");
        public static final Type PEAR  = new Type("PEAR");

        private final String name;
        private Type(String name) { this.name = name; }

        @Override
        public String toString() { return name; }
    }

    private Point        position;
    private Type         type;
    private final Random rng = new Random();

    public Food() { respawn(null); }

    /** מחדש מיקום וסוג – מבטיח שלא ייצא על תאים תפוסים */
    public void respawn(java.util.List<Point> occupied) {
        type = rng.nextBoolean() ? Type.APPLE : Type.PEAR;
        Point p;
        do {
            int x = rng.nextInt(GameConfig.GRID_SIZE);
            int y = rng.nextInt(GameConfig.GRID_SIZE);
            p = new Point(x, y);
        } while (occupied != null && occupied.contains(p));
        position = p;
    }

    // ── Getters ───────────────────────────────────────────────────
    public Point getPosition() { return position; }
    public Type  getType()     { return type; }
}
