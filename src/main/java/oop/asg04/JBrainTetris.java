package oop.asg04;

import java.awt.Dimension;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


public class JBrainTetris extends JTetris {
	
	protected JCheckBox brainMode;
	private Brain.Move bestMove;
	protected static DefaultBrain brainDefault = new DefaultBrain();
	
	private JSlider Adversary;
	private static JLabel status;
	
	Piece p;
	private Piece[] pieces;
	public JBrainTetris(int pixels) {
		super(pixels);
	}
	
	public JComponent createControlPanel() {
		JPanel panel = (JPanel) super.createControlPanel();
		

		brainMode = new JCheckBox("Brain Active !");
		panel.add(brainMode);
		
		JPanel slider = new JPanel();

		slider.add(new JLabel("Adversary :"));
		Adversary = new JSlider(0, 100, 0);	// min, max, current
		Adversary.setPreferredSize(new Dimension(100, 15));
		slider.add(Adversary);
		panel.add(slider);

		status = new JLabel(" Ok ");
		panel.add(status);
		panel.add(Box.createVerticalStrut(100));
		
		
		return panel;
	}
	
	public void tick(int verb) {
		if (brainMode.isSelected()) {
			board.undo();
			bestMove = brainDefault.bestMove(board, currentPiece, board.getHeight(), bestMove);
			
			if ( !currentPiece.equals(bestMove.piece)) {
				super.tick(ROTATE);
			}
			else {
				if ( currentX > bestMove.x ) super.tick(LEFT);
				if ( currentX < bestMove.x ) super.tick(RIGHT);
			}
			super.tick(verb);
		}
		else 
			super.tick(verb);
	}
	
	@Override
	public Piece pickNextPiece() {
		int pieceNum = (int)(Math.random() * 99 + 1);
		if (pieceNum >=  Adversary.getValue()){
			status.setText("Ok");
			return super.pickNextPiece();
		}
		else 
		{
			status.setText("*Ok*");
			double max = 0;
			Piece[] pieces = Piece.getPieces();
			for (int i=0; i < pieces.length ; i++) {
				if (brainDefault.bestMove(board, pieces[i], board.getHeight(), bestMove).score > max) {
					max = brainDefault.bestMove(board, pieces[i], board.getHeight(), bestMove).score;
					p = pieces[i];
				}
			}
		}
		return p;
	}
	
	public static void main(String[] args) {
		
		JBrainTetris tetris = new JBrainTetris(16);
		JFrame frame = JTetris.createFrame(tetris);
		frame.setVisible(true);
		brainDefault = new DefaultBrain();
	}
}
