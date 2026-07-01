import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // ───── Screens ────────────────────────────────────────────────
    public enum Screen {
        MAIN_MENU,      // main menu
        SETTINGS,       // settings / colour picker
        HIGH_SCORES,    // leaderboard
        PLAYING,        // active game (single or vs)
        PAUSE_MENU      // in-game pause menu
    }

    public enum GameMode  { SINGLE, VS }
    public enum GameState { COUNTDOWN, RUNNING, PAUSED, GAME_OVER }

    // ───── Screen / mode state ────────────────────────────────────
    private Screen    screen = Screen.MAIN_MENU;
    private GameMode  mode;
    private GameState state;

    // ───── Game objects ───────────────────────────────────────────
    private SnakeLinkedList snake1, snake2;
    private Food            food;

    // ───── Timers ─────────────────────────────────────────────────
    private Timer timer;
    private Timer countdownTimer;
    private Timer preCountTimer;

    // ───── Single-player stats ────────────────────────────────────
    private int foodCount = 0;
    private int score     = 0;

    // ───── VS timer ───────────────────────────────────────────────
    private int timeLeft = 180;   // used only in VS mode

    // ───── Input buffer ───────────────────────────────────────────
    private SnakeLinkedList.Direction pending1 = null;
    private SnakeLinkedList.Direction pending2 = null;
    private int tickCount = 0;

    // ───── VS result ───────────────────────────────────────────
    private String vsWinner = "";

    // ───── Countdown ──────────────────────────────────────────────
    private int preCount = 3;

    // ───── Settings ───────────────────────────────────────────────
    private Color snake1Color = Color.GREEN;
    private Color snake2Color = Color.CYAN;

    // Sound settings (audio files will be connected later)
    private boolean soundEnabled       = true;
    private boolean musicEnabled       = true;
    private boolean sfxEnabled         = true;

    // Available colour palette (neither snake can share a colour)
    private static final Color[] PALETTE = {
            Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW,
            Color.ORANGE, Color.MAGENTA, Color.BLUE
    };
    private static final String[] PALETTE_NAMES = {
            "Green", "Cyan", "Red", "Yellow", "Orange", "Magenta", "White"
    };

    // ───── High scores ────────────────────────────────────────────
    private final HighScoreBoard highScores = new HighScoreBoard();

    // ───── Sound ──────────────────────────────────────────────────
    private final SoundManager sound = new SoundManager();

    // ───── Pause-menu confirm dialog ──────────────────────────────
    // "confirm" = waiting for YES/NO on a dangerous action
    private enum ConfirmAction { NONE, RETURN_TO_MENU, EXIT_PROGRAM, EXIT_MAIN }
    private ConfirmAction pendingConfirm = ConfirmAction.NONE;

    // ───── Dynamic sizing helpers ─────────────────────────────────
    private int cellSize() {
        return Math.min(getWidth(), getHeight()) / GameConfig.GRID_SIZE;
    }
    private int boardPx()  { return cellSize() * GameConfig.GRID_SIZE; }
    private int offsetX()  { return (getWidth()  - boardPx()) / 2; }
    private int offsetY()  { return (getHeight() - boardPx()) / 2; }

    // ── Generic scaled sizes ──────────────────────────────────────
    private int titleFont()  { return Math.max(28, getWidth() / 14); }
    private int bodyFont()   { return Math.max(14, getWidth() / 40); }
    private int btnW()       { return Math.max(200, getWidth()  / 4); }
    private int btnH()       { return Math.max(48,  getHeight() / 12); }

    // ───── Constructor ────────────────────────────────────────────
    public GamePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(
                GameConfig.GRID_SIZE * GameConfig.CELL_SIZE,
                GameConfig.GRID_SIZE * GameConfig.CELL_SIZE));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleClick(e.getPoint()); }
        });
        timer = new Timer(GameConfig.DELAY_MS, this);
    }

    // ═════════════════════════════════════════════════════════════
    //  CLICK ROUTING
    // ═════════════════════════════════════════════════════════════
    private void handleClick(Point p) {
        switch (screen) {
            case MAIN_MENU:   handleMainMenuClick(p);   break;
            case SETTINGS:    handleSettingsClick(p);   break;
            case HIGH_SCORES: handleHighScoresClick(p); break;
            case PAUSE_MENU:  handlePauseMenuClick(p);  break;
            case PLAYING:     handlePlayingClick(p);    break;
        }
    }

    private void handlePlayingClick(Point p) {
        if (state != GameState.GAME_OVER) return;
        Rectangle[] b = gameOverButtons();
        if (mode == GameMode.SINGLE) {
            if (b[0].contains(p)) { startGame(mode);                              return; }
            if (b[1].contains(p)) { screen = Screen.HIGH_SCORES; repaint();       return; }
            if (b[2].contains(p)) { screen = Screen.MAIN_MENU;   repaint(); }
        } else {
            if (b[0].contains(p)) { startGame(mode);                              return; }
            if (b[1].contains(p)) { screen = Screen.MAIN_MENU;   repaint(); }
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  MAIN MENU
    // ═════════════════════════════════════════════════════════════
    /** Returns 5 buttons: Single, VS, Settings, High Scores, Exit */
    private Rectangle[] mainMenuButtons() {
        int cx  = getWidth()  / 2;
        int cy  = getHeight() / 2;
        int bw  = btnW(), bh = btnH();
        int gap = bh + 12;
        int startY = cy - gap * 2 + bh / 2;
        Rectangle[] r = new Rectangle[5];
        for (int i = 0; i < 5; i++)
            r[i] = new Rectangle(cx - bw / 2, startY + i * gap, bw, bh);
        return r;
    }

    private void handleMainMenuClick(Point p) {
        // If exit-confirm is open, only YES/NO buttons work
        if (pendingConfirm == ConfirmAction.EXIT_MAIN) {
            Rectangle[] cb = confirmButtons();
            if (cb[0].contains(p)) executeConfirm();
            if (cb[1].contains(p)) { pendingConfirm = ConfirmAction.NONE; repaint(); }
            return;
        }
        Rectangle[] b = mainMenuButtons();
        if (b[0].contains(p)) startGame(GameMode.SINGLE);
        if (b[1].contains(p)) startGame(GameMode.VS);
        if (b[2].contains(p)) { screen = Screen.SETTINGS;    repaint(); }
        if (b[3].contains(p)) { screen = Screen.HIGH_SCORES; repaint(); }
        if (b[4].contains(p)) confirmExit();
    }

    private void drawMainMenu(Graphics g) {
        int cx = getWidth() / 2;

        // Title
        g.setFont(new Font("Arial", Font.BOLD, titleFont()));
        g.setColor(Color.GREEN);
        FontMetrics fm = g.getFontMetrics();
        String title = "SNAKE";
        g.drawString(title, cx - fm.stringWidth(title) / 2, getHeight() / 5);

        // Buttons
        Rectangle[] b = mainMenuButtons();
        String[] labels = { "Single Player", "VS (2P)", "Settings", "High Scores", "Exit" };
        Color[]  colors = { Color.GREEN, Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.RED };
        for (int i = 0; i < 5; i++) drawButton(g, b[i], labels[i], colors[i]);

        // Key hints
        g.setFont(new Font("Arial", Font.PLAIN, bodyFont() - 2));
        g.setColor(Color.GRAY);
        String hint = "P1: Arrow keys  |  P2: WASD  |  Press 1 / 2 to start";
        fm = g.getFontMetrics();
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                b[4].y + b[4].height + 24);

        // Exit confirm overlay
        if (pendingConfirm == ConfirmAction.EXIT_MAIN) {
            g.setColor(new Color(0, 0, 0, 190));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font("Arial", Font.BOLD, bodyFont() + 2));
            g.setColor(Color.WHITE);
            String msg = "Are you sure you want to exit?";
            fm = g.getFontMetrics();
            g.drawString(msg, cx - fm.stringWidth(msg) / 2, getHeight() / 2 - btnH());
            Rectangle[] cb = confirmButtons();
            drawButton(g, cb[0], "Yes", Color.RED);
            drawButton(g, cb[1], "No",  Color.GREEN);
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  SETTINGS MENU
    // ═════════════════════════════════════════════════════════════
    // Layout: colour swatches for P1 (row 1) and P2 (row 2), then Back button
    private Rectangle[] settingsSwatches(int player) {
        // player 1 → row at 28 % height, player 2 → 46 %
        int rowY = (player == 1) ? (int)(getHeight() * 0.26) : (int)(getHeight() * 0.44);
        int sw   = Math.max(36, getWidth() / 20);
        int gap  = sw + 10;
        int totalW = PALETTE.length * gap - 10;
        int startX = (getWidth() - totalW) / 2;
        Rectangle[] r = new Rectangle[PALETTE.length];
        for (int i = 0; i < PALETTE.length; i++)
            r[i] = new Rectangle(startX + i * gap, rowY, sw, sw);
        return r;
    }

    // Three sound toggle buttons: Master / Music / SFX
    private Rectangle[] soundToggleButtons() {
        int cx  = getWidth() / 2;
        int bw  = Math.max(130, getWidth() / 8);
        int bh  = Math.max(38,  getHeight() / 14);
        int gap = bw + 16;
        int y   = (int)(getHeight() * 0.63);
        int totalW = 3 * bw + 2 * 16;
        int startX = cx - totalW / 2;
        return new Rectangle[] {
                new Rectangle(startX,           y, bw, bh),   // Master
                new Rectangle(startX + gap,     y, bw, bh),   // Music
                new Rectangle(startX + gap * 2, y, bw, bh)    // SFX
        };
    }

    private Rectangle settingsBackButton() {
        int bw = btnW(), bh = btnH();
        return new Rectangle(getWidth() / 2 - bw / 2,
                (int)(getHeight() * 0.82), bw, bh);
    }

    private void handleSettingsClick(Point p) {
        // P1 swatches
        Rectangle[] sw1 = settingsSwatches(1);
        for (int i = 0; i < PALETTE.length; i++) {
            if (sw1[i].contains(p) && !PALETTE[i].equals(snake2Color)) {
                snake1Color = PALETTE[i];
                if (snake1 != null) snake1.changeColor(snake1Color);
                repaint(); return;
            }
        }
        // P2 swatches
        Rectangle[] sw2 = settingsSwatches(2);
        for (int i = 0; i < PALETTE.length; i++) {
            if (sw2[i].contains(p) && !PALETTE[i].equals(snake1Color)) {
                snake2Color = PALETTE[i];
                if (snake2 != null) snake2.changeColor(snake2Color);
                repaint(); return;
            }
        }
        // Sound toggles
        Rectangle[] st = soundToggleButtons();
        if (st[0].contains(p)) {
            soundEnabled = !soundEnabled;
            if (!soundEnabled) { musicEnabled = false; sfxEnabled = false; }
            else               { musicEnabled = true;  sfxEnabled = true;  }
            sound.setSound(soundEnabled);
            sound.setMusic(musicEnabled);
            sound.setSfx(sfxEnabled);
            repaint(); return;
        }
        if (st[1].contains(p) && soundEnabled) {
            musicEnabled = !musicEnabled;
            sound.setMusic(musicEnabled);
            repaint(); return;
        }
        if (st[2].contains(p) && soundEnabled) {
            sfxEnabled = !sfxEnabled;
            sound.setSfx(sfxEnabled);
            repaint(); return;
        }

        // Back
        if (settingsBackButton().contains(p)) {
            screen = Screen.MAIN_MENU; repaint();
        }
    }

    private void drawSettings(Graphics g) {
        int cx = getWidth() / 2;
        int bf = bodyFont();

        // Title
        g.setFont(new Font("Arial", Font.BOLD, titleFont()));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "Settings";
        g.drawString(title, cx - fm.stringWidth(title) / 2, getHeight() / 9);

        g.setFont(new Font("Arial", Font.BOLD, bf));

        // P1 colour row
        g.setColor(snake1Color);
        String lbl1 = "P1 Snake Colour:";
        fm = g.getFontMetrics();
        Rectangle[] sw1 = settingsSwatches(1);
        g.drawString(lbl1, cx - fm.stringWidth(lbl1) / 2, sw1[0].y - 10);
        for (int i = 0; i < PALETTE.length; i++) {
            drawSwatch(g, sw1[i], PALETTE[i], PALETTE_NAMES[i],
                    PALETTE[i].equals(snake1Color), PALETTE[i].equals(snake2Color));
        }

        // P2 colour row
        g.setColor(snake2Color);
        String lbl2 = "P2 Snake Colour:";
        Rectangle[] sw2 = settingsSwatches(2);
        g.drawString(lbl2, cx - fm.stringWidth(lbl2) / 2, sw2[0].y - 10);
        for (int i = 0; i < PALETTE.length; i++) {
            drawSwatch(g, sw2[i], PALETTE[i], PALETTE_NAMES[i],
                    PALETTE[i].equals(snake2Color), PALETTE[i].equals(snake1Color));
        }

        // ── Sound section ─────────────────────────────────────────
        Rectangle[] st = soundToggleButtons();
        int lblY = st[0].y - 12;
        g.setFont(new Font("Arial", Font.BOLD, bf));
        g.setColor(Color.WHITE);
        String soundLbl = "Sound:";
        fm = g.getFontMetrics();
        g.drawString(soundLbl, cx - fm.stringWidth(soundLbl) / 2, lblY);

        // Master
        drawToggle(g, st[0], "Master", soundEnabled, true);
        // Music – greyed out when master is off
        drawToggle(g, st[1], "Music",  musicEnabled, soundEnabled);
        // SFX – greyed out when master is off
        drawToggle(g, st[2], "SFX",    sfxEnabled,   soundEnabled);

        drawButton(g, settingsBackButton(), "Back", Color.LIGHT_GRAY);
    }

    /** Draws an ON/OFF toggle button. disabled = cannot be clicked (greyed). */
    private void drawToggle(Graphics g, Rectangle r, String label,
                            boolean on, boolean enabled) {
        Color bg  = !enabled ? Color.DARK_GRAY : (on ? new Color(0, 140, 0) : new Color(120, 0, 0));
        Color fg  = enabled ? Color.WHITE : Color.GRAY;
        String txt = label + ": " + (on ? "ON" : "OFF");

        g.setColor(bg);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(enabled ? (on ? Color.GREEN : Color.RED) : Color.GRAY);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        int fs = Math.max(11, r.height / 3);
        g.setFont(new Font("Arial", Font.BOLD, fs));
        g.setColor(fg);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(txt,
                r.x + (r.width  - fm.stringWidth(txt)) / 2,
                r.y + (r.height + fm.getAscent()) / 2 - 4);
    }

    /** Draws one colour swatch square with selection/forbidden indicators. */
    private void drawSwatch(Graphics g, Rectangle r, Color c,
                            String name, boolean selected, boolean forbidden) {
        g.setColor(forbidden ? Color.DARK_GRAY : c);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);

        if (selected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 8, 8);
            g2.setStroke(new BasicStroke(1));
        }
        if (forbidden) {
            // Draw an X
            g.setColor(Color.RED);
            g.drawLine(r.x + 4, r.y + 4, r.x + r.width - 4, r.y + r.height - 4);
            g.drawLine(r.x + r.width - 4, r.y + 4, r.x + 4, r.y + r.height - 4);
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  HIGH SCORES
    // ═════════════════════════════════════════════════════════════
    private Rectangle highScoresBackButton() {
        int bw = btnW(), bh = btnH();
        return new Rectangle(getWidth() / 2 - bw / 2,
                (int)(getHeight() * 0.82), bw, bh);
    }

    private void handleHighScoresClick(Point p) {
        if (highScoresBackButton().contains(p)) {
            screen = Screen.MAIN_MENU; repaint();
        }
    }

    private void drawHighScores(Graphics g) {
        int cx = getWidth() / 2;
        int bf = bodyFont();

        // Title
        g.setFont(new Font("Arial", Font.BOLD, titleFont()));
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        String title = "High Scores";
        g.drawString(title, cx - fm.stringWidth(title) / 2, getHeight() / 6);

        // Scores list
        int[] scores = highScores.getScores();
        String[] ordinals = { "1st", "2nd", "3rd", "4th", "5th" };
        Color[]  rowColors = { Color.YELLOW, Color.LIGHT_GRAY, new Color(205,127,50),
                Color.WHITE,  Color.WHITE };
        int lineH = Math.max(30, getHeight() / 12);
        int startY = getHeight() / 4;

        g.setFont(new Font("Arial", Font.BOLD, bf + 2));
        for (int i = 0; i < 5; i++) {
            g.setColor(rowColors[i]);
            String line = ordinals[i] + "   " + (scores[i] == 0 ? "---" : scores[i]);
            fm = g.getFontMetrics();
            g.drawString(line, cx - fm.stringWidth(line) / 2, startY + i * lineH);
        }

        drawButton(g, highScoresBackButton(), "Back", Color.LIGHT_GRAY);
    }

    // ═════════════════════════════════════════════════════════════
    //  PAUSE MENU
    // ═════════════════════════════════════════════════════════════
    /** Returns 3 buttons: Resume, Return to Menu, Exit Program */
    private Rectangle[] pauseButtons() {
        int cx  = getWidth()  / 2;
        int cy  = getHeight() / 2;
        int bw  = btnW(), bh = btnH();
        int gap = bh + 14;
        int startY = cy - gap + bh / 2;
        Rectangle[] r = new Rectangle[3];
        for (int i = 0; i < 3; i++)
            r[i] = new Rectangle(cx - bw / 2, startY + i * gap, bw, bh);
        return r;
    }

    /** YES / NO confirm buttons (shown on top of pause menu). */
    private Rectangle[] confirmButtons() {
        int cx = getWidth() / 2, cy = getHeight() / 2;
        int bw = btnW() / 2 - 10, bh = btnH();
        return new Rectangle[] {
                new Rectangle(cx - bw - 10, cy + bh, bw, bh),   // YES
                new Rectangle(cx + 10,      cy + bh, bw, bh)    // NO
        };
    }

    private void handlePauseMenuClick(Point p) {
        if (pendingConfirm != ConfirmAction.NONE) {
            Rectangle[] cb = confirmButtons();
            if (cb[0].contains(p)) executeConfirm();   // YES
            if (cb[1].contains(p)) { pendingConfirm = ConfirmAction.NONE; repaint(); } // NO
            return;
        }
        Rectangle[] b = pauseButtons();
        if (b[0].contains(p)) resumeGame();
        if (b[1].contains(p)) {
            pendingConfirm = ConfirmAction.RETURN_TO_MENU; repaint();
        }
        if (b[2].contains(p)) {
            pendingConfirm = ConfirmAction.EXIT_PROGRAM;   repaint();
        }
    }

    private void drawPauseMenu(Graphics g) {
        // Draw the frozen game in background
        drawGame(g);

        // Dark overlay over full panel
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        int cx = getWidth() / 2;

        // Title
        g.setFont(new Font("Arial", Font.BOLD, titleFont()));
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        String title = "PAUSED";
        g.drawString(title, cx - fm.stringWidth(title) / 2, getHeight() / 4);

        if (pendingConfirm != ConfirmAction.NONE) {
            // Confirm dialog
            String msg = (pendingConfirm == ConfirmAction.EXIT_PROGRAM)
                    ? "Exit? Game data will be lost."
                    : "Return to menu? Game data will be lost.";
            g.setFont(new Font("Arial", Font.PLAIN, bodyFont()));
            g.setColor(Color.WHITE);
            fm = g.getFontMetrics();
            g.drawString(msg, cx - fm.stringWidth(msg) / 2, getHeight() / 2);

            Rectangle[] cb = confirmButtons();
            drawButton(g, cb[0], "Yes", Color.RED);
            drawButton(g, cb[1], "No",  Color.GREEN);
        } else {
            // Normal pause buttons
            Rectangle[] b = pauseButtons();
            drawButton(g, b[0], "Resume",           Color.GREEN);
            drawButton(g, b[1], "Return to Menu",   Color.YELLOW);
            drawButton(g, b[2], "Exit Program",      Color.RED);

            g.setFont(new Font("Arial", Font.PLAIN, bodyFont() - 2));
            g.setColor(Color.GRAY);
            String hint = "Press ESC to resume";
            fm = g.getFontMetrics();
            g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                    b[2].y + b[2].height + 20);
        }
    }

    private void resumeGame() {
        pendingConfirm = ConfirmAction.NONE;
        screen = Screen.PLAYING;
        state  = GameState.RUNNING;
        timer.start();
        // Resume VS countdown from where it was (don't call startCountdown – that resets it)
        if (mode == GameMode.VS && countdownTimer != null) countdownTimer.start();
        repaint();
    }

    private void confirmExit() {
        // Show confirmation dialog even from main menu
        pendingConfirm = ConfirmAction.EXIT_MAIN;
        repaint();
    }

    private void executeConfirm() {
        switch (pendingConfirm) {
            case RETURN_TO_MENU:
                stopAllTimers();
                pendingConfirm = ConfirmAction.NONE;
                screen = Screen.MAIN_MENU;
                sound.stopMusic();   // silence in menus
                repaint();
                break;
            case EXIT_PROGRAM:
            case EXIT_MAIN:
                System.exit(0);
                break;
            default: break;
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  GAME START / LIFECYCLE
    // ═════════════════════════════════════════════════════════════
    private void startGame(GameMode chosen) {
        stopAllTimers();

        mode       = chosen;
        state      = GameState.COUNTDOWN;
        screen     = Screen.PLAYING;
        pending1   = null;
        pending2   = null;
        vsWinner = "";
        tickCount  = 0;
        preCount   = 3;
        pendingConfirm = ConfirmAction.NONE;

        int midY   = GameConfig.GRID_SIZE / 2;
        int p1Head = GameConfig.GRID_SIZE / 4 + 2;
        int p2Head = GameConfig.GRID_SIZE - GameConfig.GRID_SIZE / 4 - 2;

        snake1 = new SnakeLinkedList(snake1Color, SnakeLinkedList.Direction.RIGHT,
                p1Head, midY, false);
        if (mode == GameMode.VS) {
            snake2 = new SnakeLinkedList(snake2Color, SnakeLinkedList.Direction.LEFT,
                    p2Head, midY, true);
        } else {
            snake2 = null;
        }

        food = new Food();
        respawnFoodSafely();

        foodCount = 0;
        score     = 0;
        if (mode == GameMode.VS) {
            timeLeft = 180;   // VS has a countdown timer
        }

        repaint();
        requestFocusInWindow();

        // 3-2-1 pre-game countdown
        preCountTimer = new Timer(1000, null);
        preCountTimer.addActionListener(ev -> {
            preCount--;
            if (preCount <= 0) {
                preCountTimer.stop();
                state = GameState.RUNNING;
                timer.start();
                if (mode == GameMode.VS) startCountdown();
                sound.playTheme();   // theme starts only when game actually begins
            }
            repaint();
        });
        sound.stopMusic();     // silence any previous theme
        sound.playStart();     // start_effect during 3-2-1
        preCountTimer.start();
    }

    private void stopAllTimers() {
        timer.stop();
        if (countdownTimer != null) { countdownTimer.stop(); countdownTimer = null; }
        if (preCountTimer  != null) { preCountTimer.stop();  preCountTimer  = null; }
    }

    /** Pauses game timers without destroying the countdown (so resume can continue it). */
    private void pauseTimers() {
        timer.stop();
        if (countdownTimer != null) countdownTimer.stop();   // keep reference, don't null it
        if (preCountTimer  != null) preCountTimer.stop();
    }

    private void respawnFoodSafely() {
        List<Point> occupied = new ArrayList<>(snake1.getBodyPoints());
        if (snake2 != null) occupied.addAll(snake2.getBodyPoints());
        food.respawn(occupied);
    }

    private void startCountdown() {
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, ev -> {
            if (state != GameState.RUNNING) return;
            timeLeft--;
            if (timeLeft <= 0) endGame("time");
        });
        countdownTimer.start();
    }

    // ═════════════════════════════════════════════════════════════
    //  GAME LOOP
    // ═════════════════════════════════════════════════════════════
    @Override
    public void actionPerformed(ActionEvent e) {
        if (state != GameState.RUNNING) return;

        if (pending1 != null) { snake1.setDirection(pending1); pending1 = null; }
        if (pending2 != null && snake2 != null) { snake2.setDirection(pending2); pending2 = null; }

        snake1.move();
        if (mode == GameMode.VS && snake2 != null) snake2.move();

        checkEating();
        tickCount++;
        if (tickCount > 1) checkCollisions();
        repaint();
    }

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

    private void checkCollisions() {
        if (snake1.collidesWithSelf()) snake1.kill();
        if (mode == GameMode.VS && snake2 != null && snake2.collidesWithSelf()) snake2.kill();

        if (mode == GameMode.VS && snake2 != null) {
            if (snake1.collidesWithOther(snake2)) snake1.kill();
            if (snake2.collidesWithOther(snake1)) snake2.kill();
            // Head-on-head: larger snake wins; equal size → both die
            if (snake1.headOnHead(snake2)) {
                int s1 = snake1.getSize(), s2 = snake2.getSize();
                if      (s1 > s2) snake2.kill();
                else if (s2 > s1) snake1.kill();
                else              { snake1.kill(); snake2.kill(); }
            }
        }

        if (mode == GameMode.SINGLE) {
            if (!snake1.isAlive()) endGame("dead");
        } else {
            boolean s1Dead = !snake1.isAlive();
            boolean s2Dead = snake2 != null && !snake2.isAlive();
            if      (s1Dead && s2Dead) { vsWinner = "Draw!";    endGame("draw");   }
            else if (s1Dead)           { vsWinner = "P2 Wins!"; endGame("p2wins"); }
            else if (s2Dead)           { vsWinner = "P1 Wins!"; endGame("p1wins"); }
        }
    }

    private void endGame(String reason) {
        state = GameState.GAME_OVER;
        stopAllTimers();
        sound.stopMusic();
        boolean isNewRecord = false;
        if (mode == GameMode.SINGLE) {
            score = (foodCount + (180 - timeLeft)) * 10;
            // isNewRecord only when beating the current #1 score (scores[0])
            isNewRecord = score > highScores.getScores()[0];
            highScores.submit(score);
        }
        sound.playLose(isNewRecord);
        repaint();
    }

    // ═════════════════════════════════════════════════════════════
    //  PAINTING
    // ═════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (screen) {
            case MAIN_MENU:   drawMainMenu(g);   break;
            case SETTINGS:    drawSettings(g);   break;
            case HIGH_SCORES: drawHighScores(g); break;
            case PLAYING:     drawPlayScreen(g); break;
            case PAUSE_MENU:  drawPauseMenu(g);  break;
        }
    }

    // ── Playing screen ────────────────────────────────────────────
    private void drawPlayScreen(Graphics g) {
        drawGame(g);
        if (state == GameState.COUNTDOWN) drawCountdown(g);
        if (state == GameState.GAME_OVER) drawGameOver(g);
    }

    private void drawGame(Graphics g) {
        int cs = cellSize(), ox = offsetX(), oy = offsetY();
        drawGrid(g, cs, ox, oy);
        food.draw(g, cs, ox, oy);
        snake1.draw(g, cs, ox, oy);
        if (mode == GameMode.VS && snake2 != null) snake2.draw(g, cs, ox, oy);
        drawHUD(g, cs, ox, oy);
    }

    private void drawGrid(Graphics g, int cs, int ox, int oy) {
        g.setColor(new Color(25, 25, 25));
        int bp = boardPx();
        for (int i = 0; i <= GameConfig.GRID_SIZE; i++) {
            g.drawLine(ox + i * cs, oy,      ox + i * cs, oy + bp);
            g.drawLine(ox,    oy + i * cs,   ox + bp,     oy + i * cs);
        }
    }

    private void drawHUD(Graphics g, int cs, int ox, int oy) {
        int fs = Math.max(12, cs * 3 / 4);
        int bp = boardPx();
        g.setFont(new Font("Arial", Font.BOLD, fs));
        if (mode == GameMode.SINGLE) {
            g.setColor(Color.WHITE);
            g.drawString("Food: " + foodCount, ox + 5, oy + fs);
        } else {
            // VS mode – show timer and player labels
            int mins = timeLeft / 60, secs = timeLeft % 60;
            g.setColor(Color.WHITE);
            g.drawString("Time: " + mins + ":" + String.format("%02d", secs),
                    ox + bp / 2 - fs * 2, oy + fs);
            g.setColor(snake1Color);
            g.drawString("P1", ox + 5, oy + fs);
            g.setColor(snake2Color);
            g.drawString("P2", ox + bp - fs * 2, oy + fs);
        }
    }

    private void drawCountdown(Graphics g) {
        int ox = offsetX(), oy = offsetY(), bp = boardPx();
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(ox, oy, bp, bp);
        int numSize = Math.max(60, bp / 4);
        g.setFont(new Font("Arial", Font.BOLD, numSize));
        g.setColor(Color.WHITE);
        String num = String.valueOf(preCount);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(num,
                ox + (bp - fm.stringWidth(num)) / 2,
                oy + (bp + fm.getAscent()) / 2 - fm.getDescent());
    }

    // ── Game Over buttons (computed dynamically like all other buttons) ──
    private Rectangle[] gameOverButtons() {
        int cx  = getWidth()  / 2;
        int cy  = getHeight() / 2;
        int bw  = btnW();
        int bh  = btnH();
        int gap = bh + 12;
        // Single: 3 buttons (New Game, High Scores, Main Menu)
        // VS:     2 buttons (New Game, Main Menu)
        int count  = (mode == GameMode.SINGLE) ? 3 : 2;
        int startY = cy + (int)(getHeight() * 0.08);
        Rectangle[] r = new Rectangle[count];
        for (int i = 0; i < count; i++)
            r[i] = new Rectangle(cx - bw / 2, startY + i * gap, bw, bh);
        return r;
    }

    private void drawGameOver(Graphics g) {
        // Dark overlay over entire panel
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        int cx = getWidth()  / 2;
        int cy = getHeight() / 2;

        // ── Title ─────────────────────────────────────────────────
        String title = (mode == GameMode.SINGLE) ? "GAME OVER" : vsWinner;
        int titleSize = Math.max(32, getWidth() / 10);
        g.setFont(new Font("Arial", Font.BOLD, titleSize));
        g.setColor(Color.RED);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, cx - fm.stringWidth(title) / 2,
                cy - (int)(getHeight() * 0.18));

        // ── Score / result line ────────────────────────────────────
        if (mode == GameMode.SINGLE) {
            boolean newRecord = score > highScores.getScores()[0];
            String scoreLine  = "Score: " + score;
            int scoreSize     = Math.max(18, getWidth() / 22);
            g.setFont(new Font("Arial", Font.BOLD, scoreSize));
            g.setColor(newRecord ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics();
            g.drawString(scoreLine, cx - fm.stringWidth(scoreLine) / 2,
                    cy - (int)(getHeight() * 0.06));

            if (newRecord) {
                String rec = "★  NEW HIGH SCORE!  ★";
                int recSize = Math.max(14, getWidth() / 30);
                g.setFont(new Font("Arial", Font.BOLD, recSize));
                g.setColor(Color.YELLOW);
                fm = g.getFontMetrics();
                g.drawString(rec, cx - fm.stringWidth(rec) / 2,
                        cy - (int)(getHeight() * 0.06) + scoreSize + 6);
            }
        }

        // ── Buttons ───────────────────────────────────────────────
        Rectangle[] b = gameOverButtons();
        if (mode == GameMode.SINGLE) {
            drawButton(g, b[0], "New Game",    Color.GREEN);
            drawButton(g, b[1], "High Scores", Color.YELLOW);
            drawButton(g, b[2], "Main Menu",   Color.LIGHT_GRAY);
        } else {
            drawButton(g, b[0], "New Game",  Color.GREEN);
            drawButton(g, b[1], "Main Menu", Color.LIGHT_GRAY);
        }

        // ── Key hints ─────────────────────────────────────────────
        int hintSize = Math.max(11, getWidth() / 55);
        g.setFont(new Font("Arial", Font.PLAIN, hintSize));
        g.setColor(Color.GRAY);
        String hint = (mode == GameMode.SINGLE)
                ? "ENTER – New Game  |  H – High Scores  |  M – Main Menu"
                : "ENTER – New Game  |  M – Main Menu";
        fm = g.getFontMetrics();
        Rectangle[] btns = gameOverButtons();
        g.drawString(hint, cx - fm.stringWidth(hint) / 2,
                btns[btns.length - 1].y + btns[btns.length - 1].height + 20);
    }

    // ── Shared button renderer ────────────────────────────────────
    private void drawButton(Graphics g, Rectangle r, String text, Color c) {
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        g.setColor(c);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        int fs = Math.max(12, r.height / 3);
        g.setFont(new Font("Arial", Font.BOLD, fs));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text,
                r.x + (r.width  - fm.stringWidth(text)) / 2,
                r.y + (r.height + fm.getAscent()) / 2 - 4);
    }

    // ═════════════════════════════════════════════════════════════
    //  KEYBOARD
    // ═════════════════════════════════════════════════════════════
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        // ── Main menu shortcuts ───────────────────────────────────
        if (screen == Screen.MAIN_MENU) {
            if (k == KeyEvent.VK_1) startGame(GameMode.SINGLE);
            if (k == KeyEvent.VK_2) startGame(GameMode.VS);
            return;
        }

        // ── Settings / high scores: ESC → back ───────────────────
        if (screen == Screen.SETTINGS || screen == Screen.HIGH_SCORES) {
            if (k == KeyEvent.VK_ESCAPE) { screen = Screen.MAIN_MENU; repaint(); }
            return;
        }

        // ── Pause menu ────────────────────────────────────────────
        if (screen == Screen.PAUSE_MENU) {
            if (pendingConfirm == ConfirmAction.NONE) {
                if (k == KeyEvent.VK_ESCAPE) resumeGame();
            } else {
                if (k == KeyEvent.VK_ESCAPE) { pendingConfirm = ConfirmAction.NONE; repaint(); }
            }
            return;
        }

        // ── Playing screen ────────────────────────────────────────
        if (screen == Screen.PLAYING) {

            // ESC → open pause menu
            if (k == KeyEvent.VK_ESCAPE && state == GameState.RUNNING) {
                state  = GameState.PAUSED;
                screen = Screen.PAUSE_MENU;
                pauseTimers();
                repaint();
                return;
            }

            // Game-over shortcuts
            if (state == GameState.GAME_OVER) {
                if (k == KeyEvent.VK_ENTER) { startGame(mode);           return; }
                if (k == KeyEvent.VK_M) { sound.stopMusic(); screen = Screen.MAIN_MENU; repaint(); return; }
                if (k == KeyEvent.VK_H && mode == GameMode.SINGLE) {
                    screen = Screen.HIGH_SCORES; repaint(); return;
                }
            }

            if (state != GameState.RUNNING) return;

            // Snake 1 – arrow keys
            if (snake1 != null) {
                SnakeLinkedList.Direction d = null;
                if (k == KeyEvent.VK_UP)    d = SnakeLinkedList.Direction.UP;
                if (k == KeyEvent.VK_DOWN)  d = SnakeLinkedList.Direction.DOWN;
                if (k == KeyEvent.VK_LEFT)  d = SnakeLinkedList.Direction.LEFT;
                if (k == KeyEvent.VK_RIGHT) d = SnakeLinkedList.Direction.RIGHT;
                if (d != null) pending1 = d;
            }

            // Snake 2 – WASD
            if (mode == GameMode.VS && snake2 != null) {
                SnakeLinkedList.Direction d = null;
                if (k == KeyEvent.VK_W) d = SnakeLinkedList.Direction.UP;
                if (k == KeyEvent.VK_S) d = SnakeLinkedList.Direction.DOWN;
                if (k == KeyEvent.VK_A) d = SnakeLinkedList.Direction.LEFT;
                if (k == KeyEvent.VK_D) d = SnakeLinkedList.Direction.RIGHT;
                if (d != null) pending2 = d;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e)    {}
}