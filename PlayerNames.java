import javax.swing.JOptionPane;
import java.awt.Component;
/**
 * ניהול שמות השחקנים – מחלקת ה"סקנר" של המשחק.
 *
 * הערה: java.util.Scanner קורא מהקונסול, שלא נראה במשחק Swing חלונאי.
 * לכן הקלט נעשה עם JOptionPane – חלונית קלט גרפית שקופצת מעל המשחק.
 *
 * כשלא הוזן שם, getName1/getName2 מחזירים "P1"/"P2" כברירת מחדל,
 * כך ששאר הקוד תמיד מקבל שם תקין להצגה.
 */
public class PlayerNames {

    private String name1 = "";
    private String name2 = "";

    // ── האם הוזן שם? ─────────────────────────────────────────────
    public boolean hasName1() { return !name1.isEmpty(); }
    public boolean hasName2() { return !name2.isEmpty(); }

    // ── שם להצגה – עם ברירת מחדל P1/P2 ──────────────────────────
    public String getName1() { return hasName1() ? name1 : "P1"; }
    public String getName2() { return hasName2() ? name2 : "P2"; }

    // ── קלט שם דרך חלונית ────────────────────────────────────────
    /** מבקש שם משחקן 1; קלט ריק או ביטול משאירים את השם הקודם */
    public void askName1(Component parent) {
        String input = JOptionPane.showInputDialog(parent,
                "Player 1 - enter your name:", name1);
        if (input != null && !input.trim().isEmpty()) name1 = input.trim();
    }

    /** מבקש שם משחקן 2; קלט ריק או ביטול משאירים את השם הקודם */
    public void askName2(Component parent) {
        String input = JOptionPane.showInputDialog(parent,
                "Player 2 - enter your name:", name2);
        if (input != null && !input.trim().isEmpty()) name2 = input.trim();
    }

    // ── לפני תחילת משחק ──────────────────────────────────────────
    /**
     * אם חסרים שמות – מבקש אותם לפני הפעלת הספירה לאחור.
     * Single: רק שחקן 1.  VS: שני השחקנים.
     */
    public void ensureNamesBeforeGame(GameMode mode, Component parent) {
        if (!hasName1()) askName1(parent);
        if (mode == GameMode.VS && !hasName2()) askName2(parent);
    }
}
