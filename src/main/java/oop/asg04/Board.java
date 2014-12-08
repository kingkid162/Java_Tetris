// Board.java
package oop.asg04;
/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid, gridBak;
	private boolean DEBUG = true;
	boolean committed;
	
	private int[] widths, heights;
	private int maxHeight;
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		
		// YOUR CODE HERE
		
		widths = new int[height];
		heights = new int[width];
		gridBak = new boolean[width][height];
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	private void setHeights(int x) {
		heights[x] =  0;
		for (int y=height-1; y >= 0; y--) {
			if (grid[x][y]) {
				heights[x] = y+1;
				break;
			}
		}
	}
	
	private void setWidths(int y) {
		widths[y] = 0;
		for (int x=0; x < width; x++) {
			if (grid[x][y] == true) {
				widths[y]++;
			}
		}
	}
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {
		maxHeight = 0;
		for (int x=0; x < width; x++) {
			maxHeight = Math.max(maxHeight, heights[x]);
		}
		return maxHeight; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
                // YOUR CODE HERE
			int h = 0;
		    while(h < height)
		    {
		        int rowWidth = 0; int i = 0;
		        while (i < width) {
		            if (grid[i][h]) rowWidth++;
		            i++;
		        }   
		        if (widths[h]!=rowWidth)	throw new RuntimeException("Wrong! Widths[" +h+ "] "
		        										+ "was " +rowWidth+ ", but was " +widths[h]);
		        h++;
		    }
		    int w =0;
	        while( w < width)
	        {
	            int columnHeight = 0;
	            for (int j = height-1; j >= 0 ; j--){
	                if(grid[w][j]) {
	                	columnHeight = j+1;
	                	break;
	                	}
	            }
	            if (heights[w]!=columnHeight)	throw new RuntimeException("Wrong! Heights[" +w+ "] "
											+ "was " +columnHeight+ ", but was " +heights[w]);
	            w++;
	        }
		}
	}
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int y = heights[x] - piece.getSkirt()[0];
		for (int i=1; i < piece.getSkirt().length; i++) {
			y = Math.max(y, heights[x+1] - piece.getSkirt()[i]);
		}
		return y; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if ((0 > x || x >= width ) || (0 > y || y >= height))
			return true;
		return grid[x][y]; // YOUR CODE HERE
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
			
		int result = PLACE_OK;
		
		backUp();
		
		int X, Y;
		TPoint[] p = piece.getBody().clone();
		for (int i=0; i < piece.getBody().length; i++) {
			p[i] = new TPoint(piece.getBody()[i]);
		}
		for (int i=0; i < p.length; i++) {
			X = x + p[i].x;
			Y = y + p[i].y;
			if ( 0 > X || X >= width || 0 > Y || Y >= height) {
				result = PLACE_OUT_BOUNDS;
				break;
			}
			else {
				if (grid[X][Y] == true){
					result = PLACE_BAD;
					break;
				}
				else {
					committed = false;
					grid[X][Y] = true;
					setHeights(X);
					setWidths(Y);
					maxHeight = getMaxHeight();
					
					if (widths[Y] == width) {
						result = PLACE_ROW_FILLED;
					}
				}
			}
		}
		sanityCheck();	
		// YOUR CODE HERE	
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if (committed) backUp();
        
        int rowsCleared = 0;
        // YOUR CODE HERE
        for (int i = maxHeight - 1; i >= 0; i--) {
			if (widths[i] == width) {
				for (int x = 0, y; x < width; x++) {
					for (y = i; y < maxHeight - 1; y++) {
						grid[x][y] = grid[x][y + 1];
					}
					grid[x][y] = false;				
					setHeights(x);
				}
				for (int y = 0; y < height; y++) {
					setWidths(y);
				}
				maxHeight = getMaxHeight();
				rowsCleared++;
			}
		}
		sanityCheck();
		committed = false;
		return rowsCleared;
}

	protected boolean[][] backUp() {
		for (int i=0; i < width; i++) {
			System.arraycopy(grid[i], 0, gridBak[i], 0, height);
		}
		return gridBak;
	}
	
	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/

	public void undo() {
        // YOUR CODE HERE
		if (committed == true) return;
		
    	boolean[][] temp = grid;
    	grid = gridBak;
    	gridBak = temp;
    
    	for (int x=0; x < width; x++) {
    		setHeights(x);
    	}
		for (int y=0; y < height; y++) {
			setWidths(y);
		}
    
    	committed = true;
    	backUp();
	}


	public void commit() {
        committed = true;  
	}

	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


