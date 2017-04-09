package tetris;// tetris.BadBrain.java

/**
 * A joke implementation based on tetris.DefaultBrain --
 * plays very, very badly by recommending the
 * opposite of the real brain.
 */
public class BadBrain extends DefaultBrain {
	public double rateBoard(Board board) {
		double score = super.rateBoard(board);
		return (10000 - score);
	}
}
