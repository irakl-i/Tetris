package tetris;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {
	Board b;

	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece so1, so2;
	private Piece st1, st2;
	private Piece lo1, lo2, lo3, lo4;
	private Piece lt1, lt2, lt3, lt4;
	private Piece stick;
	private Piece square;

	@Before
	public void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		so1 = new Piece(Piece.S1_STR);
		so2 = so1.computeNextRotation();
		st1 = new Piece(Piece.S2_STR);
		st2 = st1.computeNextRotation();
		lo1 = new Piece(Piece.L1_STR);
		lo2 = lo1.computeNextRotation();
		lo3 = lo2.computeNextRotation();
		lo4 = lo3.computeNextRotation();
		lt1 = new Piece(Piece.L2_STR);
		lt2 = lt1.computeNextRotation();
		lt3 = lt2.computeNextRotation();
		lt4 = lt3.computeNextRotation();
		stick = new Piece(Piece.STICK_STR);
		square = new Piece(Piece.SQUARE_STR);

		b.place(pyr1, 0, 0);
		b.commit();
	}

	// Check the basic width/height/max after the one placement
	@Test
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}

	// Place sRotated into the board, then check some measures
	@Test
	public void testSample2() {
		b.commit();
		int result = b.place(so2, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	// Place pieces onto the board in a general fashion
	@Test
	public void testBasicDrops() {
		Board b1 = new Board(7, 6);
		b1.place(pyr3, 2, 0);
		b1.commit();
		b1.place(so2, 1, 2);
		b1.commit();
		b1.place(st2, 4, 2);
		b1.commit();
		assertEquals(3, b1.dropHeight(pyr3, 2));
		b1.place(pyr3, 2, 3);
		b1.commit();

		assertEquals(5, b1.getColumnHeight(1));
		assertEquals(5, b1.getColumnHeight(2));
		assertEquals(5, b1.getColumnHeight(3));
		assertEquals(5, b1.getColumnHeight(4));
		assertEquals(5, b1.getColumnHeight(5));
	}

	// Test a board with pyramid drops
	@Test
	public void testBasicDrops2() {
		Board b1 = new Board(3, 6);
		assertEquals(0, b1.dropHeight(pyr4, 0));
		assertEquals(Board.PLACE_OK, b1.place(pyr4, 0, 0));
		b1.commit();
		assertEquals(1, b1.dropHeight(pyr2, 1));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(pyr2, 1, 1));
		b1.commit();
		assertEquals(3, b1.dropHeight(pyr3, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(pyr3, 0, 3));
		b1.commit();
		assertEquals(5, b1.dropHeight(pyr1, 0));
		assertEquals(Board.PLACE_OUT_BOUNDS, b1.place(pyr1, 0, 5));
		b1.commit();
	}

	// Test a board with pyramid drops
	@Test
	public void testBasicDrops3() {
		Board b1 = new Board(4, 6);
		Piece stick2 = stick.computeNextRotation();

		assertEquals(0, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 0));
		b1.commit();
		assertEquals(1, b1.getColumnHeight(1));
		assertEquals(1, b1.getColumnHeight(2));
		assertEquals(1, b1.getColumnHeight(3));
		assertEquals(1, b1.getColumnHeight(0));
		assertEquals(1, b1.getMaxHeight());

		assertEquals(1, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 1));
		b1.commit();
		assertEquals(2, b1.getColumnHeight(1));
		assertEquals(2, b1.getColumnHeight(2));
		assertEquals(2, b1.getColumnHeight(3));
		assertEquals(2, b1.getColumnHeight(0));
		assertEquals(2, b1.getMaxHeight());

		assertEquals(2, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 2));
		b1.commit();
		assertEquals(3, b1.getColumnHeight(1));
		assertEquals(3, b1.getColumnHeight(2));
		assertEquals(3, b1.getColumnHeight(3));
		assertEquals(3, b1.getColumnHeight(0));
		assertEquals(3, b1.getMaxHeight());
	}

	// Test a skinny board and height/width arrays
	@Test
	public void testArrays() {
		Board b1 = new Board(2, 8);

		assertEquals(0, b1.dropHeight(st2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(st2, 0, 0));
		assertEquals(3, b1.getMaxHeight());
		b1.commit();

		assertEquals(3, b1.dropHeight(so2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(so2, 0, 3));
		assertEquals(6, b1.getMaxHeight());
		b1.commit();

		assertEquals(5, b1.dropHeight(so2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(so2, 0, 5));
		assertEquals(8, b1.getMaxHeight());
		b1.commit();

		assertEquals(8, b1.getColumnHeight(0));
		assertEquals(7, b1.getColumnHeight(1));

		assertEquals(1, b1.getRowWidth(0));
		assertEquals(2, b1.getRowWidth(1));
		assertEquals(1, b1.getRowWidth(2));
		assertEquals(1, b1.getRowWidth(3));
		assertEquals(2, b1.getRowWidth(4));
		assertEquals(2, b1.getRowWidth(6));
		assertEquals(1, b1.getRowWidth(7));
	}

	// Test board clearing
	@Test
	public void testClearRow() {
		Board b1 = new Board(4, 3);
		Piece stick2 = stick.computeNextRotation();

		assertEquals(0, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 0));
		b1.commit();

		assertEquals(1, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 1));
		b1.commit();

		assertEquals(2, b1.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 2));
		b1.commit();

		assertEquals(3, b1.getColumnHeight(1));
		assertEquals(3, b1.getColumnHeight(2));
		assertEquals(3, b1.getColumnHeight(3));
		assertEquals(3, b1.getColumnHeight(0));
		assertEquals(3, b1.getMaxHeight());
		b1.clearRows();
		assertEquals(0, b1.getColumnHeight(1));
		assertEquals(0, b1.getColumnHeight(2));
		assertEquals(0, b1.getColumnHeight(3));
		assertEquals(0, b1.getColumnHeight(0));
		assertEquals(0, b1.getMaxHeight());
	}

	// Test board clearing
	@Test
	public void testClearRow2() {
		Board b1 = new Board(3, 6);
		assertEquals(0, b1.dropHeight(pyr4, 0));
		assertEquals(Board.PLACE_OK, b1.place(pyr4, 0, 0));
		b1.commit();
		assertEquals(1, b1.dropHeight(pyr2, 1));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(pyr2, 1, 1));
		b1.commit();
		assertEquals(3, b1.dropHeight(pyr3, 0));
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(pyr3, 0, 3));
		b1.commit();

		assertEquals(3, b1.clearRows());
		System.out.println(b1.toString());
		assertEquals(1, b1.getColumnHeight(0));
		assertEquals(2, b1.getColumnHeight(1));
		assertEquals(2, b1.getColumnHeight(2));
		assertEquals(2, b1.getMaxHeight());
	}

	// Test row clearing when nothing is present
	@Test
	public void testClearRows3() {
		Board b1 = new Board(3, 6);
		b1.clearRows();
		assertEquals(0, b1.getMaxHeight());
		assertEquals(0, b1.getRowWidth(0));
		assertEquals(0, b1.getRowWidth(1));
		assertEquals(0, b1.getRowWidth(2));
		assertEquals(0, b1.getRowWidth(3));

		b1.commit();
		b1.clearRows();
		b1.clearRows();
		assertEquals(0, b1.getRowWidth(0));
		assertEquals(0, b1.getRowWidth(1));
		assertEquals(0, b1.getRowWidth(2));
		assertEquals(0, b1.getRowWidth(3));
		assertEquals(0, b1.getMaxHeight());

		b1.undo();
		assertEquals(0, b1.getMaxHeight());
		assertEquals(0, b1.getRowWidth(0));
		assertEquals(0, b1.getRowWidth(1));
		assertEquals(0, b1.getRowWidth(2));
		assertEquals(0, b1.getRowWidth(3));
	}

	// Test a series of place, clears, and undos
	@Test
	public void testCommitSeries() {
		Board b1 = new Board(3, 6);
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(pyr1, 0, 0));
		assertEquals(1, b1.clearRows());

		b1.undo();
		assertEquals(0, b1.getColumnHeight(0));
		assertEquals(0, b1.getColumnHeight(1));
		assertEquals(0, b1.getColumnHeight(2));
		assertEquals(0, b1.getMaxHeight());
	}

	// Test a series of place, clears, and undos
	@Test
	public void testCommitSeries2() {
		Board b1 = new Board(4, 5);
		Piece stick2 = stick.computeNextRotation();

		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 0));
		b1.undo();
		assertEquals(0, b1.getColumnHeight(0));
		assertEquals(0, b1.getColumnHeight(1));
		assertEquals(0, b1.getColumnHeight(2));
		assertEquals(0, b1.getMaxHeight());
		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 0));
		b1.commit();

		assertEquals(Board.PLACE_OK, b1.place(square, 1, 1));
		assertEquals(3, b1.getMaxHeight());
		b1.undo();
		assertEquals(1, b1.getMaxHeight());
		assertEquals(Board.PLACE_OK, b1.place(square, 1, 1));
		b1.commit();

		assertEquals(1, b1.clearRows());
		assertEquals(2, b1.getMaxHeight());
		b1.undo();
		assertEquals(3, b1.getMaxHeight());
		b1.commit();

		assertEquals(Board.PLACE_ROW_FILLED, b1.place(stick2, 0, 3));
		assertEquals(4, b1.getMaxHeight());
		assertEquals(4, b1.getRowWidth(0));
		assertEquals(2, b1.getRowWidth(1));
		assertEquals(2, b1.getRowWidth(2));
		assertEquals(4, b1.getRowWidth(3));
		assertEquals(2, b1.clearRows());
		assertEquals(2, b1.getMaxHeight());
		assertEquals(2, b1.getRowWidth(0));
		assertEquals(2, b1.getRowWidth(1));
		assertEquals(0, b1.getRowWidth(2));
		assertEquals(0, b1.getRowWidth(3));
		b1.undo();

		assertEquals(3, b1.getMaxHeight());
		assertEquals(4, b1.getRowWidth(0));
		assertEquals(2, b1.getRowWidth(1));
		assertEquals(2, b1.getRowWidth(2));
		assertEquals(0, b1.getRowWidth(3));
	}

	// Test that failing cases are deteced
	@Test
	public void testFails() {
		Board b1 = new Board(4, 3);
		Piece stick2 = stick.computeNextRotation();

		assertEquals(Board.PLACE_OUT_BOUNDS, b1.place(stick2, -1, 0));
		b1.undo();
		assertEquals(0, b1.getMaxHeight());

		assertEquals(Board.PLACE_OK, b1.place(pyr1, 1, 0));
		//b1.printBoardState();
		assertEquals(3, b1.getRowWidth(0));
		assertEquals(false, b1.getGrid(0, 0));
		assertEquals(true, b1.getGrid(1, 0));
		assertEquals(true, b1.getGrid(2, 0));
		assertEquals(true, b1.getGrid(3, 0));
		b1.commit();

		assertEquals(Board.PLACE_BAD, b1.place(pyr1, 1, 0));
		b1.undo();
		assertEquals(2, b1.getMaxHeight());
		assertEquals(Board.PLACE_OUT_BOUNDS, b1.place(stick, 0, 0));
		b1.undo();
		b1.commit();
		b1.place(pyr4, 0, 0);
		assertEquals(1, b1.clearRows());
		b1.commit();
		assertEquals(3, b1.getRowWidth(0));
		assertEquals(2, b1.getMaxHeight());
		assertEquals(false, b1.getGrid(3, 0));
		assertEquals(false, b1.getGrid(2, 1));
		assertEquals(false, b1.getGrid(1, 1));
	}

	// Test board dimensions
	@Test
	public void testBoardDimensions() {
		Board b1 = new Board(4, 3);
		assertEquals(4, b1.getWidth());
		assertEquals(3, b1.getHeight());

		Board b2 = new Board(10, 10);
		assertEquals(10, b2.getWidth());
		assertEquals(10, b2.getHeight());
	}
}

