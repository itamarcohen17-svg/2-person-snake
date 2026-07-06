import java.awt.*;

/**
 * אחראי על ציור הנחש בלבד.
 * מופרד מ-SnakeLinkedList כדי להפריד בין לוגיקה לציור.
 */
public class Renderer_Snake {

    /** מצייר נחש שלם: גוף ואחר-כך ראש עם עיניים */
    public static void draw(Graphics g, SnakeLinkedList snake, int cs, int ox, int oy) {
        if (snake.getHead() == null) return;
        drawBody(g, snake, cs, ox, oy);
        drawHead(g, snake, cs, ox, oy);
    }

    // ── ציור הגוף ────────────────────────────────────────────────
    private static void drawBody(Graphics g, SnakeLinkedList snake, int cs, int ox, int oy) {
        Color      color = snake.getColor();
        SnakeNodes temp  = snake.getHead().getNext();   // מדלג על הראש

        while (temp != null) {
            Point p  = temp.getPosition();
            int   px = ox + p.x * cs;
            int   py = oy + p.y * cs;

            g.setColor(color);
            g.fillRect(px + 1, py + 1, cs - 2, cs - 2);
            g.setColor(color.darker());
            g.drawRect(px + 1, py + 1, cs - 2, cs - 2);

            temp = temp.getNext();
        }
    }

    // ── ציור הראש עם עיניים ──────────────────────────────────────
    private static void drawHead(Graphics g, SnakeLinkedList snake, int cs, int ox, int oy) {
        Point p  = snake.getHeadPosition();
        Color c  = snake.getColor();
        int   px = ox + p.x * cs;
        int   py = oy + p.y * cs;

        // בסיס הראש – זהה לגוף
        g.setColor(c);
        g.fillRect(px + 1, py + 1, cs - 2, cs - 2);
        g.setColor(c.darker());
        g.drawRect(px + 1, py + 1, cs - 2, cs - 2);

        drawEyes(g, snake.getDirection(), px, py, cs);
    }

    // ── מיקום העיניים לפי כיוון ─────────────────────────────────
    private static void drawEyes(Graphics g, Direction dir, int px, int py, int cs) {
        int eyeSize = Math.max(2, cs / 5);
        int mid     = cs / 2;
        int near    = cs / 4;
        int spread  = cs / 4;

        int eye1x, eye1y, eye2x, eye2y;
        if (dir == Direction.RIGHT) {
            eye1x = px + cs - near - eyeSize;  eye1y = py + mid - spread;
            eye2x = px + cs - near - eyeSize;  eye2y = py + mid + spread - eyeSize;
        } else if (dir == Direction.LEFT) {
            eye1x = px + near;  eye1y = py + mid - spread;
            eye2x = px + near;  eye2y = py + mid + spread - eyeSize;
        } else if (dir == Direction.UP) {
            eye1x = px + mid - spread;           eye1y = py + near;
            eye2x = px + mid + spread - eyeSize; eye2y = py + near;
        } else { // DOWN
            eye1x = px + mid - spread;           eye1y = py + cs - near - eyeSize;
            eye2x = px + mid + spread - eyeSize; eye2y = py + cs - near - eyeSize;
        }

        g.setColor(Color.WHITE);
        g.fillOval(eye1x, eye1y, eyeSize, eyeSize);
        g.fillOval(eye2x, eye2y, eyeSize, eyeSize);
    }
}