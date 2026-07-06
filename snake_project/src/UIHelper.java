import java.awt.*;

/**
 * כלי ציור משותפים לכל המסכים.
 * כל מסך משתמש בפונקציות אלו במקום לשכפל קוד ציור.
 */
public class UIHelper {

    /** מצייר כפתור עגלגל עם טקסט מרוכז */
    public static void drawButton(Graphics g, Rectangle r, String text, Color c) {
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        g.setColor(c);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);

        int fs = Math.max(12, r.height / 3);
        g.setFont(new Font("Arial", Font.BOLD, fs));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text,
                r.x + (r.width  - fm.stringWidth(text)) / 2,
                r.y + (r.height + fm.getAscent())  / 2 - 4);
    }

    /** מצייר כפתור ON/OFF עם אפשרות להשבית (אפור) */
    public static void drawToggle(Graphics g, Rectangle r, String label,
                                  boolean on, boolean enabled) {
        Color bg  = !enabled ? Color.DARK_GRAY
                : (on ? new Color(0, 140, 0) : new Color(120, 0, 0));
        Color fg  = enabled ? Color.WHITE : Color.GRAY;
        String txt = label + ": " + (on ? "ON" : "OFF");

        g.setColor(bg);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(enabled ? (on ? Color.GREEN : Color.RED) : Color.GRAY);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        int fs = Math.max(11, r.height / 3);
        g.setFont(new Font("Arial", Font.BOLD, fs));
        g.setColor(fg);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(txt,
                r.x + (r.width  - fm.stringWidth(txt)) / 2,
                r.y + (r.height + fm.getAscent())  / 2 - 4);
    }

    /** מצייר ריבוע צבע (swatch) עם סימון בחירה ו-X אם אסור */
    public static void drawSwatch(Graphics g, Rectangle r, Color c,
                                  boolean selected, boolean forbidden) {
        g.setColor(forbidden ? Color.DARK_GRAY : c);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);

        if (selected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 8, 8);
            g2.setStroke(new BasicStroke(1));
        }
        if (forbidden) {
            g.setColor(Color.RED);
            g.drawLine(r.x + 4,          r.y + 4,
                    r.x + r.width - 4, r.y + r.height - 4);
            g.drawLine(r.x + r.width - 4, r.y + 4,
                    r.x + 4,           r.y + r.height - 4);
        }
    }

    /** שתי כפתורי אישור YES/NO מרוכזים במרכז המסך */
    public static Rectangle[] confirmButtons(int panelW, int panelH,
                                             int btnW, int btnH) {
        int cx = panelW / 2, cy = panelH / 2;
        int bw = btnW / 2 - 10;
        return new Rectangle[] {
                new Rectangle(cx - bw - 10, cy + btnH, bw, btnH),   // YES
                new Rectangle(cx + 10,      cy + btnH, bw, btnH)    // NO
        };
    }
}