package tetris;// tetris.Board.java

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CS108 Tetris tetris.Board.
 * Represents a Tetris board -- essentially a 2-d grid
 * of booleans. Supports tetris pieces and row clearing.
 * Has an "undo" feature that allows clients to add and remove pieces efficiently.
 * Does not do any drawing or have any idea of pixels. Instead,
 * just represents the abstract 2-d board.
 */
public class Board {
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	private static final int DEFAULT_WIDTH = 10;
	private static final int DEFAULT_HEIGHT = 20;
	boolean committed;


	// Here a few trivial methods are provided:
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private int maxHeight;
	private int[] widths;
	private int[] heights;
	private boolean[][] grid;
	private boolean DEBUG = true;

	private int maxHeightBackup;
	private int[] widthsBackup;
	private int[] heightsBackup;
	private boolean[][] gridBackup;


	/**
	 * Creates an empty board of the given width and height
	 * measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		this.widths = new int[height];
		this.heights = new int[width];
		this.widthsBackup = new int[height];
		this.heightsBackup = new int[width];
		gridBackup = new boolean[width][height];

		computeWidths();
		computeHeights();
		maxHeight = getMaxHeight();
	}


	/**
	 * Calls main constructor with default values.
	 */
	public Board() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}


	/**
	 * Computes heights of each column.
	 */
	private void computeHeights() {
		for (int i = 0; i < grid.length; i++) {
			int top = 0;
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] && j >= top) top = j + 1;
			}
			heights[i] = top;
		}
	}


	/**
	 * Computes widths of each column.
	 */
	private void computeWidths() {
		for (int i = 0; i < grid[0].length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[j][i]) widths[i]++;
			}
		}
	}


	/**
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * Returns the max column height present in the board.
	 * For an empty board this is 0.
	 */
	public int getMaxHeight() {
		// fancy Java 8 way of finding max in the array of primitives.
		return Arrays.stream(heights).max().getAsInt();
	}


	/**
	 * Checks the board for internal consistency -- used
	 * for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			// TODO YOUR CODE HERE
		}
	}


	/**
	 * Given a piece and an x, returns the y
	 * value where the piece would come to rest
	 * if it were dropped straight down at that x.
	 * <p>
	 * <p>
	 * Implementation: use the skirt and the col heights
	 * to compute this fast -- O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int result = 0;
		for (int i = 0; i < piece.getWidth(); i++) {
			int difference = getColumnHeight(x + i) - skirt[i];
			if (difference > result) result = difference;
		}
		return result;
	}


	/**
	 * Returns the height of the given column --
	 * i.e. the y value of the highest block + 1.
	 * The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		return heights[x];
	}


	/**
	 * Returns the number of filled blocks in
	 * the given row.
	 */
	public int getRowWidth(int y) {
		return widths[y];
	}


	/**
	 * Returns true if the given block is filled in the board.
	 * Blocks outside of the valid width/height area
	 * always return true.
	 */
	public boolean getGrid(int x, int y) {
		return !inBounds(new TPoint(x, y)) || grid[x][y];
	}


	/**
	 * Attempts to add the body of a piece to the board.
	 * Copies the piece blocks into the board grid.
	 * Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 * for a regular placement that causes at least one row to be filled.
	 * <p>
	 * <p>Error cases:
	 * A placement may fail in two ways. First, if part of the piece may falls out
	 * of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 * Or the placement may collide with existing blocks in the grid
	 * in which case PLACE_BAD is returned.
	 * In both error cases, the board may be left in an invalid
	 * state. The client can use undo(), to recover the valid, pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		backup();
		committed = false;

		int result = PLACE_OK;

		TPoint dest = new TPoint(0, 0);
		for (TPoint point : piece.getBody()) {
			// Calculate destination coordinates.
			dest.x = x + point.x;
			dest.y = y + point.y;

			// Check if the point is in bounds.
			if (!inBounds(dest)) {
				result = PLACE_OUT_BOUNDS;
				break;
			}

			// Check if the point is not already filled.
			if (grid[dest.x][dest.y]) {
				result = PLACE_BAD;
				break;
			}

			grid[dest.x][dest.y] = true;
			widths[dest.y]++;
			heights[dest.x]++;

			if (widths[dest.y] == width) result = PLACE_ROW_FILLED;
		}

		recomputeDimensions();
		return result;
	}

	private void backup() {
		// Backup the board.
		for (int i = 0; i < grid.length; i++) {
			System.arraycopy(grid[i], 0, gridBackup[i], 0, gridBackup.length);
		}

		// Backup maxHeight.
		maxHeightBackup = maxHeight;

		// Backup widths and heights arrays.
		System.arraycopy(widths, 0, widthsBackup, 0, widths.length);
		System.arraycopy(heights, 0, heightsBackup, 0, heights.length);
	}


	/**
	 * Takes a point and checks if it's on the grid.
	 *
	 * @param point on the board
	 * @return true if the point is in bounds.
	 */
	private boolean inBounds(TPoint point) {
		return (point.x < width) && (point.y < height) && (point.x >= 0) && (point.y >= 0);
	}


	/**
	 * Deletes rows that are filled all the way across, moving
	 * things above down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		int rowsCleared = 0;
		int column = 0;
		int size = grid.length * grid[0].length;
		if (committed) backup();

		// Make a new grid full of 'false' values.
		boolean[][] result = new boolean[grid.length][grid[0].length];
		// ArrayList to store already read values of current column.
		ArrayList<Boolean> list = new ArrayList<>();

		for (int i = 0; i <= size; i++) {
			if (i != 0 && i % grid.length == 0) {
				// Check if every value of this column is true.
				boolean flag = isAllTrue(list);

				if (!flag) {
					// If the flag is false we need to copy
					// this column to the result grid.
					for (int j = 0; j < list.size(); j++) {
						result[j][column] = list.get(j);
					}
					column++; // 'Prepare' column for the next iteration.
				} else {
					rowsCleared++;
				}

				list.clear();
			}
			// Add current value to the list.
			if (i < size) list.add(grid[i % grid.length][i / grid.length]);
		}

		committed = false;
		grid = result;
		recomputeDimensions();
		sanityCheck();
		return rowsCleared;
	}


	/**
	 * Nullifies current widths and heights
	 * arrays and recomputes its values.
	 */
	private void recomputeDimensions() {
		// Fill arrays with zeroes.
		Arrays.fill(widths, 0);
		Arrays.fill(heights, 0);

		// Recompute widths and heights.
		computeWidths();
		computeHeights();
		maxHeight = getMaxHeight();
	}


	/**
	 * Takes a list and checks if every values is true.
	 *
	 * @param list of booleans
	 * @return true if everything's true
	 */
	private boolean isAllTrue(ArrayList<Boolean> list) {
		boolean flag = false;

		for (boolean bool : list) {
			if (!bool) {
				flag = false;
				break;
			}
			flag = true;
		}

		return flag;
	}


	/**
	 * Reverts the board to its state before up to one place
	 * and one clearRows();
	 * If the conditions for undo() are not met, such as
	 * calling undo() twice in a row, then the second undo() does nothing.
	 * See the overview docs.
	 */
	public void undo() {
		maxHeight = maxHeightBackup;
		widths = widthsBackup;
		heights = heightsBackup;
		grid = gridBackup;
		commit();
	}


	/**
	 * Puts the board in the committed state.
	 */
	public void commit() {
		committed = true;
	}


	/**
	 * Renders the board state as a big String, suitable for printing.
	 * This is the sort of print-obj-state utility that can help see complex
	 * state change over time.
	 * (provided debugging utility)
	 *
	 * @return string
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < width; x++) {
				if (getGrid(x, y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x = 0; x < width + 2; x++) buff.append('-');
		return (buff.toString());
	}
}


