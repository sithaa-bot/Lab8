
interface GridLayout {
    void insertAtRow(int rowNumber, int[] values);

    void insertAtColumn(int columnNumber, int[] values);

    void clearAtRow(int rowNumber);

    void clearAtColumn(int columnNumber);

    void updateCell(int rowNumber, int columnNumber, int value);

    void displayGrid();

    void clear();
}

class GridManagement implements GridLayout {
    private int[][] grid;
    private final int GRID_SIZE = 7;

    public GridManagement() {
        grid = new int[GRID_SIZE][GRID_SIZE];

        clear();
    }

    @Override
    public void insertAtRow(int rowNumber, int[] values) {
        if (rowNumber < 0 || rowNumber >= GRID_SIZE) {
            System.out.println("Error: Row number must be between 0 and " + (GRID_SIZE - 1));
            return;
        }

        if (values.length > GRID_SIZE) {
            System.out.println("Error: Too many values for row. Maximum is " + GRID_SIZE);
            return;
        }

        for (int i = 0; i < values.length && i < GRID_SIZE; i++) {
            if (values[i] >= 0 && values[i] <= 9) {
                grid[rowNumber][i] = values[i];
            } else {
                System.out.println("Warning: Value " + values[i] + " is out of range (0-9). Setting to 0.");
                grid[rowNumber][i] = 0;
            }
        }
    }

    @Override
    public void insertAtColumn(int columnNumber, int[] values) {
        if (columnNumber < 0 || columnNumber >= GRID_SIZE) {
            System.out.println("Error: Column number must be between 0 and " + (GRID_SIZE - 1));
            return;
        }

        if (values.length > GRID_SIZE) {
            System.out.println("Error: Too many values for column. Maximum is " + GRID_SIZE);
            return;
        }

        for (int i = 0; i < values.length && i < GRID_SIZE; i++) {
            if (values[i] >= 0 && values[i] <= 9) {
                grid[i][columnNumber] = values[i];
            } else {
                System.out.println("Warning: Value " + values[i] + " is out of range (0-9). Setting to 0.");
                grid[i][columnNumber] = 0;
            }
        }
    }

    @Override
    public void clearAtRow(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= GRID_SIZE) {
            System.out.println("Error: Row number must be between 0 and " + (GRID_SIZE - 1));
            return;
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            grid[rowNumber][i] = 0;
        }
        System.out.println("Row " + rowNumber + " cleared.");
    }

    @Override
    public void clearAtColumn(int columnNumber) {
        if (columnNumber < 0 || columnNumber >= GRID_SIZE) {
            System.out.println("Error: Column number must be between 0 and " + (GRID_SIZE - 1));
            return;
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            grid[i][columnNumber] = 0;
        }
        System.out.println("Column " + columnNumber + " cleared.");
    }

    @Override
    public void updateCell(int rowNumber, int columnNumber, int value) {
        if (rowNumber < 0 || rowNumber >= GRID_SIZE || columnNumber < 0 || columnNumber >= GRID_SIZE) {
            System.out.println("Error: Invalid cell position. Row and column must be between 0 and " + (GRID_SIZE - 1));
            return;
        }

        if (value < 0 || value > 9) {
            System.out.println("Error: Value must be between 0 and 9.");
            return;
        }

        grid[rowNumber][columnNumber] = value;
        System.out.println("Cell [" + rowNumber + "][" + columnNumber + "] updated to " + value);
    }

    @Override
    public void displayGrid() {
        System.out.println(":::: The GRID ::::");
        System.out.println();

        System.out.print("  ");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("  ");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print("- ");
        }
        System.out.println();

        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print(i + " - ");
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public void clear() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = 0;
            }
        }
        System.out.println("Grid cleared.");
    }

    public int getCellValue(int rowNumber, int columnNumber) {
        if (rowNumber < 0 || rowNumber >= GRID_SIZE || columnNumber < 0 || columnNumber >= GRID_SIZE) {
            System.out.println("Error: Invalid cell position.");
            return -1;
        }
        return grid[rowNumber][columnNumber];
    }

    /**
     * Get a copy of the entire row
     */
    public int[] getRow(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= GRID_SIZE) {
            System.out.println("Error: Invalid row number.");
            return null;
        }
        return grid[rowNumber].clone();
    }

    public int[] getColumn(int columnNumber) {
        if (columnNumber < 0 || columnNumber >= GRID_SIZE) {
            System.out.println("Error: Invalid column number.");
            return null;
        }

        int[] column = new int[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            column[i] = grid[i][columnNumber];
        }
        return column;
    }

    public void fillRandomly() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = (int) (Math.random() * 10);
            }
        }
        System.out.println("Grid filled with random values.");
    }

    public boolean isEmpty() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}

public class GridLayoutTest {
    public static void main(String[] args) {
        GridManagement gridManager = new GridManagement();

        gridManager.displayGrid();

        System.out.println("1. Update a single cell");
        gridManager.updateCell(2, 3, 5);
        gridManager.updateCell(0, 0, 1);
        gridManager.displayGrid();

        System.out.println("2. Insert at a row");
        int[] rowValues = { 1, 2, 3, 4, 5, 6, 7 };
        gridManager.insertAtRow(1, rowValues);
        gridManager.displayGrid();

        System.out.println("3. Insert at a column");
        int[] columnValues = { 9, 8, 7, 6, 5, 4, 3 };
        gridManager.insertAtColumn(5, columnValues);
        gridManager.displayGrid();

        System.out.println("4. Clear at a row");
        gridManager.clearAtRow(1);
        gridManager.displayGrid();

        System.out.println("5. Clear at a column");
        gridManager.clearAtColumn(5);
        gridManager.displayGrid();

        System.out.println("6. Clear all");
        gridManager.clear();
        gridManager.displayGrid();

        System.out.println("Testing additional features:");
        gridManager.fillRandomly();
        gridManager.displayGrid();

        System.out.println("Row 3: " + java.util.Arrays.toString(gridManager.getRow(3)));
        System.out.println("Column 2: " + java.util.Arrays.toString(gridManager.getColumn(2)));
        System.out.println("Cell [4][4] value: " + gridManager.getCellValue(4, 4));
        System.out.println("Is grid empty? " + gridManager.isEmpty());
    }
}