import javax.swing.*;
import java.awt.*;

/**
 * לוח המשחק – מתאם ראשי קצר.
 *
 * תפקידיו בלבד:
 *   1. ניווט בין מסכים (startGame / resumeGame / goTo / executeConfirm)
 *   2. ניתוב הציור למחלקת המסך המתאימה
 *
 * כל השאר הופרד:
 *   GameSession       – לוגיקת המשחק הרץ (נחשים, אוכל, טיימרים, ניקוד)
 *   GameSettings      – צבעים והגדרות צליל
 *   Handler_Mouse      – לחיצות עכבר
 *   KeyboardHandler   – קלט מקלדת
 *   UISizes           – חישובי גדלים
 *   Screen / GameMode / GameState / ConfirmAction / Direction – מחלקות קבועים
 *
 * הערה: שדות ללא מציין גישה הם package-private – כך המטפלים
 * (Handler_Mouse / KeyboardHandler) ניגשים אליהם ישירות.
 */
public class GamePanel extends JPanel {

    // ── מצב ניווט ────────────────────────────────────────────────
    Screen        screen         = Screen.MAIN_MENU;
    ConfirmAction pendingConfirm = ConfirmAction.NONE;

    // ── שירותים ──────────────────────────────────────────────────
    final HighScoreBoard highScores  = new HighScoreBoard();
    final SoundManager   sound       = new SoundManager();
    final GameSettings   settings    = new GameSettings(sound);
    final PlayerNames    playerNames = new PlayerNames();
    final GameSession    session     = new GameSession(sound, highScores, playerNames, this::repaint);

    // ── מחלקות ציור המסכים ───────────────────────────────────────
    final MainMenuScreen   mainMenuScreen   = new MainMenuScreen();
    final SettingsScreen   settingsScreen   = new SettingsScreen();
    final HighScoresScreen highScoresScreen = new HighScoresScreen();
    final PauseMenuScreen  pauseMenuScreen  = new PauseMenuScreen();
    final GameOverScreen   gameOverScreen   = new GameOverScreen();
    final Renderer_Game Renderer_Game = new Renderer_Game();

    // ── בנאי ─────────────────────────────────────────────────────
    public GamePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(
                GameConfig.GRID_SIZE * GameConfig.CELL_SIZE,
                GameConfig.GRID_SIZE * GameConfig.CELL_SIZE));
        setFocusable(true);
        addKeyListener(new Handler_Keyboard(this));
        addMouseListener(new Handler_Mouse(this));
    }

    // ═════════════════════════════════════════════════════════════
    //  ניווט
    // ═════════════════════════════════════════════════════════════
    /** מתחיל משחק חדש במצב הנבחר */
    void startGame(GameMode mode) {
        playerNames.ensureNamesBeforeGame(mode, this);   // שמות חסרים? נבקש לפני הספירה לאחור
        pendingConfirm = ConfirmAction.NONE;
        screen         = Screen.PLAYING;
        session.start(mode, settings.getSnake1Color(), settings.getSnake2Color());
        repaint();
        requestFocusInWindow();
    }

    /** ממשיך משחק מושהה */
    void resumeGame() {
        pendingConfirm = ConfirmAction.NONE;
        screen         = Screen.PLAYING;
        session.resume();
        repaint();
    }

    /** משהה את המשחק ופותח תפריט עצירה */
    void pauseGame() {
        session.pause();
        screen = Screen.PAUSE_MENU;
        repaint();
    }

    /** מעבר למסך אחר */
    void goTo(Screen target) {
        screen = target;
        repaint();
    }

    /** מבצע את הפעולה שאושרה בדיאלוג YES */
    void executeConfirm() {
        if (pendingConfirm == ConfirmAction.RETURN_TO_MENU) {
            session.stopAllTimers();
            pendingConfirm = ConfirmAction.NONE;
            screen = Screen.MAIN_MENU;
            sound.stopMusic();
            repaint();
        } else if (pendingConfirm == ConfirmAction.EXIT_PROGRAM
                || pendingConfirm == ConfirmAction.EXIT_MAIN) {
            System.exit(0);
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  ציור – ניתוב למסך המתאים
    // ═════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if      (screen == Screen.MAIN_MENU)   paintMainMenu(g);
        else if (screen == Screen.SETTINGS)    paintSettings(g);
        else if (screen == Screen.HIGH_SCORES) paintHighScores(g);
        else if (screen == Screen.PLAYING)     paintPlaying(g);
        else if (screen == Screen.PAUSE_MENU)  paintPauseMenu(g);
    }

    private void paintMainMenu(Graphics g) {
        mainMenuScreen.draw(g, getWidth(), getHeight(),
                UISizes.titleFont(getWidth()), UISizes.bodyFont(getWidth()),
                UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                pendingConfirm == ConfirmAction.EXIT_MAIN,
                playerNames.getName1(), playerNames.getName2());
    }

    private void paintSettings(Graphics g) {
        settingsScreen.draw(g, getWidth(), getHeight(),
                UISizes.titleFont(getWidth()), UISizes.bodyFont(getWidth()),
                UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                settings.getSnake1Color(), settings.getSnake2Color(),
                playerNames.getName1(), playerNames.getName2(),
                settings.isSoundEnabled(), settings.isMusicEnabled(), settings.isSfxEnabled());
    }

    private void paintHighScores(Graphics g) {
        highScoresScreen.draw(g, getWidth(), getHeight(),
                UISizes.titleFont(getWidth()), UISizes.bodyFont(getWidth()),
                UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                highScores.getScores(), highScores.getNames());
    }

    private void paintPlaying(Graphics g) {
        int cs = UISizes.cellSize(getWidth(), getHeight());
        int ox = UISizes.offsetX(getWidth(), getHeight());
        int oy = UISizes.offsetY(getWidth(), getHeight());
        int bp = UISizes.boardPx(getWidth(), getHeight());

        Renderer_Game.drawGame(g, session, cs, ox, oy,
                settings.getSnake1Color(), settings.getSnake2Color(),
                playerNames.getName1(), playerNames.getName2());

        if (session.getState() == GameState.COUNTDOWN)
            Renderer_Game.drawCountdown(g, ox, oy, bp, session.getPreCount());

        if (session.getState() == GameState.GAME_OVER)
            gameOverScreen.draw(g, getWidth(), getHeight(),
                    UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                    session.getMode() == GameMode.SINGLE,
                    session.getVsWinner(), session.getScore(), session.isNewRecord(),
                    playerNames.getName1());
    }

    private void paintPauseMenu(Graphics g) {
        // המשחק הקפוא ברקע
        int cs = UISizes.cellSize(getWidth(), getHeight());
        int ox = UISizes.offsetX(getWidth(), getHeight());
        int oy = UISizes.offsetY(getWidth(), getHeight());
        Renderer_Game.drawGame(g, session, cs, ox, oy,
                settings.getSnake1Color(), settings.getSnake2Color(),
                playerNames.getName1(), playerNames.getName2());

        boolean showConfirm = (pendingConfirm != ConfirmAction.NONE);
        String  confirmMsg  = (pendingConfirm == ConfirmAction.EXIT_PROGRAM)
                ? "Exit? Game data will be lost."
                : "Return to menu? Game data will be lost.";

        pauseMenuScreen.draw(g, getWidth(), getHeight(),
                UISizes.titleFont(getWidth()), UISizes.bodyFont(getWidth()),
                UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                showConfirm, confirmMsg);
    }
}
