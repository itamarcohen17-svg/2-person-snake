import java.awt.*;

/**
 * מסך סיום משחק (Game Over).
 * אחראי על: ציור כותרת, ציון, שיא חדש וכפתורי המשך.
 */
public class GameOverScreen {

    // ── מיקומי כפתורים ───────────────────────────────────────────
    /**
     * מחשב את הכפתורים לפי מצב המשחק:
     * Single Player: 3 כפתורים (New Game, High Scores, Main Menu)
     * VS:            2 כפתורים (New Game, Main Menu)
     */
    public Rectangle[] buttons(int panelW, int panelH, int btnW, int btnH,
                               boolean isSinglePlayer) {
        int cx     = panelW / 2;
        int gap    = btnH + 12;
        int count  = isSinglePlayer ? 3 : 2;
        int startY = panelH / 2 + (int)(panelH * 0.08);
        Rectangle[] r = new Rectangle[count];
        for (int i = 0; i < count; i++)
            r[i] = new Rectangle(cx - btnW / 2, startY + i * gap, btnW, btnH);
        return r;
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int btnW, int btnH,
                     boolean isSinglePlayer, String vsWinner,
                     int score, boolean isNewRecord, String playerName) {

        // שכבה כהה על כל המסך
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, panelW, panelH);

        int cx = panelW / 2;
        int cy = panelH / 2;

        drawTitle(g, panelW, panelH, cx, cy, isSinglePlayer, vsWinner);

        if (isSinglePlayer)
            drawScoreSection(g, panelW, panelH, cx, cy, score, isNewRecord, playerName);

        drawButtons(g, panelW, panelH, cx, cy, btnW, btnH, isSinglePlayer);
        drawKeyHints(g, panelW, panelH, btnW, btnH, cx, isSinglePlayer);
    }

    // ── כותרת ────────────────────────────────────────────────────
    private void drawTitle(Graphics g, int panelW, int panelH,
                           int cx, int cy, boolean isSinglePlayer, String vsWinner) {
        String title    = isSinglePlayer ? "GAME OVER" : vsWinner;
        int    fontSize = Math.max(32, panelW / 10);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(Color.RED);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, cx - fm.stringWidth(title) / 2,
                cy - (int)(panelH * 0.18));
    }

    // ── ציון ─────────────────────────────────────────────────────
    private void drawScoreSection(Graphics g, int panelW, int panelH,
                                  int cx, int cy, int score, boolean isNewRecord,
                                  String playerName) {
        int scoreSize = Math.max(18, panelW / 22);
        g.setFont(new Font("Arial", Font.BOLD, scoreSize));
        g.setColor(isNewRecord ? Color.YELLOW : Color.WHITE);
        String scoreLine = "Score: " + score;
        FontMetrics fm   = g.getFontMetrics();
        int scoreY       = cy - (int)(panelH * 0.06);
        g.drawString(scoreLine, cx - fm.stringWidth(scoreLine) / 2, scoreY);

        if (isNewRecord) {
            String rec     = "★  NEW HIGH SCORE: " + playerName + " - " + score + "!  ★";
            int    recSize = Math.max(14, panelW / 30);
            g.setFont(new Font("Arial", Font.BOLD, recSize));
            g.setColor(Color.YELLOW);
            fm = g.getFontMetrics();
            g.drawString(rec, cx - fm.stringWidth(rec) / 2,
                    scoreY + scoreSize + 6);
        }
    }

    // ── כפתורים ──────────────────────────────────────────────────
    private void drawButtons(Graphics g, int panelW, int panelH,
                             int cx, int cy, int btnW, int btnH,
                             boolean isSinglePlayer) {
        Rectangle[] b = buttons(panelW, panelH, btnW, btnH, isSinglePlayer);
        if (isSinglePlayer) {
            UIHelper.drawButton(g, b[0], "New Game",    Color.GREEN);
            UIHelper.drawButton(g, b[1], "High Scores", Color.YELLOW);
            UIHelper.drawButton(g, b[2], "Main Menu",   Color.LIGHT_GRAY);
        } else {
            UIHelper.drawButton(g, b[0], "New Game",  Color.GREEN);
            UIHelper.drawButton(g, b[1], "Main Menu", Color.LIGHT_GRAY);
        }
    }

    // ── רמזי מקשים ───────────────────────────────────────────────
    private void drawKeyHints(Graphics g, int panelW, int panelH, int btnW, int btnH,
                              int cx, boolean isSinglePlayer) {
        String hint = isSinglePlayer
                ? "ENTER – New Game  |  H – High Scores  |  M – Main Menu"
                : "ENTER – New Game  |  M – Main Menu";
        int hintSize = Math.max(11, panelW / 55);
        g.setFont(new Font("Arial", Font.PLAIN, hintSize));
        g.setColor(Color.GRAY);
        FontMetrics fm    = g.getFontMetrics();
        Rectangle[] btns  = buttons(panelW, panelH, btnW, btnH, isSinglePlayer);
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                btns[btns.length - 1].y + btns[btns.length - 1].height + 20);
    }
}
