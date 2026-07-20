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
    final PlayerNames     playerNames = new PlayerNames();
    final NameEntryDialog nameEntry   = new NameEntryDialog();
    final GameSession    session     = new GameSession(sound, highScores, playerNames, this::repaint);

    // ── מחלקות ציור המסכים ───────────────────────────────────────
    final MainMenuScreen   mainMenuScreen   = new MainMenuScreen();
    final SettingsScreen   settingsScreen   = new SettingsScreen();
    final HighScoresScreen highScoresScreen = new HighScoresScreen();
    final PauseMenuScreen  pauseMenuScreen  = new PauseMenuScreen();
    final GameOverScreen   gameOverScreen   = new GameOverScreen();
    final Renderer_Game     gameRenderer     = new Renderer_Game();
    final NameEntryScreen  nameEntryScreen  = new NameEntryScreen();

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
    /** מתחיל משחק חדש – אם חסרים שמות, קודם נפתחת חלונית הזנת שם */
    void startGame(GameMode mode) {
        if (!playerNames.hasName1()) {
            nameEntry.openForGameStart(1, mode);
            repaint(); return;
        }
        if (mode == GameMode.VS && !playerNames.hasName2()) {
            nameEntry.openForGameStart(2, mode);
            repaint(); return;
        }
        startGameNow(mode);
    }

    /** ההתחלה עצמה – אחרי שכל השמות טופלו */
    private void startGameNow(GameMode mode) {
        pendingConfirm = ConfirmAction.NONE;
        screen         = Screen.PLAYING;
        session.start(mode, settings.getSnake1Color(), settings.getSnake2Color());
        repaint();
        requestFocusInWindow();
    }

    /**
     * סיום חלונית הזנת השם (OK או Cancel).
     * אם החלונית נפתחה לקראת תחילת משחק – ממשיכים בתהליך:
     * שחקן 1 סיים ב-VS וחסר שם לשחקן 2 → נפתחת חלונית לשחקן 2,
     * אחרת המשחק מתחיל. כל שחקן נשאל פעם אחת בלבד (בלי לולאות).
     */
    void finishNameEntry(boolean confirmed) {
        if (confirmed)
            playerNames.setName(nameEntry.getPlayer(), nameEntry.getText());

        int      player  = nameEntry.getPlayer();
        GameMode pending = nameEntry.getPendingStartMode();
        nameEntry.close();

        if (pending != null) {
            if (player == 1 && pending == GameMode.VS && !playerNames.hasName2())
                nameEntry.openForGameStart(2, pending);
            else
                startGameNow(pending);
        }
        repaint();
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

        // חלונית הזנת שם – נמשכת מעל המסך הנוכחי, כמו דיאלוג אישור
        if (nameEntry.isActive()) {
            Color pColor = (nameEntry.getPlayer() == 1)
                    ? settings.getSnake1Color() : settings.getSnake2Color();
            nameEntryScreen.draw(g, getWidth(), getHeight(),
                    UISizes.bodyFont(getWidth()),
                    UISizes.btnW(getWidth()), UISizes.btnH(getHeight()),
                    nameEntry.getPlayer(), nameEntry.getText(), pColor);
        }
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

        gameRenderer.drawGame(g, session, cs, ox, oy,
                settings.getSnake1Color(), settings.getSnake2Color(),
                playerNames.getName1(), playerNames.getName2());

        if (session.getState() == GameState.COUNTDOWN)
            gameRenderer.drawCountdown(g, ox, oy, bp, session.getPreCount());

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
        gameRenderer.drawGame(g, session, cs, ox, oy,
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