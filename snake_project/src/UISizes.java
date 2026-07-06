/**
 * חישובי גדלים דינמיים לפי גודל הפאנל.
 * הופרד מ-GamePanel כדי שכל מחלקה (מסכים, קלט) תוכל לחשב אותם בעצמה.
 */
public class UISizes {

    // ── גדלי לוח המשחק ───────────────────────────────────────────
    /** גודל תא בודד – הצלע הקטנה של הפאנל חלקי מספר התאים */
    public static int cellSize(int panelW, int panelH) {
        return Math.min(panelW, panelH) / GameConfig.GRID_SIZE;
    }

    /** גודל הלוח כולו בפיקסלים */
    public static int boardPx(int panelW, int panelH) {
        return cellSize(panelW, panelH) * GameConfig.GRID_SIZE;
    }

    /** הזחה אופקית – ממרכזת את הלוח בפאנל */
    public static int offsetX(int panelW, int panelH) {
        return (panelW - boardPx(panelW, panelH)) / 2;
    }

    /** הזחה אנכית – ממרכזת את הלוח בפאנל */
    public static int offsetY(int panelW, int panelH) {
        return (panelH - boardPx(panelW, panelH)) / 2;
    }

    // ── גדלי גופנים וכפתורים ─────────────────────────────────────
    public static int titleFont(int panelW) { return Math.max(28, panelW / 14); }
    public static int bodyFont(int panelW)  { return Math.max(14, panelW / 40); }
    public static int btnW(int panelW)      { return Math.max(200, panelW / 4); }
    public static int btnH(int panelH)      { return Math.max(48,  panelH / 12); }
}