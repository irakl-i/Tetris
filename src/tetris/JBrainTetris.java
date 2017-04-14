package tetris;

import javax.swing.*;
import java.awt.*;

/**
 * CS108 Tetris Game.
 * tetris.JBrainTetris presents a tetris game in a window.
 * It handles the GUI, the animation and the AI.
 * The tetris.Piece and tetris.Board classes handle the
 * lower-level computations.
 * <p>
 * Use Keys j-k-l to move, n to drop (or 4-5-6 0)
 * During animation, filled rows draw as green.
 * Clearing 1-4 rows scores 5, 10, 20, 40 points.
 * Clearing 4 rows at a time beeps!
 */
public class JBrainTetris extends JTetris {
	// Graphics
	private JComponent panel;
	private JPanel little;
	private JCheckBox brainMode;
	private JSlider adversary;

	// Brain
	private DefaultBrain brain;
	private Brain.Move move;
	private int count;

	/**
	 * Initialize the board and some instance variables.
	 *
	 * @param pixels
	 */
	public JBrainTetris(int pixels) {
		super(pixels);
		brain = new DefaultBrain();
		count = 0;
	}

	/**
	 * Creates a frame with a tetris.JBrainTetris.
	 */
	public static void main(String[] args) {
		// Set GUI Look And Feel Boilerplate.
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JTetris brainTetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(brainTetris);
		frame.setVisible(true);
	}

	/**
	 * Randomly selects the worst piece if adversary value is >= 0.
	 * @return worst piece
	 */
	@Override
	public Piece pickNextPiece() {
		// Get the slider value.
		int sliderNumber = adversary.getValue();
		// This has to be here for brain to work correctly for some reason.
		if (sliderNumber == 0) return super.pickNextPiece();

		// No cleaner way of choosing a random number between [1, 99]
		int randomNumber = random.nextInt(99) + 1;

		// If the randomNumber is >= than the slider piece should be
		// chosen randomly by super's pickNextPiece().
		if (randomNumber >= sliderNumber) {
			System.out.println("*ok*");
			return super.pickNextPiece();
		}
		System.out.println("ok");

		Brain.Move worstMove = null;
		Brain.Move currentMove = null;
		// Iterate over the pieces and choose a piece
		// with the worst score. We can just save
		// Brain.Move objects as they also include Piece.
		for (Piece piece : pieces) {
			currentMove = brain.bestMove(board, piece, HEIGHT, null);
			if (currentMove != null && (worstMove == null || worstMove.score < currentMove.score)) {
				worstMove = currentMove;
			}
		}

		// Return piece from the worst move.
		return (worstMove != null) ? worstMove.piece : super.pickNextPiece();
	}

	/**
	 * Uses brain to compute the best possible move for each piece.
	 * @param verb
	 */
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
				if (!currentPiece.equals(move.piece)) {
					super.tick(ROTATE); // Could have used tick(x) (slower approach).
				}

				// If the move.x value is > than x value of our piece
				// we need to move our piece to the right.
				if (move.x > currentX) {
					super.tick(RIGHT);
				}

				// Same thing for the left.
				else if (move.x < currentX) {
					super.tick(LEFT);
				}

				// If the y value is < than our y value
				// only thing left to do is to drop our piece.
				else if (move.y < currentY) super.tick(DROP);
			}
		}
		super.tick(verb);
	}

	/**
	 * Creates a JTetris control panel with some additions.
	 * @return panel
	 */
	@Override
	public JComponent createControlPanel() {
		panel = super.createControlPanel();

		// Make brain checkbox and add it to the panel.
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);

		// Make a little panel, put a JSlider in it
		// and add it to the main panel.
		little = new JPanel();
		little.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0); // min, max, current
		adversary.setPreferredSize(new Dimension(100, 15));
		little.add(adversary);
		panel.add(little);


		return panel;
	}
}
