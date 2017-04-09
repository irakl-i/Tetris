package tetris;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
  Unit test for tetris.Piece class -- starter shell.
 */
public class PieceTest {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new tetris.PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;
	private Piece l1, l2, l3, l4;
	private Piece sq1, sq2;
	private Piece st1, st2;

	@Before
	public void setUp() throws Exception {

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		l1 = new Piece(Piece.L1_STR);
		l2 = l1.computeNextRotation();
		l3 = l2.computeNextRotation();
		l4 = l3.computeNextRotation();

		sq1 = new Piece(Piece.SQUARE_STR);
		sq2 = sq1.computeNextRotation();

		st1 = new Piece(Piece.STICK_STR);
		st2 = st1.computeNextRotation();

	}

	// Here are some sample tests to get you started

	@Test
	public void testSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());

		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());

		// Now try with some other piece, made a different way
		assertEquals(1, st1.getWidth());
		assertEquals(4, st1.getHeight());

		// Rotate stick and compare its height and width to the original
		assertEquals(st1.getWidth(), st2.getHeight());
		assertEquals(st2.getHeight(), st1.getWidth());

		// Square and its measurements
		assertEquals(2, sq1.getWidth());
		assertEquals(2, sq1.getHeight());

		assertEquals(2, sq2.getWidth());
		assertEquals(2, sq2.getHeight());

		// L and its measurements
		assertEquals(2, l1.getWidth());
		assertEquals(3, l1.getHeight());

		assertEquals(3, l2.getWidth());
		assertEquals(2, l2.getHeight());

		// S and its measurements
		assertEquals(2, s.getHeight());
		assertEquals(3, s.getWidth());

		assertEquals(3, sRotated.getHeight());
		assertEquals(2, sRotated.getWidth());

	}


	// Test the skirt returned by a few pieces
	@Test
	public void testSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[]{0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[]{1, 0, 1}, pyr3.getSkirt()));

		assertTrue(Arrays.equals(new int[]{0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[]{1, 0}, sRotated.getSkirt()));

		assertTrue(Arrays.equals(new int[]{0}, st1.getSkirt()));
		assertTrue(Arrays.equals(new int[]{0, 0, 0, 0}, st2.getSkirt()));

		assertTrue(Arrays.equals(new int[]{0, 0}, l1.getSkirt()));
		assertTrue(Arrays.equals(new int[]{0, 0, 0}, l2.getSkirt()));

		assertTrue(Arrays.equals(new int[]{0, 0}, sq1.getSkirt()));
		assertTrue(Arrays.equals(new int[]{0, 0}, sq2.getSkirt()));
	}

	@Test
	public void testEquality() {
		assertTrue(pyr1.equals(pyr4.computeNextRotation()));

		assertTrue(st1.equals(st2.computeNextRotation()));

		assertTrue(sq1.equals(sq2.computeNextRotation()));
		assertTrue(sq1.equals(sq2));

		assertTrue(l1.equals(l2.computeNextRotation().computeNextRotation().computeNextRotation()));

		assertTrue(s.equals(sRotated.computeNextRotation().computeNextRotation().computeNextRotation()));
	}

	@Test
	public void testRotations() {
		Piece[] pieces = Piece.getPieces();

		Piece pyr = pieces[Piece.PYRAMID];
		assertEquals(pyr, pyr.fastRotation().fastRotation().fastRotation().fastRotation());

		Piece st = pieces[Piece.STICK];
		assertEquals(st, st.fastRotation().fastRotation());

		Piece sq = pieces[Piece.SQUARE];
		assertEquals(sq, sq.fastRotation().fastRotation());

		Piece l1 = pieces[Piece.L1];
		assertEquals(l1, l1.fastRotation().fastRotation().fastRotation().fastRotation());

		Piece l2 = pieces[Piece.L2];
		assertEquals(l2, l2.fastRotation().fastRotation().fastRotation().fastRotation());

		Piece s1 = pieces[Piece.S1];
		assertEquals(s1, s1.fastRotation().fastRotation().fastRotation().fastRotation());

		Piece s2 = pieces[Piece.S2];
		assertEquals(s2, s2.fastRotation().fastRotation().fastRotation().fastRotation());
	}


}
