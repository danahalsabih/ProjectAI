import random 
 
EMPTY = 0  # Empty cell marker 
grid = []  # The Sudoku grid 
start_time = 0  # Start time for measuring execution duration 
 
 
def main(): 
    global grid, start_time 
    # Generate a semi-random starting grid 
    grid = generate_random_sudoku_start(20)  # Adjust the number of filled cells as needed 
 
    # Start solving timer 
    start_time = 0 
 
    # Solve the Sudoku 
    if solve_sudoku(grid): 
        end_time = 0 
        duration = (end_time - start_time) / 1_000_000  # Duration in milliseconds 
        print("Sudoku Solved Successfully:") 
        print_grid(grid) 
        print("Computational Time: " + str(duration) + " milliseconds") 
    else: 
        print("No solution found for the Sudoku.") 
 
 
# Solve the Sudoku using backtracking and MRV heuristic 
def solve_sudoku(grid): 
    cell = find_unassigned_location(grid) 
    if cell is None: 
        return True  # Puzzle solved 
    row, col = cell 
    values = get_possible_values(grid, row, col) 
    for value in values: 
        if is_safe(grid, row, col, value): 
            grid[row][col] = value 
            print("Assigning value " + str(value) + " to cell [" + str(row) + ", " + str(col) + "]") 
            print_grid(grid)  # Print the grid after each assignment 
            print("-------------------------------------------") 
            if solve_sudoku(grid): 
                return True 
            grid[row][col] = EMPTY  # Undo & try again 
    return False  # Trigger backtracking 
 
 
# Find the first unassigned location (with MRV heuristic could be enhanced here) 
def find_unassigned_location(grid): 
    for row in range(9): 
        for col in range(9): 
            if grid[row][col] == EMPTY: 
                return row, col 
    return None  # No unassigned location found 
 
 
# Checks whether it will be legal to assign num to the given row, col 
def is_safe(grid, row, col, num): 
    return not used_in_row(grid, row, num) and not used_in_col(grid, col, num) and \
           not used_in_box(grid, row - row % 3, col - col % 3, num)
 
 
 
# Check if num is not in the current row 
def used_in_row(grid, row, num): 
    return num in grid[row] 
 
 
# Check if num is not in the current column 
def used_in_col(grid, col, num): 
    return num in [grid[i][col] for i in range(9)] 
 
 
# Check if num is not in the current 3x3 box 
def used_in_box(grid, box_start_row, box_start_col, num): 
    return any(num == grid[row][col] for row in range(box_start_row, box_start_row + 3) 
               for col in range(box_start_col, box_start_col + 3)) 
 
 
# Generate a list of possible values for a cell 
def get_possible_values(grid, row, col): 
    possible = [True] * 9 
 
    # Eliminate numbers based on the current row, column, and 3x3 box 
    for i in range(9): 
        if grid[row][i] != EMPTY: 
            possible[grid[row][i] - 1] = False 
        if grid[i][col] != EMPTY: 
            possible[grid[i][col] - 1] = False 
        box_row = row - row % 3 + i // 3 
        box_col = col - col % 3 + i % 3 
        if grid[box_row][box_col] != EMPTY: 
            possible[grid[box_row][box_col] - 1] = False 
 
    # Compile the list of valid numbers 
    return [i + 1 for i, flag in enumerate(possible) if flag] 
 
 
# Generate a semi-random starting grid 
def generate_random_sudoku_start(fill_cells): 
    new_grid = [[EMPTY] * 9 for _ in range(9)] 
    while fill_cells > 0: 
        row = random.randint(0, 8) 
        col = random.randint(0, 8) 
        if new_grid[row][col] == EMPTY: 
            nums = get_possible_values(new_grid, row, col) 
            if nums: 
                num = random.choice(nums) 
                new_grid[row][col] = num 
                fill_cells -= 1 
    return new_grid 
 
 
# Print the Sudoku grid 
def print_grid(grid): 
    for row in grid: 
        print(" ".join(map(str, row))) 
 
 
if __name__ == "__main__": 
    main()