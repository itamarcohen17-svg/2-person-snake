import java.awt.*;

/**
 * מסך הגדרות.
 * אחראי על: ציור בוחר הצבעים, כפתורי הצליל וכפתור חזרה.
 */
public class SettingsScreen {

    // פלטת הצבעים הזמינה
    public static final Color[]  PALETTE       = {
            Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW,
            Color.ORANGE, Color.MAGENTA, Color.BLUE
    };
    public static final String[] PALETTE_NAMES = {
            "Green", "Cyan", "Red", "Yellow", "Orange", "Magenta", "Blue"
    };

    // ── מיקומי אלמנטים ───────────────────────────────────────────

    /** ריבועי הצבעים של שחקן מסוים (1 או 2) */
    public Rectangle[] swatches(int player, int panelW, int panelH) {
        int rowY   = (player == 1) ? (int)(panelH * 0.26) : (int)(panelH * 0.44);
        int sw     = Math.max(36, panelW / 20);
        int gap    = sw + 10;
        int totalW = PALETTE.length * gap - 10;
        int startX = (panelW - totalW) / 2;
        Rectangle[] r = new Rectangle[PALETTE.length];
        for (int i = 0; i < PALETTE.length; i++)
            r[i] = new Rectangle(startX + i * gap, rowY, sw, sw);
        return r;
    }

    /** 3 כפתורי הצליל: Master / Music / SFX */
    public Rectangle[] soundToggles(int panelW, int panelH) {
        int cx     = panelW / 2;
        int bw     = Math.max(130, panelW / 8);
        int bh     = Math.max(38,  panelH / 14);
        int gap    = bw + 16;
        int y      = (int)(panelH * 0.63);
        int startX = cx - (3 * bw + 2 * 16) / 2;
        return new Rectangle[] {
                new Rectangle(startX,           y, bw, bh),
                new Rectangle(startX + gap,     y, bw, bh),
                new Rectangle(startX + gap * 2, y, bw, bh)
        };
    }

    /** כפתור חזרה לתפריט ראשי */
    public Rectangle backButton(int panelW, int panelH, int btnW, int btnH) {
        return new Rectangle(panelW / 2 - btnW / 2, (int)(panelH * 0.82), btnW, btnH);
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int titleFont, int bodyFont, int btnW, int btnH,
                     Color snake1Color, Color snake2Color,
                     boolean soundEnabled, boolean musicEnabled, boolean sfxEnabled) {

        int cx = panelW / 2;

        // כותרת
        g.setFont(new Font("Arial", Font.BOLD, titleFont));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "Settings";
        g.drawString(title, cx - fm.stringWidth(title) / 2, panelH / 9);

        drawColorRows(g, panelW, panelH, bodyFont, cx, snake1Color, snake2Color);
        drawSoundSection(g, panelW, panelH, bodyFont, cx,
                soundEnabled, musicEnabled, sfxEnabled);

        UIHelper.drawButton(g, backButton(panelW, panelH, btnW, btnH),
                "Back", Color.LIGHT_GRAY);
    }

    // ── שורות הצבעים ─────────────────────────────────────────────
    private void drawColorRows(Graphics g, int panelW, int panelH, int bodyFont, int cx,
                               Color snake1Color, Color snake2Color) {
        g.setFont(new Font("Arial", Font.BOLD, bodyFont));
        FontMetrics fm = g.getFontMetrics();

        // שורת P1
        g.setColor(snake1Color);
        String lbl1 = "P1 Snake Colour:";
        Rectangle[] sw1 = swatches(1, panelW, panelH);
        g.drawString(lbl1, cx - fm.stringWidth(lbl1) / 2, sw1[0].y - 10);
        for (int i = 0; i < PALETTE.length; i++)
            UIHelper.drawSwatch(g, sw1[i], PALETTE[i],
                    PALETTE[i].equals(snake1Color), PALETTE[i].equals(snake2Color));

        // שורת P2
        g.setColor(snake2Color);
        String lbl2 = "P2 Snake Colour:";
        Rectangle[] sw2 = swatches(2, panelW, panelH);
        g.drawString(lbl2, cx - fm.stringWidth(lbl2) / 2, sw2[0].y - 10);
        for (int i = 0; i < PALETTE.length; i++)
            UIHelper.drawSwatch(g, sw2[i], PALETTE[i],
                    PALETTE[i].equals(snake2Color), PALETTE[i].equals(snake1Color));
    }

    // ── קטע הצליל ────────────────────────────────────────────────
    private void drawSoundSection(Graphics g, int panelW, int panelH, int bodyFont, int cx,
                                  boolean soundEnabled, boolean musicEnabled, boolean sfxEnabled) {
        Rectangle[] st = soundToggles(panelW, panelH);

        g.setFont(new Font("Arial", Font.BOLD, bodyFont));
        g.setColor(Color.WHITE);
        String soundLbl = "Sound:";
        FontMetrics fm  = g.getFontMetrics();
        g.drawString(soundLbl, cx - fm.stringWidth(soundLbl) / 2, st[0].y - 12);

        UIHelper.drawToggle(g, st[0], "Master", soundEnabled, true);
        UIHelper.drawToggle(g, st[1], "Music",  musicEnabled, soundEnabled);
        UIHelper.drawToggle(g, st[2], "SFX",    sfxEnabled,   soundEnabled);
    }
}