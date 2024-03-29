import java.util.*;

public class SudokuSolver{
    private static final int DOMAIN = 9; //1-9
    private static final int EMPTY = 0; //assume empty cells is 0
    private static int[][] board;

    public static void main(String[] args) {

        SudokuGenerator generator = new SudokuGenerator(); // create a SudokuGenerator instance
        generator.generate(); // generate a Sudoku grid
        board=generator.getGrid();// get grid puzzle

        System.out.println("The Sudoku puzzle initially: ");
        printBoard();

        long startTime = System.nanoTime(); // start timer

        boolean isSolved= backtrackingAlgorthim(); // try to solve sudoku puzzle

        if (isSolved) { 
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println("The Sudoku puzzle after solution: ");
            printBoard();
            System.out.println("Sudoku solved in " + duration + " nanoseconds.");
        } else {
            System.out.println("No solution found.");
        }
    }

    private static boolean backtrackingAlgorthim() {
        int[] nextCell = findEmptyCellByMRV(); // using MVC (Minimum Remaining Values)
        if (nextCell == null) {
            return true; // Puzzle solved
        }

        int row = nextCell[0];
        int col = nextCell[1];

        // assigning values from 1 to 9 by LCV (Least Constraining Value)
        List<Integer> possibleValues = findPossibleValuesByLCV(row, col);
        for (int value : possibleValues) {
            board[row][col] = value;
            System.out.println("Assign the value: "+value+ " to cell that is in row: "+ (row+1) +" and column: "+(col+1));
            printBoard();
            if (backtrackingAlgorthim()) {
                return true; // Solution found
            }
            // Backtrack
            System.out.println("Backtrack");
            board[row][col] = EMPTY;
        }
        return false; // No solution found
    }

    private static int[] findEmptyCellByMRV() {
        int minRemainingValues = DOMAIN + 1;
        int[] nextCell = null;

        for (int row = 0; row < DOMAIN; row++) {
            for (int col = 0; col < DOMAIN; col++) {
                if (board[row][col] == EMPTY) {
                    int remainingValues = findPossibleValuesByLCV(row, col).size();
                    if (remainingValues < minRemainingValues) {
                        minRemainingValues = remainingValues;
                        nextCell = new int[]{row, col};
                    }
                }
            }
        }
        return nextCell;
    }

    private static List<Integer> findPossibleValuesByLCV(int row, int col) {
        Set<Integer> values = new HashSet<>();
        for (int i = 1; i <= DOMAIN; i++) {
            values.add(i);
        }
        // exclude values in the same row
        for (int c = 0; c < DOMAIN; c++) {
            values.remove(board[row][c]);
        }
        // exclude values in the same column
        for (int r = 0; r < DOMAIN; r++) {
            values.remove(board[r][col]);
        }
        // exclude values in the same 3x3 subgrid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                values.remove(board[r][c]);
            }
        }
        return new ArrayList<>(values);
    }

    private static void printBoard() {
        for (int i = 0; i < DOMAIN; i++) {
            for (int j = 0; j < DOMAIN; j++) {
                if(j==0)
                System.out.print("|");
                System.out.print(board[i][j] + " ");
                if(j==8)
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("********************");
        System.out.println();
    }
}
