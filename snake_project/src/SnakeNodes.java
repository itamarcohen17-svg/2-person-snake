import java.awt.*;

// צומת בודד ברשימה המקושרת של הנחש – שומר צבע, מיקום ומצביע לצומת הבא
public class SnakeNodes {

    private Color      data;
    private Point      position;
    private SnakeNodes next;
    //─────────── בנאי אתחול ────────────
    public SnakeNodes(Color data, Point position) {
        this.data     = data;
        this.position = new Point(position);
        this.next     = null;
    }
    // ── Getters / Setters ─────────────────────────────────────────
    public Color      getData()                    { return data; }
    public void       setData(Color data)          { this.data = data; }

    public Point      getPosition()                { return position; }
    public void       setPosition(Point position)  { this.position = new Point(position); }

    public SnakeNodes getNext()                    { return next; }
    public void       setNext(SnakeNodes next)     { this.next = next; }
}