package tetris;

import javax.swing.*;

/**
 * Created by Luka on 4/12/2017.
 */
public class JBrainTetris extends JTetris {
	private JComponent panel;
	private JCheckBox brainMode;
	private DefaultBrain brain;
	private Brain.Move move;
	private int count;

	public JBrainTetris(int pixels) {
		super(pixels);
		brain = new DefaultBrain();
		count = 0;
	}

	/**
	 * Creates a frame with a tetris.JBrainTetris.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JTetris brainTetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(brainTetris);
		frame.setVisible(true);
	}

	@Override
	public void tick(int verb) {
		// Check if the brainMode is active
		if (brainMode.isSelected() && verb == DOWN) {
			if (count != super.count) {
				count = super.count;
				board.undo();
				// Calculate best move for this piece with regards to board height.
				move = brain.bestMove(board, currentPiece, HEIGHT, move);
			}

			// Tries to match best move by rotating and
			// moving the piece left and right. Drops it down
			// if the best move to do is to drop it down.
			if (move != null) {
				// Rotate if the best move is to rotate the piece
				// until we get what we want.
				if (!currentPiece.equals(move.piece))
					super.tick(ROTATE); // Could have used tick(x) (slower approach, nicer code(?)).

				// If the move.x value is > than x value of our piece
				// we need to move our piece to the right.
				if (move.x > currentX) super.tick(RIGHT);

				// Same thing for the left.
				if (move.x < currentX) super.tick(LEFT);

					// If the y value is < than our y value
					// only thing left to do is to drop our piece.
				else if (move.y < currentY) super.tick(DROP);
			}
		}
		super.tick(verb);
	}

	@Override
	public JComponent createControlPanel() {
		panel = super.createControlPanel();
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);
		return panel;
	}
}
