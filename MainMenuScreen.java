import java.awt.*;

/**
 * מסך תפריט ראשי.
 * אחראי על: ציור התפריט, חישוב מיקומי הכפתורים וזיהוי לחיצות.
 */
public class MainMenuScreen {

    // ── 5 כפתורי התפריט ──────────────────────────────────────────
    private static final String[] LABELS = { "Single Player", "VS (2P)", "Settings", "High Scores", "Exit" };
    private static final Color[]  COLORS = { Color.GREEN, Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.RED };

    // ── מיקומי כפתורים ───────────────────────────────────────────
    public Rectangle[] buttons(int panelW, int panelH, int btnW, int btnH) {
        int cx     = panelW / 2;
        int gap    = btnH + 12;
        int startY = panelH / 2 - gap * 2 + btnH / 2;
        Rectangle[] r = new Rectangle[5];
        for (int i = 0; i < 5; i++)
            r[i] = new Rectangle(cx - btnW / 2, startY + i * gap, btnW, btnH);
        return r;
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int titleFont, int bodyFont, int btnW, int btnH,
                     boolean showExitConfirm, String name1, String name2) {

        int cx = panelW / 2;

        // כותרת
        g.setFont(new Font("Arial", Font.BOLD, titleFont));
        g.setColor(Color.GREEN);
        FontMetrics fm = g.getFontMetrics();
        String title = "SNAKE";
        g.drawString(title, cx - fm.stringWidth(title) / 2, panelH / 5);

        // כפתורים
        Rectangle[] b = buttons(panelW, panelH, btnW, btnH);
        for (int i = 0; i < 5; i++)
            UIHelper.drawButton(g, b[i], LABELS[i], COLORS[i]);

        // רמז מקשים
        g.setFont(new Font("Arial", Font.PLAIN, bodyFont - 2));
        g.setColor(Color.GRAY);
        String hint = name1 + ": Arrow keys  |  " + name2 + ": WASD  |  Press 1 / 2 to start";
        fm = g.getFontMetrics();
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                b[4].y + b[4].height + 26);

        // שכבת אישור יציאה
        if (showExitConfirm) drawExitConfirmOverlay(g, panelW, panelH, bodyFont, btnW, btnH);
    }

    // ── שכבת אישור יציאה ─────────────────────────────────────────
    private void drawExitConfirmOverlay(Graphics g, int panelW, int panelH,
                                        int bodyFont, int btnW, int btnH) {
        int cx = panelW / 2;

        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, panelW, panelH);

        g.setFont(new Font("Arial", Font.BOLD, bodyFont + 2));
        g.setColor(Color.WHITE);
        String msg = "Are you sure you want to exit?";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, cx - fm.stringWidth(msg) / 2, panelH / 2 - btnH);

        Rectangle[] cb = UIHelper.confirmButtons(panelW, panelH, btnW, btnH);
        UIHelper.drawButton(g, cb[0], "Yes", Color.RED);
        UIHelper.drawButton(g, cb[1], "No",  Color.GREEN);
    }
}
