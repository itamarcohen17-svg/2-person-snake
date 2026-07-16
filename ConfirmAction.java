/**
 * דיאלוג אישור ממתין – איזו פעולה מסוכנת מחכה לאישור YES/NO.
 * מחלקה עם קבועים בלבד (לא enum).
 */
public class ConfirmAction {

    public static final ConfirmAction NONE           = new ConfirmAction("NONE");            // אין דיאלוג פתוח
    public static final ConfirmAction RETURN_TO_MENU = new ConfirmAction("RETURN_TO_MENU");  // חזרה לתפריט
    public static final ConfirmAction EXIT_PROGRAM   = new ConfirmAction("EXIT_PROGRAM");    // יציאה מתפריט העצירה
    public static final ConfirmAction EXIT_MAIN      = new ConfirmAction("EXIT_MAIN");       // יציאה מהתפריט הראשי

    private final String name;

    private ConfirmAction(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}

