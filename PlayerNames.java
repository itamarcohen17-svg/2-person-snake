/**
 * אחסון שמות השחקנים.
 *
 * הקלט עצמו נעשה דרך חלונית מעוצבת בתוך המשחק
 * (NameEntryDialog + NameEntryScreen) – לא דרך JOptionPane.
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

    // ── קביעת שם – קלט ריק לא דורס שם קיים ──────────────────────
    public void setName(int player, String name) {
        String trimmed = (name == null) ? "" : name.trim();
        if (trimmed.isEmpty()) return;
        if (player == 1) name1 = trimmed;
        else             name2 = trimmed;
    }
}