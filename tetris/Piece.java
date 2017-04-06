// Piece.java

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


/**
 * An immutable representation of a tetris piece in a particular rotation.
 * Each piece is defined by the blocks that make up its body.
 * <p>
 * Typical client code looks like...
 * <pre>
 * Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 * int width = pyra.getWidth();			// 3
 * Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 *
 * Piece[] pieces = Piece.getPieces();	// the array of root pieces
 * Piece stick = pieces[STICK];
 * int width = stick.getWidth();		// get its width
 * Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 * </pre>
 */
public class Piece {
	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR = "0 0	0 1	 0 2  0 3";
	public static final String L1_STR = "0 0	0 1	 0 2  1 0";
	public static final String L2_STR = "0 0	1 0 1 1	 1 2";
	public static final String S1_STR = "0 0	1 0	 1 1  2 1";
	public static final String S2_STR = "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR = "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR = "0 0  1 0  1 1  2 0";
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1 = 1;
	public static final int L2 = 2;
	public static final int S1 = 3;
	public static final int S2 = 4;
	public static final int SQUARE = 5;
	public static final int PYRAMID = 6;
	static private Piece[] pieces;    // singleton static array of first rotations
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation

	/**
	 * Defines a new piece given a TPoint[] array of its body.
	 * Makes its own copy of the array and the TPoints inside it.
	 */
	public Piece(TPoint[] points) {
		body = new TPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			body[i] = new TPoint(points[i]);
		}
		setMeasurements();
		setSkirt();
	}

	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 * Returns an array containing the first rotation of
	 * each of the 7 standard tetris pieces in the order
	 * STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 * The next (counterclockwise) rotation can be obtained
	 * from each piece with the {@link #fastRotation()} message.
	 * In this way, the client can iterate through all the rotations
	 * until eventually getting back to the first rotation.
	 * (provided code)
	 */
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces == null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[]{
					makeFastRotations(new Piece(STICK_STR)),
					makeFastRotations(new Piece(L1_STR)),
					makeFastRotations(new Piece(L2_STR)),
					makeFastRotations(new Piece(S1_STR)),
					makeFastRotations(new Piece(S2_STR)),
					makeFastRotations(new Piece(SQUARE_STR)),
					makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}

		return Piece.pieces;
	}

	/**
	 * Given the "first" root rotation of a piece, computes all
	 * the other rotations and links them all together
	 * in a circular list. The list loops back to the root as soon
	 * as possible. Returns the root piece. fastRotation() relies on the
	 * pointer structure setup here.
	 */
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
	private static Piece makeFastRotations(Piece root) {
		Piece current = root; // No need to change root.
		Piece next;

		while (true) {
			next = current.computeNextRotation();
			if (next.equals(root)) {
				current.next = root;
				break;
			}
			current.next = next;
			current = next;
		}

		return root;
	}

	/**
	 * Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 * the points into a TPoint[] array.
	 * (Provided code)
	 */
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while (tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());

				points.add(new TPoint(x, y));
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}

		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

	/**
	 * Calculates width and length of a piece.
	 */
	private void setMeasurements() {
		int width = 0;
		int height = 0;

		// Find rightmost and topmost
		// coordinates in pieces body.
		for (TPoint point : body) {
			if (point.x > width) {
				width = point.x;
			}

			if (point.y > height) {
				height = point.y;
			}
		}

		// Assign calculated width/height + 1 to ivars
		// (+ 1) could be omitted by initializing w/h as 1
		this.width = width + 1;
		this.height = height + 1;
	}

	/**
	 *
	 */
	private void setSkirt() {
		// Assign 0x7fffffff to the each element of the array.
		// Works only because we know that skirt is
		// much much smaller number in practice.
		skirt = new int[width];
		for (int i = 0; i < skirt.length; i++) {
			skirt[i] = Integer.MAX_VALUE;
			// HACK -- Come up with a better solution.
		}

		for (TPoint point : body) {
			if (point.y < skirt[point.x])
				skirt[point.x] = point.y;
		}

	}

	/**
	 * Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns a pointer to the piece's body. The caller
	 * should not modify this array.
	 */
	public TPoint[] getBody() {
		return body;
	}

	/**
	 * Returns a pointer to the piece's skirt. For each x value
	 * across the piece, the skirt gives the lowest y value in the body.
	 * This is useful for computing where the piece will land.
	 * The caller should not modify this array.
	 */
	public int[] getSkirt() {
		return skirt;
	}

	/**
	 * Returns a new piece that is 90 degrees counter-clockwise
	 * rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		TPoint[] points = new TPoint[body.length];

		for (int i = 0; i < body.length; i++) {
			TPoint point = body[i];
			points[i] = new TPoint(-point.y + (height - 1), point.x); // (-y + offset, x);
		}

		return new Piece(points);
	}

	/**
	 * Returns a pre-computed piece that is 90 degrees counter-clockwise
	 * rotated from the receiver.	 Fast because the piece is pre-computed.
	 * This only works on pieces set up by makeFastRotations(), and otherwise
	 * just returns null.
	 */
	public Piece fastRotation() {
		return next;
	}

	/**
	 * Returns true if two pieces are the same --
	 * their bodies contain the same points.
	 * Interestingly, this is not the same as having exactly the
	 * same body arrays, since the points may not be
	 * in the same order in the bodies. Used internally to detect
	 * if two rotations are effectively the same.
	 */
	public boolean equals(Object obj) {
		// standard equals() technique 1
		if (obj == this) return true;

		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece) obj;

		// Make two lists of Piece points.
		ArrayList<TPoint> ours = new ArrayList<>(Arrays.asList(this.getBody())); // could have used this.body instead
		ArrayList<TPoint> theirs = new ArrayList<>(Arrays.asList(other.getBody()));

		// Sort them using lambda functions (yay!).
		ours.sort((a, b) -> b.compareTo(a));
		theirs.sort((a, b) -> b.compareTo(a));

		// Compare these two lists
		return ours.equals(theirs);
	}


}
