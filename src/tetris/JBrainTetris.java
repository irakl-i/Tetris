package tetris;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Luka on 4/12/2017.
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
		Brain.Move currentMove;
		// Iterate over the pieces and choose a piece
		// with the worst score. We can just save
		// Brain.Move objects as they also include Piece.
		for (Piece piece : pieces) {
			currentMove = brain.bestMove(board, piece, HEIGHT, null);
			if (worstMove == null || worstMove.score < currentMove.score) {
				worstMove = currentMove;
			}
		}

		// Return piece from the worst move.
		return worstMove.piece;
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
				if (!currentPiece.equals(move.piece)) {
					super.tick(ROTATE); // Could have used tick(x) (slower approach, nicer code(?)).
				}

				// If the move.x value is > than x value of our piece
				// we need to move our piece to the right.
				else if (move.x > currentX) {
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