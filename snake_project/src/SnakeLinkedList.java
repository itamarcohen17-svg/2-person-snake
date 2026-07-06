import java.awt.*;

/**
 * הנחש עצמו – רשימה מקושרת של צמתים.
 * אחראי על: תנועה, גדילה, בדיקות התנגשות, כיוון וצבע.
 * הכיוונים מוגדרים ב-Direction (מחלקה נפרדת).
 * ציור הנחש מבוצע על-ידי SnakeRenderer.
 */
public class SnakeLinkedList {

    private SnakeNodes head;
    private Direction  direction;
    private Color      color;
    private boolean    alive;

    /**
     * @param tailRight  true  → הזנב משתרע ימינה  (לנחש שמתחיל פונה שמאלה)
     *                   false → הזנב משתרע שמאלה  (לנחש שמתחיל פונה ימינה)
     */
    public SnakeLinkedList(Color color, Direction startDir,
                           int startCol, int startRow, boolean tailRight) {
        this.color     = color;
        this.direction = startDir;
        this.alive     = true;
        this.head      = null;

        int step = tailRight ? 1 : -1;
        for (int i = 0; i < 3; i++)
            addNodeAtTail(new Point(startCol + step * i, startRow));
    }

    // ── נקודות הגוף – נדרש ל-Food.respawn ───────────────────────
    public java.util.List<Point> getBodyPoints() {
        java.util.List<Point> pts = new java.util.ArrayList<>();
        SnakeNodes t = head;
        while (t != null) { pts.add(t.getPosition()); t = t.getNext(); }
        return pts;
    }

    // ── גדילה ────────────────────────────────────────────────────
    public void grow() {
        addNodeAtTail(new Point(getTail().getPosition()));
    }

    // ── תנועה ────────────────────────────────────────────────────
    public void move() {
        if (!alive) return;

        // הראש החדש = הראש הישן + צעד בכיוון הנוכחי
        Point newHead = new Point(head.getPosition());
        newHead.x += direction.dx();
        newHead.y += direction.dy();

        // Wrap-around – עטיפה בקצוות הלוח
        newHead.x = (newHead.x + GameConfig.GRID_SIZE) % GameConfig.GRID_SIZE;
        newHead.y = (newHead.y + GameConfig.GRID_SIZE) % GameConfig.GRID_SIZE;

        SnakeNodes n = new SnakeNodes(color, newHead);
        n.setNext(head);
        head = n;
        removeTail();
    }

    // ── בדיקות התנגשות ───────────────────────────────────────────
    /** בדיקה אם הראש פוגע בגוף של עצמו */
    public boolean collidesWithSelf() {
        Point      hp = head.getPosition();
        SnakeNodes t  = head.getNext();
        while (t != null) {
            if (t.getPosition().equals(hp)) return true;
            t = t.getNext();
        }
        return false;
    }

    /** בדיקה אם הראש שלי פוגע בגוף של נחש אחר */
    public boolean collidesWithOther(SnakeLinkedList other) {
        Point      myHead = head.getPosition();
        SnakeNodes t      = (other.head != null) ? other.head.getNext() : null;
        while (t != null) {
            if (t.getPosition().equals(myHead)) return true;
            t = t.getNext();
        }
        return false;
    }

    /** בדיקה אם שני ראשים נפגשים באותו תא */
    public boolean headOnHead(SnakeLinkedList other) {
        return head.getPosition().equals(other.head.getPosition());
    }

    /** בדיקה אם הראש נמצא על האוכל */
    public boolean eats(Point foodPos) {
        return head.getPosition().equals(foodPos);
    }

    // ── כיוון ────────────────────────────────────────────────────
    /** מגדיר כיוון חדש – מונע היפוך ישיר בעזרת Direction.opposite() */
    public void setDirection(Direction d) {
        if (d != direction.opposite()) direction = d;
    }

    // ── צבע ──────────────────────────────────────────────────────
    /** שינוי צבע – מעדכן את כל הצמתים */
    public void changeColor(Color c) {
        this.color = c;
        SnakeNodes t = head;
        while (t != null) { t.setData(c); t = t.getNext(); }
    }

    // ── מצב ──────────────────────────────────────────────────────
    public boolean    isAlive()         { return alive; }
    public void       kill()            { alive = false; }
    public Point      getHeadPosition() { return head.getPosition(); }
    public Direction  getDirection()    { return direction; }
    public Color      getColor()        { return color; }
    public SnakeNodes getHead()         { return head; }

    public int getSize() {
        int size = 0;
        SnakeNodes t = head;
        while (t != null) { size++; t = t.getNext(); }
        return size;
    }

    // ── עזר פנימי ────────────────────────────────────────────────
    private void addNodeAtTail(Point pos) {
        SnakeNodes n = new SnakeNodes(color, pos);
        if (head == null) { head = n; return; }
        SnakeNodes t = head;
        while (t.getNext() != null) t = t.getNext();
        t.setNext(n);
    }

    private SnakeNodes getTail() {
        SnakeNodes t = head;
        while (t.getNext() != null) t = t.getNext();
        return t;
    }

    private void removeTail() {
        if (head == null || head.getNext() == null) { head = null; return; }
        SnakeNodes t = head;
        while (t.getNext().getNext() != null) t = t.getNext();
        t.setNext(null);
    }
}