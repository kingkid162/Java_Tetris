package oop.asg04;

import static org.junit.Assert.*;
import org.junit.*;

public class BoardTest {
	Board b;
	private Piece p1, p2, p3, p4, p5;
	private Piece s11, s12, s13, s21, s22, s23;
	private Piece l11, l12, l13, l14, l15, l21, l22, l23, l24, l25;
	private Piece sq1, sq2, st1, st2, st3;
	
	Piece[] p;

	@Before
	public void setUp() throws Exception {
		
		p1 = new Piece(Piece.PYRAMID_STR);
		p2 = p1.computeNextRotation();
		p3 = p2.computeNextRotation();
		p4 = p3.computeNextRotation();
		p5 = p4.computeNextRotation();
		
		l11 = new Piece(Piece.L1_STR);
		l12 = l11.computeNextRotation();
		l13 = l12.computeNextRotation();
		l14 = l13.computeNextRotation();
		l15 = l14.computeNextRotation();
		
		l21 = new Piece(Piece.L2_STR);
		l22 = l21.computeNextRotation();
		l23 = l22.computeNextRotation();
		l24 = l23.computeNextRotation();
		l25 = l24.computeNextRotation();
		
		s11 = new Piece(Piece.S1_STR);
		s12 = s11.computeNextRotation();
		s13 = s12.computeNextRotation();
		
		s21 = new Piece(Piece.S2_STR);
		s22 = s21.computeNextRotation();
		s23 = s22.computeNextRotation();
		
		st1 = new Piece(Piece.STICK_STR);
		st2 = st1.computeNextRotation();
		st3 = st2.computeNextRotation();
		
		sq1 = new Piece(Piece.SQUARE_STR);
		sq2 = sq1.computeNextRotation();		
		
		p = Piece.getPieces();
		
		b = new Board(4,10);
		b.place(p1,0,0);
	}
	
	// Check the basic width/height/max after the one placement
    @Test
    public void testPlace_OK() {
        b.commit();
        int result = b.place(s12, 1, 1);
        assertEquals(Board.PLACE_OK, result);
    }

	@Test
	public void testPlace_Bad() {
		b.commit();
		b.place(s12, 1, 1);
		b.commit();
        int result = b.place(s12, 1, 1);
		assertEquals(Board.PLACE_BAD, result);
    }

