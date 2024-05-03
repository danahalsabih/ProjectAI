public class SudokuGeneratorRandomly {
   
        private static final int DOMAIN = 9;
    
        public static int[][] generateSudoku() {
            int[][] grid = new int[DOMAIN][DOMAIN];
            fillCellsRandomly(grid);
            return grid;
        }
    
        private static void fillCellsRandomly(int[][] grid) {
            for (int i = 0; i < DOMAIN; i++) {
                for (int j = 0; j < DOMAIN; j++) {
                    int randomNumber = (int) (Math.random() * 9) + 1;
                    grid[i][j]=randomNumber;
                }
            }
        }

    }
    