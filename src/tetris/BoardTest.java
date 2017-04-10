package tetris;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {
	Board b;
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s1, sRotated, s2, s2Rotated;
	private Piece l1, l2, l3, l4;
	private Piece sq1, sq2;
	private Piece st1, st2;

	// This shows how to build things in setUp() to re-use
	// across tests.

	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	@Before
	public void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s1 = new Piece(Piece.S1_STR);
		sRotated = s1.computeNextRotation();
		s2 = new Piece(Piece.S2_STR);
		s2Rotated = s2.computeNextRotation();

		l1 = new Piece(Piece.L2_STR);
		l2 = l1.computeNextRotation();
		l3 = l2.computeNextRotation();
		l4 = l3.computeNextRotation();

		sq1 = new Piece(Piece.SQUARE_STR);
		sq2 = sq1.computeNextRotation();

		st1 = new Piece(Piece.STICK_STR);
		st2 = st1.computeNextRotation();

		b.place(pyr1, 0, 0);
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
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	@Test
	public void testDropHeights() {
		b.commit();
		assertEquals(1, b.dropHeight(st1, 0));
		assertEquals(1, b.dropHeight(st1, 2));
		assertEquals(2, b.dropHeight(st1, 1));
		b.place(st1, 0, 1);
		assertEquals(5, b.dropHeight(sq1, 0));
		assertEquals(2, b.dropHeight(sq1, 1));
		assertEquals(1, b.dropHeight(sRotated, 1));
		b.commit();
		b.place(sRotated, 1, 1);
		assertEquals(3, b.dropHeight(st1, 2));
		System.out.println(b.toString());
	}

	@Test
	public void testMaxHeight() {
		b.commit();
		assertEquals(2, b.getMaxHeight());
		b.place(st1, 0, 1);
		assertEquals(5, b.getMaxHeight());
		b.undo();
		assertEquals(2, b.getMaxHeight());
		b.commit();
		b.place(st1, 2, 1);
		assertEquals(5, b.getMaxHeight());
		b.undo();
		b.place(sRotated, 1, 1);
		assertEquals(4, b.getMaxHeight());
		b.commit();
		b.place(sRotated, 1, 3);
		assertEquals(6, b.getMaxHeight());
		System.out.println(b.toString());
	}

	@Test
	public void testColumnHeight() {
		b.commit();
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		b.place(st1, 0, 1);
		b.commit();
		b.place(st1, 2, 1);
		assertEquals(5, b.getColumnHeight(0));
		assertEquals(5, b.getColumnHeight(2));
		b.undo();
		b.place(sRotated, 1, 1);
		assertEquals(5, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		b.commit();
		b.place(sq1, 1, 4);
		assertEquals(6, b.getColumnHeight(1));
		assertEquals(6, b.getColumnHeight(2));
		System.out.println(b.toString());
	}

	@Test
	public void testRowWidth() {
		b.undo();
		b.place(sq1, 0, 0);
		b.commit();
		b.place(sq2, 0, 2);
		assertEquals(2, b.getRowWidth(0));
		assertEquals(2, b.getRowWidth(1));
		assertEquals(2, b.getRowWidth(2));
		assertEquals(2, b.getRowWidth(3));
		b.commit();
		b.place(st1, 2, 0);
		assertEquals(3, b.getRowWidth(0));
		assertEquals(3, b.getRowWidth(1));
		assertEquals(3, b.getRowWidth(2));
		assertEquals(3, b.getRowWidth(3));
		b.commit();
		b.place(s1, 0, 4);
		assertEquals(2, b.getRowWidth(4));
		assertEquals(2, b.getRowWidth(5));
		b.clearRows();
		b.commit();
		b.place(s2Rotated, 0, 1);
		assertEquals(2, b.getRowWidth(0));
		assertEquals(3, b.getRowWidth(1));
		assertEquals(2, b.getRowWidth(2));
		assertEquals(1, b.getRowWidth(3));
		System.out.println(b.toString());
	}

	@Test
	public void testClearRows() {
		b.undo();
		b.place(sq1, 0, 0);
		b.commit();
		b.place(sq2, 0, 2);
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(st1, 2, 0));
		assertEquals(4, b.clearRows());
		b.commit();
		b.place(pyr1, 0, 0);
		b.commit();
		b.place(s2Rotated, 0, 1);
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(st1, 2, 1));
		assertEquals(3, b.clearRows());
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(s2Rotated, 0, 0));
		b.commit();
		assertEquals(2, b.clearRows());
		b.commit();
		b.place(l3, 0, 0);
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(st1, 2, 0));
		b.commit();
		assertEquals(2, b.clearRows());
		System.out.println(b.toString());
	}

	@Test
	public void testUndo() {
		b.commit();
		b.clear();
		b.place(l2, 0, 0);
		assertEquals(1, b.clearRows());
		b.undo();
		b.commit();
		b.place(sq1, 0, 0);
		b.commit();
		b.place(sq2, 0, 2);
		b.commit();
		b.place(st1, 2, 0);
		b.undo();
		assertEquals(0, b.clearRows());
		System.out.println(b.toString());
	}

	@Test
	public void testBreakBoard() {
		b.commit();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(s1, 13, 37));
		b.commit();
		assertEquals(Board.PLACE_BAD, b.place(s1, 0, 0));
	}

	@Test
	public void testOtherStuff() {
		b = new Board();
		assertEquals(10, b.getWidth());
		assertEquals(20, b.getHeight());
		b.place(st2, 0, 0);
		b.commit();
		b.place(st2, 4, 0);
		b.commit();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(st2, 8, 0));
		b.undo();
		assertEquals(Board.PLACE_BAD, b.place(st2, 6, 0));
		b.undo();
		b.place(sq1, 0, 1);
		b.commit();
		b.place(s1, 2, 1);
		assertEquals(false, b.getGrid(2, 2));
		for (int i = 0; i < 8; i++) {
			assertEquals(true, b.getGrid(i, 0));
		}
		System.out.println(b.toString());
	}


	// Make  more tests, by putting together longer series of
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.

}