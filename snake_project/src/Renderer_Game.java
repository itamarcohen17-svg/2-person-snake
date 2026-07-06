import java.awt.*;

/**
 * אחראי על ציור מצב המשחק הפעיל:
 *   גריד הרשת, נחשים, אוכל, HUD (ניקוד/טיימר) וספירה לאחור.
 * מתאם בין FoodRenderer ו-SnakeRenderer.
 */
public class Renderer_Game {

    private final Renderer_Food RenderFood = new Renderer_Food();

    // ── ציור כל מצב המשחק ────────────────────────────────────────
    public void drawGame(Graphics g, GameSession session,
                         int cs, int ox, int oy,
                         Color snake1Color, Color snake2Color) {

        drawGrid(g, cs, ox, oy);
        RenderFood.draw(g, session.getFood(), cs, ox, oy);
        Renderer_Snake.draw(g, session.getSnake1(), cs, ox, oy);
        if (session.getMode() == GameMode.VS && session.getSnake2() != null)
            Renderer_Snake.draw(g, session.getSnake2(), cs, ox, oy);
        drawHUD(g, session, cs, ox, oy, snake1Color, snake2Color);
    }

    // ── ספירה לאחור (3-2-1) ──────────────────────────────────────
    public void drawCountdown(Graphics g, int ox, int oy, int bp, int preCount) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(ox, oy, bp, bp);

        int numSize = Math.max(60, bp / 4);
        g.setFont(new Font("Arial", Font.BOLD, numSize));
        g.setColor(Color.WHITE);
        String num = String.valueOf(preCount);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(num,
                ox + (bp - fm.stringWidth(num)) / 2,
                oy + (bp + fm.getAscent()) / 2 - fm.getDescent());
    }

    // ── גריד הרשת ────────────────────────────────────────────────
    private void drawGrid(Graphics g, int cs, int ox, int oy) {
        g.setColor(new Color(25, 25, 25));
        int bp = cs * GameConfig.GRID_SIZE;
        for (int i = 0; i <= GameConfig.GRID_SIZE; i++) {
            g.drawLine(ox + i * cs, oy,      ox + i * cs, oy + bp);
            g.drawLine(ox,    oy + i * cs,   ox + bp,     oy + i * cs);
        }
    }

    // ── HUD (ניקוד ב-Single / טיימר וסימון שחקנים ב-VS) ─────────
    private void drawHUD(Graphics g, GameSession session,
                         int cs, int ox, int oy,
                         Color snake1Color, Color snake2Color) {

        int fs = Math.max(12, cs * 3 / 4);
        int bp = cs * GameConfig.GRID_SIZE;
        g.setFont(new Font("Arial", Font.BOLD, fs));

        if (session.getMode() == GameMode.SINGLE) {
            g.setColor(Color.WHITE);
            g.drawString("Food: " + session.getFoodCount(), ox + 5, oy + fs);
        } else {
            int timeLeft = session.getTimeLeft();
            int mins = timeLeft / 60, secs = timeLeft % 60;
            g.setColor(Color.WHITE);
            g.drawString("Time: " + mins + ":" + String.format("%02d", secs),
                    ox + bp / 2 - fs * 2, oy + fs);
            g.setColor(snake1Color);
            g.drawString("P1", ox + 5, oy + fs);
            g.setColor(snake2Color);
            g.drawString("P2", ox + bp - fs * 2, oy + fs);
        }
    }
}