    @Test
    public void testPlace_OutOfBound() {

        Board b2 = new Board(4,6);
		int result = b2.place(s12, 3, 1);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);
		b2 = new Board(4,6);
		result = b2.place(s12, 0, -1);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);
		b2 = new Board(4,6);
		result = b2.place(s12, 4, 3);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);
	}
	
	// Place sRotated into the board, then check some measures
	@Test
	public void testDropHeight() {
		b.commit();
		int result = b.place(s12, 1, 1);
		b.commit();
		assertEquals(1, b.dropHeight(st1,0));
		assertEquals(4, b.dropHeight(st2,0));
		assertEquals(2, b.dropHeight(p2,2));
		assertEquals(3, b.dropHeight(l11,2));		
	}
	
	// Make  more tests, by putting together longer series of 
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.
	
	@Test
	public void testClearRows_OneAtBottom() {
        //System.out.println(b);
		b.commit();
		int result = b.place(p2, 2, 0);
        //System.out.println(b);
		int rowsCleared = b.clearRows();
		
		assertEquals(1, rowsCleared);
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(1, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getColumnHeight(3));
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(0, b.getRowWidth(3));
		assertEquals(2, b.getMaxHeight());
	}

    @Test
    public void testClearRows_OneOnTop() {
        b.commit();
        int result = b.place(p[Piece.STICK].fastRotation(), 0, 2);
        int rowsCleared = b.clearRows();

        assertEquals(1, rowsCleared);
        assertEquals(1, b.getColumnHeight(0));
        assertEquals(2, b.getColumnHeight(1));
        assertEquals(1, b.getColumnHeight(2));
        assertEquals(0, b.getColumnHeight(3));
        assertEquals(3, b.getRowWidth(0));
        assertEquals(1, b.getRowWidth(1));
        assertEquals(0, b.getRowWidth(2));
        assertEquals(0, b.getRowWidth(3));
        assertEquals(2, b.getMaxHeight());
    }
	
	@Test
	public void testClearRows_TwoAtATime() {
        b.commit();
		b.place(p2, 2, 0);
		b.clearRows();
		b.commit();
		b.place(p[Piece.L1].fastRotation().fastRotation().fastRotation(), 0, 0);
        int rowsCleared = b.clearRows();
		
		assertEquals(2, rowsCleared);
		
		b.commit();
		b.place(p[Piece.L1].fastRotation(), 1, 0);
		b.commit();
		b.place(p[Piece.L2].fastRotation(), 1, 2);
        b.commit();
		b.place(p[Piece.STICK], 0, 0);
        rowsCleared = b.clearRows();
		assertEquals(2, rowsCleared);
		assertEquals(2, b.getColumnHeight(0));
		assertEquals(0, b.getColumnHeight(1));
		assertEquals(0, b.getColumnHeight(2));
		assertEquals(2, b.getColumnHeight(3));
		assertEquals(2, b.getRowWidth(0));
		assertEquals(2, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(2, b.getMaxHeight());
	}
	
	
	@Test
	public void testUndo() {
		b.commit();
		int result = b.place(p2, 2, 0);
		b.commit();
		int rowsCleared = b.clearRows();
		b.commit();
		result = b.place(p[Piece.STICK].fastRotation(), 0, 2);
		rowsCleared = b.clearRows();
		
		b.undo();
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(1, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getColumnHeight(3));
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(0, b.getRowWidth(3));
		assertEquals(2, b.getMaxHeight());

		result = b.place(p[Piece.STICK].fastRotation(), 0, 2);
		rowsCleared = b.clearRows();
		b.commit();
	
		result = b.place(p[Piece.L1].fastRotation().fastRotation().fastRotation(), 0, 0);
		rowsCleared = b.clearRows();
		b.undo();
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(1, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getColumnHeight(3));
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(2, b.getMaxHeight());
		result = b.place(p[Piece.L1].fastRotation().fastRotation().fastRotation(), 0, 0);
		rowsCleared = b.clearRows();
		b.commit();
		
		result = b.place(p[Piece.L1].fastRotation(), 1, 0);
		b.commit();
		result = b.place(p[Piece.L2].fastRotation(), 1, 2);
		b.commit();
		result = b.place(p[Piece.STICK], 0, 0);
		rowsCleared = b.clearRows();
	
		b.undo();
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(4, b.getColumnHeight(2));
		assertEquals(4, b.getColumnHeight(3));
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(1, b.getRowWidth(2));
		assertEquals(4, b.getMaxHeight());
		
		result = b.place(p[Piece.STICK], 0, 0);
		rowsCleared = b.clearRows();
		b.commit();
	}
	
	/*
	public static void main(String [] args) {
		Board b = new Board(3, 6);
		
		Piece pyr1 = new Piece(Piece.PYRAMID_STR);
		Piece pyr2 = pyr1.computeNextRotation();
		Piece pyr3 = pyr2.computeNextRotation();
		Piece pyr4 = pyr3.computeNextRotation();
		
		Piece s = new Piece(Piece.S1_STR);
		Piece sRotated = s.computeNextRotation();
		
		b.place(pyr1, 0, 0);
		System.out.println(b);
		b.commit();
		int result = b.place(sRotated, 1, 5);
		System.out.println(b);
		b.undo();
		b.place(sRotated, 1, 4);
		System.out.println(b);
		b.undo();
		b.place(sRotated, 1, 3);
		System.out.println(b);
		b.undo();
		b.place(sRotated, 1, 2);
		System.out.println(b);
		b.undo();
		b.place(sRotated, 1, 1);
		System.out.println(b);
		b.undo();
	}*/
	
}
