import java.util.*;

public class SudokuSolver_completeFormulation_localSearch {

    private static final int SIZE = 9;
    private static final int BOX_SIZE = 3;
    private static final double INITIAL_TEMPERATURE = 90000;
    private static final double COOLING_RATE = 0.9999; // Slower cooling

    private final int[][] board;

    public SudokuSolver_completeFormulation_localSearch() {
        this.board = new int[SIZE][SIZE];
    }

    public void solve() {
        generateValidInitialState(); // Generate guaranteed valid initial state
        printBoard();
        fillRemainingRandomly();
        printBoard();
        long startTime = System.currentTimeMillis(); // start timer
        simulatedAnnealing();
        long endTime = System.currentTimeMillis();
        if (isSolved(board)) {
            long duration = (endTime - startTime);
            System.out.println("Sudoku solved!");
            System.out.println("Total time is " + duration + " milliseconds.");
        } else {
            System.out.println("No solution found within constraints.");
        }

        printBoard();
    }

    private void generateValidInitialState() {
        System.out.println("Generating valid initial state with 20 numbers...");

        // Fill the board with zeros initially
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }

        // Random number generator
        Random random = new Random();

        // Fill 15 random cells with valid numbers
        int filledCells = 0;
        while (filledCells < 15) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            int num = random.nextInt(SIZE) + 1; // Generate numbers 1 to 9

