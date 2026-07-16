import java.awt.*;
import javax.swing.ImageIcon;

/**
 * אחראי על ציור האוכל בלבד.
 * הלוגיקה הועברה מ-Food כדי להפריד בין נתונים לציור.
 */
public class Renderer_Food {

    private final ImageIcon apple = new ImageIcon("apple.png");
    private final ImageIcon pear  = new ImageIcon("pear.png");

    /** מצייר את האוכל בתא המתאים על הלוח */
    public void draw(Graphics g, Food food, int cs, int ox, int oy) {
        Point p  = food.getPosition();
        int   px = ox + p.x * cs;
        int   py = oy + p.y * cs;

        Image img = (food.getType() == Food.Type.APPLE)
                ? apple.getImage()
                : pear.getImage();

        g.drawImage(img, px + 2, py + 2, cs - 4, cs - 4, null);
    }
}