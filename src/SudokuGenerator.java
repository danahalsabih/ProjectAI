import java.util.Random;

public class SudokuGenerator {

    private static final int SIZE = 9; // Size of the Sudoku grid
    private static final int SUBGRID_SIZE = 3; // Size of each subgrid
    private static final int EMPTY_CELL = 0; // Representation of an empty cell

    private int[][] grid; // Sudoku grid
    private Random random; // Random number generator

    public SudokuGenerator() {
        grid = new int[SIZE][SIZE]; // Initialize the Sudoku grid
        random = new Random(); // Initialize the random number generator
    }

    public void generate() {
        fillDiagonalSubgrids(); // Fill the diagonal subgrids
        fillRemaining(0, SUBGRID_SIZE); // Fill the remaining grid
        removeCells(); // Remove cells to make it a puzzle
    }

    private void fillDiagonalSubgrids() {
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) { // Loop through the grid with step of SUBGRID_SIZE
            fillSubgrid(i, i); // Fill each diagonal subgrid
        }
    }

    private void fillSubgrid(int row, int col) {
        int num;
        for (int i = 0; i < SUBGRID_SIZE; i++) { // Loop through rows of the subgrid
            for (int j = 0; j < SUBGRID_SIZE; j++) { // Loop through columns of the subgrid
                do {
                    num = random.nextInt(SIZE) + 1; // Generate a random number within the grid range
                } while (!isValid(row, col, num)); // Repeat until a valid number is found
                grid[row + i][col + j] = num; // Assign the valid number to the grid cell
            }
        }
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) { // Check if the number already exists in the row or column
                return false; // Invalid number
            }
        }
        int subgridRowStart = row - row % SUBGRID_SIZE; // Find the starting row index of the subgrid
        int subgridColStart = col - col % SUBGRID_SIZE; // Find the starting column index of the subgrid
        for (int i = 0; i < SUBGRID_SIZE; i++) { // Loop through rows of the subgrid
            for (int j = 0; j < SUBGRID_SIZE; j++) { // Loop through columns of the subgrid
                if (grid[subgridRowStart + i][subgridColStart + j] == num) { // Check if the number already exists in the subgrid
                    return false; // Invalid number
                }
            }
        }
        return true; // Valid number
    }

    private boolean fillRemaining(int row, int col) {
        if (col >= SIZE && row < SIZE - 1) { // If reached end of column and not end of row
            row++; // Move to the next row
            col = 0; // Reset column to 0
        }
        if (row >= SIZE && col >= SIZE) { // If reached the end of grid
            return true; // Grid is filled
        }
        if (row < SUBGRID_SIZE) {
            if (col < SUBGRID_SIZE) {
                col = SUBGRID_SIZE; // Skip filling cells in the same subgrid
            }
        } else if (row < SIZE - SUBGRID_SIZE) {
            if (col == (row / SUBGRID_SIZE) * SUBGRID_SIZE) {
                col += SUBGRID_SIZE; // Skip filling cells in the same subgrid
            }
        } else {
            if (col == SIZE - SUBGRID_SIZE) {
                row++; // Move to the next row
                col = 0; // Reset column to 0
                if (row >= SIZE) { // If reached the end of grid
                    return true; // Grid is filled
                }
            }
        }
        for (int num = 1; num <= SIZE; num++) { // Try filling the cell with numbers 1 to 9
            if (isValid(row, col, num)) { // Check if the number is valid
                grid[row][col] = num; // Assign the number to the cell
                if (fillRemaining(row, col + 1)) { // Recursively fill the remaining cells
                    return true; // Grid is filled
                }
                grid[row][col] = EMPTY_CELL; // Backtrack if filling the cell was unsuccessful
            }
        }
        return false; // Grid cannot be filled
    }

    private void removeCells() {
        Random random = new Random();
        int minCellsToRemove = 45; // Minimum number of cells to remove
        int maxCellsToRemove = 55; // Maximum number of cells to remove
        int cellsToRemove = random.nextInt(maxCellsToRemove - minCellsToRemove + 1) + minCellsToRemove; // Number of cells to remove randomly choosen
        System.out.println(cellsToRemove);
        
        while (cellsToRemove > 0) { // Remove cells until desired count is reached
            int row = random.nextInt(SIZE); // Generate random row index
            int col = random.nextInt(SIZE); // Generate random column index
            if (grid[row][col] != EMPTY_CELL) { // If the cell is not already empty
                grid[row][col] = EMPTY_CELL; // Make the cell empty
                cellsToRemove--; // Decrement the count of cells to remove
            }
        }
    }
    
    public int[][]  getGrid(){
        return grid;
    }

}
