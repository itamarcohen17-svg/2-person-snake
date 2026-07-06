import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * מטפל בכל לחיצות העכבר – מנתב לפי המסך הנוכחי.
 * הופרד מ-GamePanel; ניגש לשדות הפאנל שהם package-private.
 */
public class Handler_Mouse extends MouseAdapter {

    private final GamePanel panel;

    public Handler_Mouse(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if      (panel.screen == Screen.MAIN_MENU)   mainMenuClick(p);
        else if (panel.screen == Screen.SETTINGS)    settingsClick(p);
        else if (panel.screen == Screen.HIGH_SCORES) highScoresClick(p);
        else if (panel.screen == Screen.PAUSE_MENU)  pauseMenuClick(p);
        else if (panel.screen == Screen.PLAYING)     playingClick(p);
    }

    // ── קיצורי גודל ──────────────────────────────────────────────
    private int w()  { return panel.getWidth(); }
    private int h()  { return panel.getHeight(); }
    private int bw() { return UISizes.btnW(w()); }
    private int bh() { return UISizes.btnH(h()); }

    // ═════════════════════════════════════════════════════════════
    //  תפריט ראשי
    // ═════════════════════════════════════════════════════════════
    private void mainMenuClick(Point p) {
        // אם דיאלוג יציאה פתוח – רק YES/NO פעילים
        if (panel.pendingConfirm == ConfirmAction.EXIT_MAIN) {
            Rectangle[] cb = UIHelper.confirmButtons(w(), h(), bw(), bh());
            if (cb[0].contains(p)) panel.executeConfirm();
            if (cb[1].contains(p)) { panel.pendingConfirm = ConfirmAction.NONE; panel.repaint(); }
            return;
        }
        Rectangle[] b = panel.mainMenuScreen.buttons(w(), h(), bw(), bh());
        if (b[0].contains(p)) panel.startGame(GameMode.SINGLE);
        if (b[1].contains(p)) panel.startGame(GameMode.VS);
        if (b[2].contains(p)) panel.goTo(Screen.SETTINGS);
        if (b[3].contains(p)) panel.goTo(Screen.HIGH_SCORES);
        if (b[4].contains(p)) { panel.pendingConfirm = ConfirmAction.EXIT_MAIN; panel.repaint(); }
    }

    // ═════════════════════════════════════════════════════════════
    //  הגדרות
    // ═════════════════════════════════════════════════════════════
    private void settingsClick(Point p) {
        GameSettings settings = panel.settings;

        // ריבועי צבע – שחקן 1
        Rectangle[] sw1 = panel.settingsScreen.swatches(1, w(), h());
        for (int i = 0; i < SettingsScreen.PALETTE.length; i++) {
            if (sw1[i].contains(p) && settings.trySetSnake1Color(SettingsScreen.PALETTE[i])) {
                panel.session.recolorSnake1(settings.getSnake1Color());
                panel.repaint(); return;
            }
        }
        // ריבועי צבע – שחקן 2
        Rectangle[] sw2 = panel.settingsScreen.swatches(2, w(), h());
        for (int i = 0; i < SettingsScreen.PALETTE.length; i++) {
            if (sw2[i].contains(p) && settings.trySetSnake2Color(SettingsScreen.PALETTE[i])) {
                panel.session.recolorSnake2(settings.getSnake2Color());
                panel.repaint(); return;
            }
        }
        // כפתורי צליל
        Rectangle[] st = panel.settingsScreen.soundToggles(w(), h());
        if (st[0].contains(p)) { settings.toggleMaster(); panel.repaint(); return; }
        if (st[1].contains(p)) { if (settings.toggleMusic()) panel.repaint(); return; }
        if (st[2].contains(p)) { if (settings.toggleSfx())   panel.repaint(); return; }

        // חזרה לתפריט
        if (panel.settingsScreen.backButton(w(), h(), bw(), bh()).contains(p))
            panel.goTo(Screen.MAIN_MENU);
    }

    // ═════════════════════════════════════════════════════════════
    //  טבלת שיאים
    // ═════════════════════════════════════════════════════════════
    private void highScoresClick(Point p) {
        if (panel.highScoresScreen.backButton(w(), h(), bw(), bh()).contains(p))
            panel.goTo(Screen.MAIN_MENU);
    }

    // ═════════════════════════════════════════════════════════════
    //  תפריט עצירה
    // ═════════════════════════════════════════════════════════════
    private void pauseMenuClick(Point p) {
        if (panel.pendingConfirm != ConfirmAction.NONE) {
            Rectangle[] cb = UIHelper.confirmButtons(w(), h(), bw(), bh());
            if (cb[0].contains(p)) panel.executeConfirm();
            if (cb[1].contains(p)) { panel.pendingConfirm = ConfirmAction.NONE; panel.repaint(); }
            return;
        }
        Rectangle[] b = panel.pauseMenuScreen.buttons(w(), h(), bw(), bh());
        if (b[0].contains(p)) panel.resumeGame();
        if (b[1].contains(p)) { panel.pendingConfirm = ConfirmAction.RETURN_TO_MENU; panel.repaint(); }
        if (b[2].contains(p)) { panel.pendingConfirm = ConfirmAction.EXIT_PROGRAM;   panel.repaint(); }
    }

    // ═════════════════════════════════════════════════════════════
    //  מסך משחק – לחיצות פעילות רק ב-Game Over
    // ═════════════════════════════════════════════════════════════
    private void playingClick(Point p) {
        GameSession session = panel.session;
        if (session.getState() != GameState.GAME_OVER) return;

        boolean single = (session.getMode() == GameMode.SINGLE);
        Rectangle[] b  = panel.gameOverScreen.buttons(w(), h(), bw(), bh(), single);

        if (b[0].contains(p)) { panel.startGame(session.getMode()); return; }
        if (single) {
            if (b[1].contains(p)) { panel.goTo(Screen.HIGH_SCORES); return; }
            if (b[2].contains(p))   panel.goTo(Screen.MAIN_MENU);
        } else {
            if (b[1].contains(p))   panel.goTo(Screen.MAIN_MENU);
        }
    }
}