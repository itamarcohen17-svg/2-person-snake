import java.awt.*;

public class SnakeLinkedList {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private SnakeNodes head;
    private Direction  direction;
    private Color      color;
    private boolean    alive;

    /**
     * @param tailRight  true  → tail extends rightward  (use for LEFT-facing snake)
     *                   false → tail extends leftward   (use for RIGHT-facing snake)
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

    // ── Body points – used by Food.respawn to avoid spawning on snake ─
    public java.util.List<Point> getBodyPoints() {
        java.util.List<Point> pts = new java.util.ArrayList<>();
        SnakeNodes t = head;
        while (t != null) { pts.add(t.getPosition()); t = t.getNext(); }
        return pts;
    }

    // ── Grow ──────────────────────────────────────────────────────
    public void grow() {
        addNodeAtTail(new Point(getTail().getPosition()));
    }

    // ── Move ──────────────────────────────────────────────────────
    public void move() {
        if (!alive) return;
        Point newHead = new Point(head.getPosition());
        switch (direction) {
            case UP:    newHead.y--; break;
            case DOWN:  newHead.y++; break;
            case LEFT:  newHead.x--; break;
            case RIGHT: newHead.x++; break;
        }
        // Wrap-around
        newHead.x = (newHead.x + GameConfig.GRID_SIZE) % GameConfig.GRID_SIZE;
        newHead.y = (newHead.y + GameConfig.GRID_SIZE) % GameConfig.GRID_SIZE;

        SnakeNodes n = new SnakeNodes(color, newHead);
        n.setNext(head);
        head = n;
        removeTail();
    }

    // ── Draw – cs/ox/oy passed in from GamePanel ──────────────────
    public void draw(Graphics g, int cs, int ox, int oy) {
        if (head == null) return;
        // Draw body first (head is drawn on top)
        SnakeNodes temp = head.getNext();
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
        // Draw head on top
        drawHead(g, cs, ox, oy);
    }

    /** Draws the head with two white 'eyes' positioned according to movement direction. */
    private void drawHead(Graphics g, int cs, int ox, int oy) {
        Point p  = head.getPosition();
        int   px = ox + p.x * cs;
        int   py = oy + p.y * cs;

        // Base fill – same as body
        g.setColor(color);
        g.fillRect(px + 1, py + 1, cs - 2, cs - 2);
        g.setColor(color.darker());
        g.drawRect(px + 1, py + 1, cs - 2, cs - 2);

        // Eye size and positions depend on direction
        int eyeSize = Math.max(2, cs / 5);
        int mid     = cs / 2;
        int near    = cs / 4;          // distance from the leading edge
        int spread  = cs / 4;          // distance from centre axis

        int eye1x, eye1y, eye2x, eye2y;
        switch (direction) {
            case RIGHT:
                eye1x = px + cs - near - eyeSize;  eye1y = py + mid - spread;
                eye2x = px + cs - near - eyeSize;  eye2y = py + mid + spread - eyeSize;
                break;
            case LEFT:
                eye1x = px + near;  eye1y = py + mid - spread;
                eye2x = px + near;  eye2y = py + mid + spread - eyeSize;
                break;
            case UP:
                eye1x = px + mid - spread;          eye1y = py + near;
                eye2x = px + mid + spread - eyeSize; eye2y = py + near;
                break;
            default: // DOWN
                eye1x = px + mid - spread;            eye1y = py + cs - near - eyeSize;
                eye2x = px + mid + spread - eyeSize;  eye2y = py + cs - near - eyeSize;
                break;
        }

        g.setColor(Color.WHITE);
        g.fillOval(eye1x, eye1y, eyeSize, eyeSize);
        g.fillOval(eye2x, eye2y, eyeSize, eyeSize);
    }

    // ── Collision checks ──────────────────────────────────────────
    public boolean collidesWithSelf() {
        Point hp   = head.getPosition();
        SnakeNodes t = head.getNext();
        while (t != null) { if (t.getPosition().equals(hp)) return true; t = t.getNext(); }
        return false;
    }

    public boolean collidesWithOther(SnakeLinkedList other) {
        Point      myHead = head.getPosition();
        SnakeNodes t      = (other.head != null) ? other.head.getNext() : null;
        while (t != null) { if (t.getPosition().equals(myHead)) return true; t = t.getNext(); }
        return false;
    }

    public boolean headOnHead(SnakeLinkedList other) {
        return head.getPosition().equals(other.head.getPosition());
    }

    public boolean eats(Point foodPos) {
        return head.getPosition().equals(foodPos);
    }

    // ── Direction ─────────────────────────────────────────────────
    public void setDirection(Direction d) {
        boolean illegal =
                (d == Direction.UP    && direction == Direction.DOWN)  ||
                        (d == Direction.DOWN  && direction == Direction.UP)    ||
                        (d == Direction.LEFT  && direction == Direction.RIGHT) ||
                        (d == Direction.RIGHT && direction == Direction.LEFT);
        if (!illegal) direction = d;
    }

    // ── Colour ────────────────────────────────────────────────────
    public void changeColor(Color c) {
        this.color = c;
        SnakeNodes t = head;
        while (t != null) { t.setData(c); t = t.getNext(); }
    }

    // ── State ─────────────────────────────────────────────────────
    public boolean   isAlive()        { return alive; }
    public void      kill()           { alive = false; }
    public Point     getHeadPosition(){ return head.getPosition(); }
    public Direction getDirection()   { return direction; }
    public Color     getColor()       { return color; }

    // ── Private helpers ───────────────────────────────────────────
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
    public int getSize(){
        int size = 0;
        SnakeNodes t = head;
        while (t != null) { size++; t = t.getNext(); }
        return size;
    }
}