            if (isValidMove(board, row, col, num)) {
                board[row][col] = num;
                filledCells++;
            }
        }

    }
    private  int[][] fillRemainingRandomly() { //complete fo
        int[][] filledGrid = board;

        for (int i = 0; i < SIZE; i++) { // Iterate through each row
            List<Integer> emptyCells = new ArrayList<>(); // Store indices of empty cells in this row
            for (int j = 0; j < SIZE; j++) { // Iterate through each column
                if (board[i][j] == 0) { // If the cell is empty
                    emptyCells.add(j); // Add the index of the empty cell to the list
                }
            }

            List<Integer> remainingValues = new ArrayList<>(); // Store remaining values to be placed
            for (int num = 1; num <= SIZE; num++) {
                if (!containsValue(board[i], num)) { // If the number is not already in the row
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

    private boolean isValidMove(int[][] board, int row, int col, int num) {
        // Check if the number is already present in the same row, column, or box
        return !usedInRow(board, row, num) && !usedInColumn(board, col, num) && !usedInBox(board, row - row % BOX_SIZE, col - col % BOX_SIZE, num);
    }

    private boolean usedInRow(int[][] board, int row, int num) {
        for (int col = 0; col < SIZE; col++) {
            if (board[row][col] == num) {
                return true; // Number is already present in the row
            }
        }
        return false;
    }

    private boolean usedInColumn(int[][] board, int col, int num) {
        for (int row = 0; row < SIZE; row++) {
            if (board[row][col] == num) {
                return true; // Number is already present in the column
            }
        }
        return false;
    }

    private boolean usedInBox(int[][] board, int startRow, int startCol, int num) {
        for (int row = 0; row < BOX_SIZE; row++) {
            for (int col = 0; col < BOX_SIZE; col++) {
                if (board[row + startRow][col + startCol] == num) {
                    return true; // Number is already present in the box
                }
            }
        }
        return false;
    }

    private static boolean isSolved(int[][] grid) {
        
        // Check columns
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                if (!isValidColumn(grid,row,col)) {
                    return false;
                }
            }
        }
        
        // Check 3x3 subgrids
        for (int startRow = 0; startRow < SIZE; startRow += 3) {
            for (int startCol = 0; startCol < SIZE; startCol += 3) {
                if (!isValidSubgrid(grid, startRow, startCol)) {
                    return false;
                }
            }
        }
        // Check rows
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isValidRow(grid,row,col)) {
                    return false;
                }
            }
        }
        
        return true; // Sudoku grid is solved
    }
    
    private static boolean isValidSubgrid(int[][] grid, int startRow, int startCol) {
        boolean[] found = new boolean[SIZE];
        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = startCol; col < startCol + 3; col++) {
                int num = grid[row][col];
                if (found[num - 1]) {
                    return false; // Duplicate number found
                }
                found[num - 1] = true;
            }
        }
        return true;
    }    

    private static boolean isValidRow(int[][] grid, int row, int col) {
        int num = grid[row][col];
        for (int j = 0; j < SIZE; j++) {
            if (j != col && grid[row][j] == num) {
                return false; // Duplicate number found in the row
            }
        }
        return true;
    }
    
    private static boolean isValidColumn(int[][] grid, int row, int col) {
        int num = grid[row][col];
        for (int i = 0; i < SIZE; i++) {
            if (i != row && grid[i][col] == num) {
                return false; // Duplicate number found in the column
            }
        }
        return true;
    }

    private void printBoard() {
        System.out.println("Sudoku Board:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void simulatedAnnealing() {
        double temperature = INITIAL_TEMPERATURE;
        int iter=0;
        while(temperature>1){
            if(iter%10==0 && isSolved(board)) {
                return;
            }
            iter++;
                int row1 = (int) (Math.random() * SIZE);
                int col1 = (int) (Math.random() * SIZE);
                int row2 = (int) (Math.random() * SIZE);
                int col2 = (int) (Math.random() * SIZE);

                    int tempVal = board[row1][col1];
                    board[row1][col1] = board[row2][col2];
                    board[row2][col2] = tempVal;
                   

                    int deltaE = calculateDeltaE(row1, col1, row2, col2);

                    double probability = Math.exp(-deltaE / temperature);
                    if (deltaE >= 0  || Math.random() > probability) {
                        continue;
                    } else {
                        // Revert swap if not accepted
                        tempVal = board[row1][col1];
                        board[row1][col1] = board[row2][col2];
                        board[row2][col2] = tempVal;
                        
                    }
                
            
            temperature *= COOLING_RATE;
        }
    }

    private int calculateDeltaE(int row1, int col1, int row2, int col2){
        int conflictsAfter = countConflicts();
        // Swap back to calculate conflicts before.
        int tempVal = board[row1][col1];
        board[row1][col1] = board[row2][col2];
        board[row2][col2] = tempVal;
        int conflictsBefore = countConflicts();

        tempVal = board[row1][col1];
        board[row1][col1] = board[row2][col2];
        board[row2][col2] = tempVal; // Revert the swap again
        return conflictsBefore - conflictsAfter;
    }

    private int countConflicts() {
        int conflicts = 0;

        // Check rows for duplicates
        for (int row = 0; row < SIZE; row++) {
            Set<Integer> set = new HashSet<>();
            for (int col = 0; col < SIZE; col++) {
                int num = board[row][col];
                if (num != 0 && !set.add(num)) {
                    conflicts++;
                }
            }
        }

        // Check columns for duplicates
        for (int col = 0; col < SIZE; col++) {
            Set<Integer> set = new HashSet<>();
            for (int row = 0; row < SIZE; row++) {
                int num = board[row][col];
                if (num != 0 && !set.add(num)) {
                    conflicts++;
                }
            }
        }

        // Check boxes for duplicates
        for (int startRow = 0; startRow < SIZE - BOX_SIZE + 1; startRow += BOX_SIZE) {
            for (int startCol = 0; startCol < SIZE - BOX_SIZE + 1; startCol += BOX_SIZE) {
                Set<Integer> set = new HashSet<>();
                for (int row = startRow; row < startRow + BOX_SIZE; row++) {
                    for (int col = startCol; col < startCol + BOX_SIZE; col++) {
                        int num = board[row][col];
                        if (num != 0 && !set.add(num)) {
                            conflicts++;
                        }
                    }
                }
            }
        }

        return conflicts;
    }


    public static void main(String[] args) {
        SudokuSolver_completeFormulation_localSearch solver = new SudokuSolver_completeFormulation_localSearch();
        solver.solve();
    }

}
