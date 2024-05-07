import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuSolverLocalSearch {

    private static final int DOMAIN = 9; // 1-9
    private static final int SUBGRID_SIZE = 3;
    private static int[][] grid, originalGrid;
    private static long durationNotIncluded = 0;
    private static BufferedWriter writer;

    public static void main(String[] args) {
        SudokuGenerator generator = new SudokuGenerator(); // create a SudokuGenerator instance
        generator.generate(); // generate a Sudoku puzzle
        grid = generator.getGrid();// get grid puzzle
        grid=fillRemainingRandomly(grid);
                originalGrid=grid;
            durationNotIncluded=0;
            grid = SudokuGeneratorRandomly.generateSudoku();
            originalGrid=grid;
            System.out.println("The Sudoku puzzle initially: ");
            printBoard();
            try {
                writer = new BufferedWriter(new FileWriter("sudoku_local_search.txt"));
                writer.write("The Sudoku puzzle initially: ");
                writer.newLine();
                printBoardToFile();
               

            long startTime = System.currentTimeMillis(); // start timer
            solveSudoku(grid);
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            if(isSolved(grid)){
                System.out.println("\nThe Sudoku puzzle after solution: ");
                printBoard();
                System.out.println("Total time is " + duration + " milliseconds.");
                System.out.println("Sudoku solved in " + (duration - durationNotIncluded) + " milliseconds.");
                writer.write("The Sudoku puzzle after solution: ");
                writer.newLine();
                printBoardToFile();
                writer.write("Total time is " + duration + " milliseconds.");
                writer.newLine();
                writer.write("Sudoku solved in " + (duration - durationNotIncluded) + " milliseconds.");
                
            }else{
                System.out.println("\nNo solution found.");
                System.out.println("Total time is " + duration + " milliseconds.");  
                writer.write("No solution found.");
            
        }
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    }
    private static int[][] fillRemainingRandomly(int[][] grid) { //complete fo
        int[][] filledGrid = grid;

        for (int i = 0; i < DOMAIN; i++) { // Iterate through each row
            List<Integer> emptyCells = new ArrayList<>(); // Store indices of empty cells in this row
            for (int j = 0; j < DOMAIN; j++) { // Iterate through each column
                if (grid[i][j] == 0) { // If the cell is empty
                    emptyCells.add(j); // Add the index of the empty cell to the list
                }
            }

            List<Integer> remainingValues = new ArrayList<>(); // Store remaining values to be placed
            for (int num = 1; num <= DOMAIN; num++) {
                if (!containsValue(grid[i], num)) { // If the number is not already in the row
                    remainingValues.add(num); // Add it to the list of remaining values
                }
            }

            Random random = new Random();
            for (int emptyCellIndex : emptyCells) { // Iterate through empty cells in the row
                int randomIndex = random.nextInt(remainingValues.size()); // Choose a random index from the remaining values
                filledGrid[i][emptyCellIndex] = remainingValues.get(randomIndex); // Assign the value to the empty cell
                remainingValues.remove(randomIndex); // Remove the assigned value from the list of remaining values
            }
        }
        return filledGrid;
    }

    private static boolean containsValue(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }


    public static boolean solveSudoku(int[][] grid) {
        // Solve using min-conflicts heuristic
        for (int Iteration = 1; Iteration <= 10000; Iteration++) { // Set a maximum number of iterations
            long stopTime = System.currentTimeMillis();
            System.out.println("Iteration:" + Iteration);
            try {
                writer.write("Iteration:" + Iteration);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                System.out.println("No conflicted cell found.");
                try {
                    writer.write("No conflicted cell found.");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resumeTime = System.currentTimeMillis();
                durationNotIncluded += (resumeTime - stopTime);
                return false;
            }
            stopTime = System.currentTimeMillis();
            System.out.println("Conflicted cell: " + conflictedCell[0] + ", " + conflictedCell[1]);
            int count=countConflicts(grid, conflictedCell[0],conflictedCell[1]);
            System.out.println("Count conflicts: "+count );
            try {
                writer.write("Conflicted cell: " + conflictedCell[0] + ", " + conflictedCell[1]);
                writer.newLine();
                writer.write("Count conflicts: "+count);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);
            // Use the min-conflicts heuristic to find the appropriate value for the conflicted variable
            int value = findMinConflictValue(grid, conflictedCell[0], conflictedCell[1]);
            grid[conflictedCell[0]][conflictedCell[1]] = value;
            stopTime = System.currentTimeMillis();
            System.out.println("Changing value at " + conflictedCell[0] + ", " + conflictedCell[1] + " to " + value);
            System.out.println("Updated Grid:");
            printBoard();
            try {
                writer.write("Changing value at " + conflictedCell[0] + ", " + conflictedCell[1] + " to " + value);
                writer.newLine();
                writer.write("Updated Grid:");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            printBoardToFile();
            resumeTime = System.currentTimeMillis();
            durationNotIncluded += (resumeTime - stopTime);

            // Check if the solution is stuck and make swapping numbers to migrate the stuck
            if (Iteration % 10 == 0 && !isProgress(grid)) {
                stopTime = System.currentTimeMillis();
                System.out.println("Stuck! Swapping two numbers.");
                try {
                    writer.write("Stuck! Swapping two numbers.");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resumeTime = System.currentTimeMillis();
                durationNotIncluded += (resumeTime - stopTime);
                swapNumbers(grid);
            }
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Sudoku not solved within 10000 iterations.");
        try {
            writer.write("Sudoku not solved within 10000 iterations.");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long resumeTime = System.currentTimeMillis();
        durationNotIncluded += (resumeTime - stopTime);
        return false; // Failed to solve within the maximum number of iterations
    }

    private static boolean isProgress(int[][] grid) {
        // Compare the current grid with the original grid to check for changes
        for (int i = 0; i < DOMAIN; i++) {
            for (int j = 0; j < DOMAIN; j++) {
                if (grid[i][j] != originalGrid[i][j]) {
                    originalGrid=grid;
                    return true; // Progress made
                }
            }
        }
        return false; // No progress made
    }

    private static void swapNumbers(int[][] grid) {
        // Randomly select two different cells and swap their values
        Random random = new Random();
        int row1 = random.nextInt(DOMAIN);
        int col1 = random.nextInt(DOMAIN);
        int row2 = random.nextInt(DOMAIN);
        int col2 = random.nextInt(DOMAIN);
        while (row1 == row2 && col1 == col2) {
            // Ensure that the two cells are different for swapping
            row2 = random.nextInt(DOMAIN);
            col2 = random.nextInt(DOMAIN);
        }
        // Swap the 2 values
        int temp = grid[row1][col1];
        grid[row1][col1] = grid[row2][col2];
        grid[row2][col2] = temp;
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
            try {
                writer.write("Conflicted cells:");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            for (int[] cell : conflictedCells) {
                System.out.println(cell[0] + ", " + cell[1]);
                try {
                    writer.write(cell[0] + ", " + cell[1]);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            try {
                writer.write("No value can minumize conflicts");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    private static void printBoardToFile() {
        try {
            for (int i = 0; i < DOMAIN; i++) {
                for (int j = 0; j < DOMAIN; j++) {
                    if (j == 0)
                        writer.write("|");
                    writer.write(grid[i][j] + " ");
                    if (j == 8)
                        writer.write("|");
                }
                writer.newLine();
            }
            writer.write("********************");
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
