import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Food {
    public enum Type { APPLE, PEAR }

    private Point        position;
    private Type         type;
    private final Random rng   = new Random();
    private ImageIcon    apple = new ImageIcon("apple.png");
    private ImageIcon    pear  = new ImageIcon("pear.png");

    public Food() { respawn(null); }

    //פונקציה לחידוש פירות
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

    public Point getPosition() { return position; }
    public Type  getType()     { return type; }

    // cs/ox/oy passed in from GamePanel – same pattern as SnakeLinkedList
    //פונקציית ציור
    public void draw(Graphics g, int cs, int ox, int oy) {
        int px  = ox + position.x * cs;
        int py  = oy + position.y * cs;
        Image img = (type == Type.APPLE) ? apple.getImage() : pear.getImage();
        g.drawImage(img, px + 2, py + 2, cs - 4, cs - 4, null);
    }
}