import java.awt.*;

/**
 * ציור חלונית הזנת השם – באותו סגנון של דיאלוג אישור היציאה:
 * שכבה כהה שקופה, טקסט מרוכז וכפתורי UIHelper מעוגלים.
 * כפתורי OK/Cancel יושבים באותם מיקומים של Yes/No (UIHelper.confirmButtons).
 */
public class NameEntryScreen {

    public void draw(Graphics g, int panelW, int panelH,
                     int bodyFont, int btnW, int btnH,
                     int player, String typedText, Color playerColor) {

        int cx = panelW / 2;
        int cy = panelH / 2;

        // שכבה כהה מעל המסך הנוכחי – כמו באישור יציאה
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, panelW, panelH);

        drawPrompt(g, cx, cy, bodyFont, btnH, player, playerColor);
        drawInputBox(g, cx, cy, bodyFont, btnW, btnH, typedText, playerColor);
        drawButtons(g, panelW, panelH, btnW, btnH);
        drawKeyHint(g, panelW, panelH, cx, bodyFont, btnH);
    }

    // ── כותרת: מי מזין את שמו ────────────────────────────────────
    private void drawPrompt(Graphics g, int cx, int cy,
                            int bodyFont, int btnH, int player, Color playerColor) {
        g.setFont(new Font("Arial", Font.BOLD, bodyFont + 2));
        g.setColor(playerColor);
        String prompt = "Player " + player + " - Enter Your Name";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(prompt, cx - fm.stringWidth(prompt) / 2, cy - btnH - 20);
    }

    // ── תיבת הקלט עם סמן ─────────────────────────────────────────
    private void drawInputBox(Graphics g, int cx, int cy,
                              int bodyFont, int btnW, int btnH,
                              String typedText, Color playerColor) {
        int boxW = btnW;
        int boxH = Math.max(40, btnH * 3 / 4);
        int boxX = cx - boxW / 2;
        int boxY = cy - boxH / 2;

        // רקע כהה ומסגרת בצבע השחקן – כמו כפתורי UIHelper
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 15, 15);
        g.setColor(playerColor);
        g.drawRoundRect(boxX, boxY, boxW, boxH, 15, 15);

        // הטקסט שהוקלד + סמן "_"
        g.setFont(new Font("Arial", Font.BOLD, bodyFont + 2));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String shown = typedText + "_";
        g.drawString(shown, boxX + 14, boxY + (boxH + fm.getAscent()) / 2 - 4);
    }

    // ── כפתורי OK / Cancel – אותם מיקומים כמו Yes/No ─────────────
    private void drawButtons(Graphics g, int panelW, int panelH, int btnW, int btnH) {
        Rectangle[] cb = UIHelper.confirmButtons(panelW, panelH, btnW, btnH);
        UIHelper.drawButton(g, cb[0], "OK",     Color.GREEN);
        UIHelper.drawButton(g, cb[1], "Cancel", Color.RED);
    }

    // ── רמז מקשים ────────────────────────────────────────────────
    private void drawKeyHint(Graphics g, int panelW, int panelH,
                             int cx, int bodyFont, int btnH) {
        g.setFont(new Font("Arial", Font.PLAIN, bodyFont - 2));
        g.setColor(Color.GRAY);
        String hint = "ENTER - OK  |  ESC - Cancel";
        FontMetrics fm = g.getFontMetrics();
        Rectangle[] cb = UIHelper.confirmButtons(panelW, panelH,
                Math.max(200, panelW / 4), btnH);
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                cb[0].y + cb[0].height + 22);
    }
}