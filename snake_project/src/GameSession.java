import javax.swing.Timer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * המשחק הרץ – כל הלוגיקה של משחק פעיל אחד:
 *   נחשים, אוכל, ניקוד, טיימרים, אכילה, התנגשויות וסיום.
 *
 * GamePanel רק מפעיל: start / pause / resume / stopAllTimers,
 * ושואל getters לצורך ציור.
 */
public class GameSession {

    // ── מצב המשחק ────────────────────────────────────────────────
    private GameMode  mode;
    private GameState state;

    // ── אובייקטים ────────────────────────────────────────────────
    private SnakeLinkedList snake1, snake2;
    private Food            food;

    // ── ניקוד ומונים ─────────────────────────────────────────────
    private int     foodCount   = 0;
    private int     score       = 0;
    private int     timeLeft    = 180;    // VS mode בלבד
    private int     preCount    = 3;      // ספירה לאחור 3-2-1
    private int     tickCount   = 0;      // מונע בדיקת התנגשות בפריים הראשון
    private String  vsWinner    = "";
    private boolean isNewRecord = false;

    // ── בופר קלט ─────────────────────────────────────────────────
    private Direction pending1 = null;
    private Direction pending2 = null;

    // ── טיימרים ──────────────────────────────────────────────────
    private final Timer gameTimer;         // לולאת המשחק
    private Timer       preCountTimer;     // ספירה לאחור לפני התחלה
    private Timer       vsCountdownTimer;  // טיימר 3 דקות ב-VS

    // ── שירותים ──────────────────────────────────────────────────
    private final SoundManager   sound;
    private final HighScoreBoard highScores;
    private final Runnable       repaint;   // קריאה חוזרת לציור הפאנל

    public GameSession(SoundManager sound, HighScoreBoard highScores, Runnable repaint) {
        this.sound      = sound;
        this.highScores = highScores;
        this.repaint    = repaint;
        this.gameTimer  = new Timer(GameConfig.DELAY_MS, e -> tick());
    }

    // ═════════════════════════════════════════════════════════════
    //  תחילת משחק
    // ═════════════════════════════════════════════════════════════
    public void start(GameMode chosen, Color snake1Color, Color snake2Color) {
        stopAllTimers();

        mode        = chosen;
        state       = GameState.COUNTDOWN;
        pending1    = null;
        pending2    = null;
        vsWinner    = "";
        tickCount   = 0;
        preCount    = 3;
        foodCount   = 0;
        score       = 0;
        isNewRecord = false;

        buildSnakes(snake1Color, snake2Color);

        food = new Food();
        respawnFoodSafely();

        startPreCountdown();
    }

    /** בונה את הנחשים במיקומי הפתיחה */
    private void buildSnakes(Color snake1Color, Color snake2Color) {
        int midY   = GameConfig.GRID_SIZE / 2;
        int p1Head = GameConfig.GRID_SIZE / 4 + 2;
        int p2Head = GameConfig.GRID_SIZE - GameConfig.GRID_SIZE / 4 - 2;

        snake1 = new SnakeLinkedList(snake1Color, Direction.RIGHT, p1Head, midY, false);
        if (mode == GameMode.VS) {
            snake2   = new SnakeLinkedList(snake2Color, Direction.LEFT, p2Head, midY, true);
            timeLeft = 180;
        } else {
            snake2 = null;
        }
    }

    /** ספירה לאחור 3-2-1; בסיומה המשחק מתחיל לרוץ */
    private void startPreCountdown() {
        preCountTimer = new Timer(1000, null);
        preCountTimer.addActionListener(ev -> {
            preCount--;
            if (preCount <= 0) {
                preCountTimer.stop();
                state = GameState.RUNNING;
                gameTimer.start();
                if (mode == GameMode.VS) startVsCountdown();
                sound.playTheme();
            }
            repaint.run();
        });
        sound.stopMusic();
        sound.playStart();
        preCountTimer.start();
    }

    /** טיימר הזמן ב-VS – סיום המשחק כשנגמרות 3 הדקות */
    private void startVsCountdown() {
        if (vsCountdownTimer != null) vsCountdownTimer.stop();
        vsCountdownTimer = new Timer(1000, ev -> {
            if (state != GameState.RUNNING) return;
            timeLeft--;
            if (timeLeft <= 0) endGameByTime();
        });
        vsCountdownTimer.start();
    }

    /** כשנגמר הזמן ב-VS: הנחש הגדול יותר מנצח; גודל שווה = תיקו */
    private void endGameByTime() {
        int s1 = snake1.getSize();
        int s2 = snake2.getSize();
        if      (s1 > s2) vsWinner = "P1 Wins!";
        else if (s2 > s1) vsWinner = "P2 Wins!";
        else              vsWinner = "Draw!";
        endGame();
    }

    // ═════════════════════════════════════════════════════════════
    //  השהייה / המשך / עצירה
    // ═════════════════════════════════════════════════════════════
    /** השהייה – עוצר טיימרים בלי לאפס אותם (כדי שהמשך ימשיך מאותה נקודה) */
    public void pause() {
        state = GameState.PAUSED;
        gameTimer.stop();
        if (vsCountdownTimer != null) vsCountdownTimer.stop();
        if (preCountTimer    != null) preCountTimer.stop();
    }

    /** המשך משחק אחרי השהייה */
    public void resume() {
        state = GameState.RUNNING;
        gameTimer.start();
        if (mode == GameMode.VS && vsCountdownTimer != null) vsCountdownTimer.start();
    }

