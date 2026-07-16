import java.awt.*;

/**
 * מסך תפריט עצירה (Pause).
 * אחראי על: ציור כפתורי השהייה ודיאלוג אישור.
 * המשחק הקפוא מצויר ברקע על-ידי Renderer_Game לפני קריאה למחלקה זו.
 */
public class PauseMenuScreen {

    // ── 3 כפתורי תפריט עצירה ─────────────────────────────────────
    public Rectangle[] buttons(int panelW, int panelH, int btnW, int btnH) {
        int cx     = panelW / 2;
        int gap    = btnH + 14;
        int startY = panelH / 2 - gap + btnH / 2;
        Rectangle[] r = new Rectangle[3];
        for (int i = 0; i < 3; i++)
            r[i] = new Rectangle(cx - btnW / 2, startY + i * gap, btnW, btnH);
        return r;
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int titleFont, int bodyFont, int btnW, int btnH,
                     boolean showConfirm, String confirmMessage) {

        int cx = panelW / 2;

        // שכבה כהה מעל המשחק הקפוא
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, panelW, panelH);

        // כותרת PAUSED
        g.setFont(new Font("Arial", Font.BOLD, titleFont));
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        String title = "PAUSED";
        g.drawString(title, cx - fm.stringWidth(title) / 2, panelH / 4);

        if (showConfirm) {
            drawConfirmDialog(g, panelW, panelH, bodyFont, btnW, btnH, cx, confirmMessage);
        } else {
            drawNormalButtons(g, panelW, panelH, bodyFont, btnW, btnH, cx);
        }
    }

    // ── דיאלוג אישור ─────────────────────────────────────────────
    private void drawConfirmDialog(Graphics g, int panelW, int panelH,
                                   int bodyFont, int btnW, int btnH,
                                   int cx, String msg) {
        g.setFont(new Font("Arial", Font.PLAIN, bodyFont));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, cx - fm.stringWidth(msg) / 2, panelH / 2);

        Rectangle[] cb = UIHelper.confirmButtons(panelW, panelH, btnW, btnH);
        UIHelper.drawButton(g, cb[0], "Yes", Color.RED);
        UIHelper.drawButton(g, cb[1], "No",  Color.GREEN);
    }

    // ── כפתורים רגילים ───────────────────────────────────────────
    private void drawNormalButtons(Graphics g, int panelW, int panelH,
                                   int bodyFont, int btnW, int btnH, int cx) {
        Rectangle[] b = buttons(panelW, panelH, btnW, btnH);
        UIHelper.drawButton(g, b[0], "Resume",         Color.GREEN);
        UIHelper.drawButton(g, b[1], "Return to Menu", Color.YELLOW);
        UIHelper.drawButton(g, b[2], "Exit Program",   Color.RED);

        g.setFont(new Font("Arial", Font.PLAIN, bodyFont - 2));
        g.setColor(Color.GRAY);
        String hint = "Press ESC to resume";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                b[2].y + b[2].height +21);
    }
}
