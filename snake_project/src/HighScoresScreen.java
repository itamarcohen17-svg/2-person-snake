import java.awt.*;

/**
 * מסך טבלת השיאים.
 * אחראי על: ציור 5 השורות העליונות וכפתור חזרה.
 */
public class HighScoresScreen {

    private static final String[] ORDINALS   = { "1st", "2nd", "3rd", "4th", "5th" };
    private static final Color[]  ROW_COLORS = {
            Color.YELLOW, Color.LIGHT_GRAY, new Color(205, 127, 50),
            Color.WHITE,  Color.WHITE
    };

    // ── מיקום כפתור חזרה ─────────────────────────────────────────
    public Rectangle backButton(int panelW, int panelH, int btnW, int btnH) {
        return new Rectangle(panelW / 2 - btnW / 2, (int)(panelH * 0.82), btnW, btnH);
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int titleFont, int bodyFont, int btnW, int btnH,
                     int[] scores) {

        int cx = panelW / 2;

        // כותרת
        g.setFont(new Font("Arial", Font.BOLD, titleFont));
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        String title = "High Scores";
        g.drawString(title, cx - fm.stringWidth(title) / 2, panelH / 6);

        // רשימת ציונים
        int lineH  = Math.max(30, panelH / 12);
        int startY = panelH / 4;
        g.setFont(new Font("Arial", Font.BOLD, bodyFont + 2));
        for (int i = 0; i < 5; i++) {
            g.setColor(ROW_COLORS[i]);
            String line = ORDINALS[i] + "   " + (scores[i] == 0 ? "---" : scores[i]);
            fm = g.getFontMetrics();
            g.drawString(line, cx - fm.stringWidth(line) / 2, startY + i * lineH);
        }

        UIHelper.drawButton(g, backButton(panelW, panelH, btnW, btnH),
                "Back", Color.LIGHT_GRAY);
    }
}