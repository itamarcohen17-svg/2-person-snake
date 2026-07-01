import java.awt.*;
public class SnakeNodes {
    private Color     data;
    private Point     position;   // מיקום על הגריד (עמודה, שורה)
    private SnakeNodes next;

    public SnakeNodes(Color data, Point position) {
        this.data     = data;
        this.position = new Point(position);
        this.next     = null;           // תוקן: היה this.next = next (באג!)
    }
    // ── getters / setters ──────────────────────────────────────
    public Color      getData()           { return data; }
    public void       setData(Color data) { this.data = data; }

    public Point      getPosition()                { return position; }
    public void       setPosition(Point position)  { this.position = new Point(position); }

    public SnakeNodes getNext()                    { return next; }
    public void       setNext(SnakeNodes next)     { this.next = next; }
}

// ── מקשי שחקן 1 ──   ── מקשי שחקן 2 ──
// חץ למעלה  = UP        W = UP
// חץ למטה   = DOWN      S = DOWN
// חץ שמאל   = LEFT      A = LEFT
// חץ ימין   = RIGHT     D = RIGHT
//movement:
//right arrow=right;
//left arrow=left;
//up arrow=up;
//down arrow=down;

//movement:
//a botton=left
//d botton=right
//w botton=up
//s botton=down

