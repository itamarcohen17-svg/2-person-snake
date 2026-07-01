import java.util.Arrays;

/**
 * Manages a top-5 leaderboard (in-memory only, resets on each run).
 * Logic from the original HighScoreBoard.java – renamed to follow Java conventions.
 */
public class HighScoreBoard {

    private int[] scores;      // always sorted descending
    private static final int SIZE = 5;

    public HighScoreBoard() {
        scores = new int[SIZE];   // initialised to 0
    }

    /** Submit a new score; inserts it if it beats the 5th place. */
    public void submit(int newScore) {
        if (newScore > scores[SIZE - 1]) {
            scores[SIZE - 1] = newScore;
            sortDescending();
        }
    }

    /** Returns a copy of the top-5 array (descending). */
    public int[] getScores() {
        return scores.clone();
    }

    /** True if newScore would make the leaderboard. */
    public boolean isHighScore(int newScore) {
        return newScore > scores[SIZE - 1];
    }

    private void sortDescending() {
        Arrays.sort(scores);
        // reverse
        for (int i = 0; i < SIZE / 2; i++) {
            int tmp = scores[i];
            scores[i] = scores[SIZE - 1 - i];
            scores[SIZE - 1 - i] = tmp;
        }
    }

    @Override
    public String toString() {
        String[] ordinals = { "1st", "2nd", "3rd", "4th", "5th" };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++)
            sb.append(ordinals[i]).append(": ").append(scores[i]).append("\n");
        return sb.toString();
    }
}
