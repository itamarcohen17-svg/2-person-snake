import java.util.Arrays;

/**
 * מנהל טבלת שיאים של 5 מקומות (זיכרון בלבד – מתאפס עם הפעלה מחדש).
 */
public class HighScoreBoard {

    private static final int SIZE = 5;
    private int[] scores;   // ממויין בסדר יורד תמיד

    public HighScoreBoard() {
        scores = new int[SIZE];   // מאותחל ל-0
    }

    /** מגיש ציון חדש – נכנס לטבלה אם גובר על המקום החמישי */
    public void submit(int newScore) {
        if (newScore > scores[SIZE - 1]) {
            scores[SIZE - 1] = newScore;
            sortDescending();
        }
    }

    /** מחזיר עותק של מערך הציונים (יורד) */
    public int[] getScores() {
        return scores.clone();
    }

    // ── עזר פנימי ────────────────────────────────────────────────
    private void sortDescending() {
        Arrays.sort(scores);
        for (int i = 0; i < SIZE / 2; i++) {
            int tmp = scores[i];
            scores[i]          = scores[SIZE - 1 - i];
            scores[SIZE-1-i]   = tmp;
        }
    }

    @Override
    public String toString() {
        String[]      ordinals = { "1st", "2nd", "3rd", "4th", "5th" };
        StringBuilder sb       = new StringBuilder();
        for (int i = 0; i < SIZE; i++)
            sb.append(ordinals[i]).append(": ").append(scores[i]).append("\n");
        return sb.toString();
    }
}