    /** עצירה מלאה של כל הטיימרים (סיום או חזרה לתפריט) */
    public void stopAllTimers() {
        gameTimer.stop();
        if (vsCountdownTimer != null) { vsCountdownTimer.stop(); vsCountdownTimer = null; }
        if (preCountTimer    != null) { preCountTimer.stop();    preCountTimer    = null; }
    }

    // ═════════════════════════════════════════════════════════════
    //  לולאת המשחק – רצה כל פריים
    // ═════════════════════════════════════════════════════════════
    private void tick() {
        if (state != GameState.RUNNING) return;

        // החלת הכיוונים הממתינים
        if (pending1 != null) { snake1.setDirection(pending1); pending1 = null; }
        if (pending2 != null && snake2 != null) { snake2.setDirection(pending2); pending2 = null; }

        snake1.move();
        if (mode == GameMode.VS && snake2 != null) snake2.move();

        checkEating();
        tickCount++;
        if (tickCount > 1) checkCollisions();
        repaint.run();
    }

    // ── אכילה ────────────────────────────────────────────────────
    private void checkEating() {
        Point foodPos = food.getPosition();
        if (snake1.eats(foodPos)) {
            snake1.grow();
            foodCount++;
            if (mode == GameMode.SINGLE) score += 10;
            sound.playEat();
            respawnFoodSafely();
        }
        if (mode == GameMode.VS && snake2 != null && snake2.eats(foodPos)) {
            snake2.grow();
            sound.playEat();
            respawnFoodSafely();
        }
    }

    // ── התנגשויות ────────────────────────────────────────────────
    private void checkCollisions() {
        if (snake1.collidesWithSelf()) snake1.kill();
        if (mode == GameMode.VS && snake2 != null && snake2.collidesWithSelf()) snake2.kill();

        if (mode == GameMode.VS && snake2 != null) {
            if (snake1.collidesWithOther(snake2)) snake1.kill();
            if (snake2.collidesWithOther(snake1)) snake2.kill();
            // ראש-בראש: הגדול מנצח; שווים – שניהם מתים
            if (snake1.headOnHead(snake2)) {
                int s1 = snake1.getSize(), s2 = snake2.getSize();
                if      (s1 > s2) snake2.kill();
                else if (s2 > s1) snake1.kill();
                else              { snake1.kill(); snake2.kill(); }
            }
        }

        if (mode == GameMode.SINGLE) {
            if (!snake1.isAlive()) endGame();
        } else {
            boolean s1Dead = !snake1.isAlive();
            boolean s2Dead = snake2 != null && !snake2.isAlive();
            if      (s1Dead && s2Dead) { vsWinner = "Draw!";    endGame(); }
            else if (s1Dead)           { vsWinner = "P2 Wins!"; endGame(); }
            else if (s2Dead)           { vsWinner = "P1 Wins!"; endGame(); }
        }
    }

    // ── סיום משחק ────────────────────────────────────────────────
    private void endGame() {
        state = GameState.GAME_OVER;
        stopAllTimers();
        sound.stopMusic();

        isNewRecord = false;
        if (mode == GameMode.SINGLE) {
            score       = (foodCount + (180 - timeLeft)) * 10;
            isNewRecord = score > highScores.getScores()[0];   // בדיקה לפני ה-submit!
            highScores.submit(score);
        }
        // Single: lose רגיל, record רק בשיא חדש.
        // VS: record כשיש מנצח; בתיקו (שניהם מתו) נשאר lose.
        boolean vsHasWinner     = (mode == GameMode.VS) && !vsWinner.equals("Draw!");
        boolean playRecordSound = vsHasWinner || isNewRecord;
        sound.playLose(playRecordSound);
        repaint.run();
    }

    // ── אוכל ─────────────────────────────────────────────────────
    private void respawnFoodSafely() {
        List<Point> occupied = new ArrayList<>(snake1.getBodyPoints());
        if (snake2 != null) occupied.addAll(snake2.getBodyPoints());
        food.respawn(occupied);
    }

    // ═════════════════════════════════════════════════════════════
    //  קלט – כיוונים ממתינים (מוחלים בפריים הבא)
    // ═════════════════════════════════════════════════════════════
    public void setPending1(Direction d) { pending1 = d; }
    public void setPending2(Direction d) { pending2 = d; }

    // ═════════════════════════════════════════════════════════════
    //  שינוי צבע נחש תוך כדי משחק (מהגדרות)
    // ═════════════════════════════════════════════════════════════
    public void recolorSnake1(Color c) { if (snake1 != null) snake1.changeColor(c); }
    public void recolorSnake2(Color c) { if (snake2 != null) snake2.changeColor(c); }

    // ═════════════════════════════════════════════════════════════
    //  Getters לציור
    // ═════════════════════════════════════════════════════════════
    public GameMode        getMode()      { return mode; }
    public GameState       getState()     { return state; }
    public SnakeLinkedList getSnake1()    { return snake1; }
    public SnakeLinkedList getSnake2()    { return snake2; }
    public Food            getFood()      { return food; }
    public int             getFoodCount() { return foodCount; }
    public int             getScore()     { return score; }
    public int             getTimeLeft()  { return timeLeft; }
    public int             getPreCount()  { return preCount; }
    public String          getVsWinner()  { return vsWinner; }
    public boolean         isNewRecord()  { return isNewRecord; }
}