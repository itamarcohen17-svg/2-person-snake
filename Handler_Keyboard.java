import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * מטפל בכל קלט המקלדת – מנתב לפי המסך הנוכחי.
 * הופרד מ-GamePanel; מיפוי המקשים לכיוונים נמצא ב-Direction.
 */
public class Handler_Keyboard extends KeyAdapter {

    private final GamePanel panel;

    public Handler_Keyboard(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if      (panel.screen == Screen.MAIN_MENU)   mainMenuKeys(k);
        else if (panel.screen == Screen.SETTINGS
                || panel.screen == Screen.HIGH_SCORES) escapeToMenu(k);
        else if (panel.screen == Screen.PAUSE_MENU)  pauseMenuKeys(k);
        else if (panel.screen == Screen.PLAYING)     playingKeys(k);
    }

    // ── תפריט ראשי: 1 / 2 להתחלה מהירה ──────────────────────────
    private void mainMenuKeys(int k) {
        if (k == KeyEvent.VK_1) panel.startGame(GameMode.SINGLE);
        if (k == KeyEvent.VK_2) panel.startGame(GameMode.VS);
    }

    // ── הגדרות / שיאים: ESC חוזר לתפריט ─────────────────────────
    private void escapeToMenu(int k) {
        if (k == KeyEvent.VK_ESCAPE) panel.goTo(Screen.MAIN_MENU);
    }

    // ── תפריט עצירה: ESC ממשיך משחק או סוגר דיאלוג ──────────────
    private void pauseMenuKeys(int k) {
        if (k != KeyEvent.VK_ESCAPE) return;
        if (panel.pendingConfirm == ConfirmAction.NONE) {
            panel.resumeGame();
        } else {
            panel.pendingConfirm = ConfirmAction.NONE;
            panel.repaint();
        }
    }

    // ── מסך משחק ─────────────────────────────────────────────────
    private void playingKeys(int k) {
        GameSession session = panel.session;

        // ESC בזמן ריצה → תפריט עצירה
        if (k == KeyEvent.VK_ESCAPE && session.getState() == GameState.RUNNING) {
            panel.pauseGame();
            return;
        }

        // קיצורי Game Over
        if (session.getState() == GameState.GAME_OVER) {
            if (k == KeyEvent.VK_ENTER) { panel.startGame(session.getMode()); return; }
            if (k == KeyEvent.VK_M)     { panel.sound.stopMusic(); panel.goTo(Screen.MAIN_MENU); return; }
            if (k == KeyEvent.VK_H && session.getMode() == GameMode.SINGLE) {
                panel.goTo(Screen.HIGH_SCORES); return;
            }
        }

        if (session.getState() != GameState.RUNNING) return;

        // שחקן 1 – מקשי חצים
        Direction d1 = Direction.fromArrowKey(k);
        if (d1 != null) session.setPending1(d1);

        // שחקן 2 – WASD (רק ב-VS)
        if (session.getMode() == GameMode.VS) {
            Direction d2 = Direction.fromWasdKey(k);
            if (d2 != null) session.setPending2(d2);
        }
    }
}