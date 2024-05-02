import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuSolverLocalSearch {

    private static final int DOMAIN = 9; // 1-9
    private static final int SUBGRID_SIZE = 3;
    private static int[][] grid;
    private static long durationNotIncluded = 0;

    public static void main(String[] args) {
        grid = SudokuGeneratorRandomly.generateSudoku();
        while(!isSolved(grid)){
            durationNotIncluded=0;
            grid = SudokuGeneratorRandomly.generateSudoku();
            System.out.println("The Sudoku puzzle initially: ");
            printBoard();
            long startTime = System.currentTimeMillis(); // start timer
            solveSudoku(grid);
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            if(isSolved(grid)){
                System.out.println("\nThe Sudoku puzzle after solution: ");
                printBoard();
                System.out.println("Total time is " + duration + " milliseconds.");
                System.out.println("Sudoku solved in " + (duration - durationNotIncluded) + " milliseconds.");
            }else{
                System.out.println("\nNo sloution found");
                System.out.println("Total time is " + duration + " milliseconds.");  
            }
        }
    
    }

    public static boolean solveSudoku(int[][] grid) {
        // Solve using min-conflicts heuristic
        for (int Iteration = 1; Iteration <= 300; Iteration++) { // Set a maximum number of iterations
            long stopTime = System.currentTimeMillis();
            System.out.println("Iteration:" + Iteration);
            long resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
            if (isSolved(grid)) {
                return true; // Puzzle solved
            }
            // Choose a random conflicted variable
            int[] conflictedCell = findConflictedCell(grid);
            if (conflictedCell == null) {
                // If no conflicted variable found, return false
                stopTime = System.currentTimeMillis();
                System.out.println("no conflictedCell");
                resumeTime = System.currentTimeMillis();
                durationNotIncluded += (resumeTime - stopTime);
                return false;
            }
            stopTime = System.currentTimeMillis();
            System.out.println("Conflicted cell: " + conflictedCell[0] + ", " + conflictedCell[1]);
            System.out.println("countConflicts: "+countConflicts(grid, conflictedCell[0],conflictedCell[1]) );
            resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
            // Use the min-conflicts heuristic to find the appropriate value for the conflicted variable
            int value = findMinConflictValue(grid, conflictedCell[0], conflictedCell[1]);
            grid[conflictedCell[0]][conflictedCell[1]] = value;
            stopTime = System.currentTimeMillis();
            System.out.println("Changing value at " + conflictedCell[0] + ", " + conflictedCell[1] + " to " + value);
            System.out.println("Updated Grid:");
            printBoard();
            resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Sudoku not solved within 1000 iterations.");
        long resumeTime = System.currentTimeMillis();
        durationNotIncluded += (resumeTime - stopTime);
        return false; // Failed to solve within the maximum number of iterations
    }

    private static boolean isSolved(int[][] grid) {
        // Check rows
        for (int row = 0; row < DOMAIN; row++) {
            for (int col = 0; col < DOMAIN; col++) {
                if (!isValidRow(grid,row,col)) {
                    return false;
                }
            }
        }
        
        // Check columns
        for (int col = 0; col < DOMAIN; col++) {
            for (int row = 0; row < DOMAIN; row++) {
                if (!isValidColumn(grid,row,col)) {
                    return false;
                }
            }
        }
        
        // Check 3x3 subgrids
        for (int startRow = 0; startRow < DOMAIN; startRow += SUBGRID_SIZE) {
            for (int startCol = 0; startCol < DOMAIN; startCol += SUBGRID_SIZE) {
                if (!isValidSubgrid(grid, startRow, startCol)) {
                    return false;
                }
            }
        }
        
        return true; // Sudoku grid is solved
    }
    
    private static boolean isValidSubgrid(int[][] grid, int startRow, int startCol) {
        boolean[] found = new boolean[DOMAIN];
        for (int row = startRow; row < startRow + SUBGRID_SIZE; row++) {
            for (int col = startCol; col < startCol + SUBGRID_SIZE; col++) {
                int num = grid[row][col];
                if (found[num - 1]) {
                    return false; // Duplicate number found
                }
                found[num - 1] = true;
            }
        }
        return true;
    }    

    private static int[] findConflictedCell(int[][] grid) {
        List<int[]> conflictedCells = new ArrayList<>();
        // Check rows
        for (int row = 0; row < DOMAIN; row++) {
            for (int col = 0; col < DOMAIN; col++) {
                if (!isValidRow(grid, row, col)) {
                    conflictedCells.add(new int[]{row, col});
                }
            }
        }
        // Check columns
        for (int col = 0; col < DOMAIN; col++) {
            for (int row = 0; row < DOMAIN; row++) {
                if (!isValidColumn(grid, row, col)) {
                    conflictedCells.add(new int[]{row, col});
                }
            }
        }
        // Check 3x3 subgrids
        for (int startRow = 0; startRow < DOMAIN; startRow += SUBGRID_SIZE) {
            for (int startCol = 0; startCol < DOMAIN; startCol += SUBGRID_SIZE) {
                if (!isValidSubgrid(grid, startRow, startCol)) {
                    boolean[] found = new boolean[DOMAIN];
                    for (int row = startRow; row < startRow + SUBGRID_SIZE; row++) {
                        for (int col = startCol; col < startCol + SUBGRID_SIZE; col++) {
                            int num = grid[row][col];
                            if (found[num - 1]) {
                                conflictedCells.add(new int[]{row, col});
                            } else {
                                found[num - 1] = true;
                            }
                        }
                    }
                }
            }
        }
        // If there are conflicted cells, return a random one
        if (!conflictedCells.isEmpty()) {
            long stopTime = System.currentTimeMillis();
            System.out.println("Conflicted cells:");
            for (int[] cell : conflictedCells) {
                System.out.println(cell[0] + ", " + cell[1]);
            }
            long resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
            Random rand = new Random();
            return conflictedCells.get(rand.nextInt(conflictedCells.size()));
        }
        return null; // No conflicted cells found
    }
    
    private static boolean isValidRow(int[][] grid, int row, int col) {
        int num = grid[row][col];
        for (int j = 0; j < DOMAIN; j++) {
            if (j != col && grid[row][j] == num) {
                return false; // Duplicate number found in the row
            }
        }
        return true;
    }
    
    private static boolean isValidColumn(int[][] grid, int row, int col) {
        int num = grid[row][col];
        for (int i = 0; i < DOMAIN; i++) {
            if (i != row && grid[i][col] == num) {
                return false; // Duplicate number found in the column
            }
        }
        return true;
    }

    private static int findMinConflictValue(int[][] grid, int row, int col) {
        List<Integer> validValues = new ArrayList<>();
        
        // Count conflicts for each possible value
        int minConflicts = Integer.MAX_VALUE;
        for (int num = 1; num <= DOMAIN; num++) {
            grid[row][col] = num; // Assign the number temporarily
            int conflicts = countConflicts(grid, row, col);
            if (conflicts <= minConflicts) {
                minConflicts = conflicts;
                validValues.clear();
                validValues.add(num);
            } else if (conflicts == minConflicts) {
                validValues.add(num);
            }
        }
        
        // If there are valid values, select a random one
        if (!validValues.isEmpty()) {
            long stopTime = System.currentTimeMillis();
            System.out.println("No value can minumize conflicts");
            long resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
            Random rand = new Random();
            return validValues.get(rand.nextInt(validValues.size()));
        } else {
            // If no valid values are found, return a random number from 1 to 9
            return (int) (Math.random() * DOMAIN) + 1;
        }
    }
    
    private static int countConflicts(int[][] grid, int row, int col) {
        int conflicts = 0;
        int num = grid[row][col];
        // Count conflicts in the row
        for (int j = 0; j < DOMAIN; j++) {
            if (j != col && grid[row][j] == num) {
                conflicts++;
            }
        }
        // Count conflicts in the column
        for (int i = 0; i < DOMAIN; i++) {
            if (i != row && grid[i][col] == num) {
                conflicts++;
            }
        }
        // Count conflicts in the subgrid
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int r = startRow; r < startRow + SUBGRID_SIZE; r++) {
            for (int c = startCol; c < startCol + SUBGRID_SIZE; c++) {
                if (r != row && c != col && grid[r][c] == num) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }

    private static void printBoard() {
        System.out.println("********************");
        for (int i = 0; i < DOMAIN; i++) {
            for (int j = 0; j < DOMAIN; j++) {
                if (j == 0)
                    System.out.print("|");
                System.out.print(grid[i][j] + " ");
                if (j == 8)
                    System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("********************");
        System.out.println();
    }
    
}
