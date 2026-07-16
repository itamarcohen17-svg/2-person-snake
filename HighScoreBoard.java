/**
 * מנהל טבלת שיאים של 5 מקומות (זיכרון בלבד – מתאפס עם הפעלה מחדש).
 * לכל שיא נשמר גם שם השחקן שקבע אותו – במערך String מקביל.
 */
public class HighScoreBoard {

    private static final int SIZE = 5;

    private int[]    scores;   // ממויין בסדר יורד תמיד
    private String[] names;    // names[i] = מי קבע את scores[i]

    public HighScoreBoard() {
        scores = new int[SIZE];       // מאותחל ל-0
        names  = new String[SIZE];
        for (int i = 0; i < SIZE; i++) names[i] = "";
    }

    /** מגיש ציון חדש עם שם השחקן – נכנס לטבלה אם גובר על המקום החמישי */
    public void submit(int newScore, String playerName) {
        if (newScore > scores[SIZE - 1]) {
            scores[SIZE - 1] = newScore;
            names[SIZE - 1]  = playerName;
            sortDescending();
        }
    }

    /** מחזיר עותק של מערך הציונים (יורד) */
    public int[] getScores() {
        return scores.clone();
    }

    /** מחזיר עותק של מערך השמות – names[i] מתאים ל-scores[i] */
    public String[] getNames() {
        return names.clone();
    }

    // ── עזר פנימי ────────────────────────────────────────────────
    /**
     * מיון בועות בסדר יורד.
     * כל החלפה מבוצעת בשני המערכים יחד – כך שם נשאר צמוד לציון שלו.
     */
    private void sortDescending() {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = 0; j < SIZE - 1 - i; j++) {
                if (scores[j] < scores[j + 1]) {
                    int tmpScore  = scores[j];
                    scores[j]     = scores[j + 1];
                    scores[j + 1] = tmpScore;

                    String tmpName = names[j];
                    names[j]       = names[j + 1];
                    names[j + 1]   = tmpName;
                }
            }
        }
    }

    @Override
    public String toString() {
        String[]      ordinals = { "1st", "2nd", "3rd", "4th", "5th" };
        StringBuilder sb       = new StringBuilder();
        for (int i = 0; i < SIZE; i++)
            sb.append(ordinals[i]).append(": ")
                    .append(names[i]).append(" ").append(scores[i]).append("\n");
        return sb.toString();
    }
}
