public class GameConfig{
        public static int GRID_SIZE;
        public static int CELL_SIZE;
        public static int FPS;
        public static int DELAY_MS;

        //פונקציה לאיתחול הלוח משחק (גודל, קצב פיירמים לשנייה ודיליי קבועים)
        public GameConfig(int gridSize, int cellSize, int fps) {
            GRID_SIZE = gridSize;
            CELL_SIZE = cellSize;
            FPS = fps;
            DELAY_MS = 1000 / FPS;
        }
}
