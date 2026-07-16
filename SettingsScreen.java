import java.awt.*;

/**
 * מסך הגדרות.
 * אחראי על: ציור בוחר הצבעים, כפתורי השם, כפתורי הצליל וכפתור חזרה.
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
        int rowY   = (player == 1) ? (int)(panelH * 0.20) : (int)(panelH * 0.42);
        int sw     = Math.max(36, panelW / 20);
        int gap    = sw + 10;
        int totalW = PALETTE.length * gap - 10;
        int startX = (panelW - totalW) / 2;
        Rectangle[] r = new Rectangle[PALETTE.length];
        for (int i = 0; i < PALETTE.length; i++)
            r[i] = new Rectangle(startX + i * gap, rowY, sw, sw);
        return r;
    }

    /** כפתור הזנת השם – ממוקם מתחת לשורת הצבעים של אותו שחקן */
    public Rectangle nameButton(int player, int panelW, int panelH) {
        Rectangle[] sw = swatches(player, panelW, panelH);
        int bw = Math.max(180, panelW / 4);
        int bh = Math.max(30, panelH / 20);
        int y  = sw[0].y + sw[0].height + 10;
        return new Rectangle(panelW / 2 - bw / 2, y, bw, bh);
    }

    /** 3 כפתורי הצליל: Master / Music / SFX */
    public Rectangle[] soundToggles(int panelW, int panelH) {
        int cx     = panelW / 2;
        int bw     = Math.max(130, panelW / 8);
        int bh     = Math.max(38,  panelH / 14);
        int y      = (int)(panelH * 0.67);
        int startX = cx - (3 * bw + 2 * 16) / 2;
        int gap    = bw + 16;
        return new Rectangle[] {
                new Rectangle(startX,           y, bw, bh),
                new Rectangle(startX + gap,     y, bw, bh),
                new Rectangle(startX + gap * 2, y, bw, bh)
        };
    }

    /** כפתור חזרה לתפריט ראשי */
    public Rectangle backButton(int panelW, int panelH, int btnW, int btnH) {
        return new Rectangle(panelW / 2 - btnW / 2, (int)(panelH * 0.84), btnW, btnH);
    }

    // ── ציור ─────────────────────────────────────────────────────
    public void draw(Graphics g, int panelW, int panelH,
                     int titleFont, int bodyFont, int btnW, int btnH,
                     Color snake1Color, Color snake2Color,
                     String name1, String name2,
                     boolean soundEnabled, boolean musicEnabled, boolean sfxEnabled) {

        int cx = panelW / 2;

        // כותרת
        g.setFont(new Font("Arial", Font.BOLD, titleFont));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "Settings";
        g.drawString(title, cx - fm.stringWidth(title) / 2, panelH / 10);

        drawPlayerRow(g, 1, panelW, panelH, bodyFont, cx, snake1Color, snake2Color, name1);
        drawPlayerRow(g, 2, panelW, panelH, bodyFont, cx, snake2Color, snake1Color, name2);
        drawSoundSection(g, panelW, panelH, bodyFont, cx,
                soundEnabled, musicEnabled, sfxEnabled);

        UIHelper.drawButton(g, backButton(panelW, panelH, btnW, btnH),
                "Back", Color.LIGHT_GRAY);
    }

    // ── שורת שחקן: תווית, ריבועי צבע וכפתור שם ──────────────────
    private void drawPlayerRow(Graphics g, int player, int panelW, int panelH,
                               int bodyFont, int cx,
                               Color myColor, Color otherColor, String name) {
        g.setFont(new Font("Arial", Font.BOLD, bodyFont));
        FontMetrics fm = g.getFontMetrics();

        // תווית עם שם השחקן במקום P1/P2
        g.setColor(myColor);
        String label = name + " Snake Colour:";
        Rectangle[] sw = swatches(player, panelW, panelH);
        g.drawString(label, cx - fm.stringWidth(label) / 2, sw[0].y - 8);

        // ריבועי צבע – הצבע של השחקן השני חסום
        for (int i = 0; i < PALETTE.length; i++)
            UIHelper.drawSwatch(g, sw[i], PALETTE[i],
                    PALETTE[i].equals(myColor), PALETTE[i].equals(otherColor));

        // כפתור הזנת שם
        UIHelper.drawButton(g, nameButton(player, panelW, panelH),
                "Name: " + name, myColor);
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