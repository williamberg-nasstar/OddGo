package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import util.CoordSet;

public class BoardView extends JPanel implements MouseListener{

	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = -2488201994652013371L;
	
	private static int PREFERRED_CELL_SIZE = 30;
	private static double REL_STONE_DIAMETER = 0.8;	// diameter of stones relative to board square size
	private static Color DEFAULT_BOARD_COLOR = new Color(190,140,75);
	private static Color DEFAULT_BACKGROUND_COLOR = new Color(40,160,50);
	private static double REL_SHADOW_OFFSET = 0.075;
	private static Color SHADOW_COLOR = new Color(0,0,0,128);
	private static Color GRID_COLOR = Color.BLACK;
	private static Color CROSS_COLOR = Color.RED;
	private static double REL_CROSS_SIZE = 0.3;
	private static double REL_DOT_DIAMETER = 0.25;
	private static int DOT_DISTANCE = 3;

	private GoGUI gui;
	private GoBoard board;
	private Coord crossCoord;
	private CoordSet deadStones = new CoordSet();
	private CoordSet blackCrosses = new CoordSet();
	private CoordSet whiteCrosses = new CoordSet();
	
	public BoardView(GoGUI gui, GoBoard board) {
		
		this.gui = gui;
		this.board = board;
		addMouseListener(this);
		setPreferredSize(new Dimension((board.getCols()+1)*PREFERRED_CELL_SIZE,(board.getRows()+1)*PREFERRED_CELL_SIZE));
		
	}
	
	public void paint (Graphics g) {
		
		g.setColor(DEFAULT_BACKGROUND_COLOR);
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
		
		int cellWidth = getWidth()/(board.getCols()+1);
		int cellHeight = getHeight()/(board.getRows()+1);
		int xMargin;
		int yMargin;
		
		if(cellWidth<cellHeight) {

			cellHeight = cellWidth;
			xMargin = cellWidth;
			yMargin = (getHeight() - ((board.getRows() - 1)*cellHeight)) / 2;
			
		} else {
			
			cellWidth = cellHeight;
			xMargin = (getWidth()-((board.getCols() - 1)*cellWidth)) / 2;
			yMargin = cellHeight;
			
		}
		
		int arcSize = cellWidth / 2;
		g.setColor(SHADOW_COLOR);
		g.fillRoundRect((int)(xMargin - cellWidth + cellWidth*REL_SHADOW_OFFSET), (int)(yMargin - cellHeight + cellHeight*REL_SHADOW_OFFSET), cellWidth*(board.getCols()+1), cellHeight*(board.getRows()+1),arcSize,arcSize);
		g.setColor(DEFAULT_BOARD_COLOR);
		g.fillRoundRect(xMargin - cellWidth, yMargin - cellHeight, cellWidth*(board.getCols()+1), cellHeight*(board.getRows()+1),arcSize,arcSize);
		g.setColor(GRID_COLOR);
		g.drawRoundRect(xMargin - cellWidth, yMargin - cellHeight, cellWidth*(board.getCols()+1), cellHeight*(board.getRows()+1),arcSize,arcSize);
		
		for (int i = 0; i < board.getCols(); i++){
			g.drawLine(xMargin + (cellWidth * i), yMargin, xMargin + (cellWidth * i), yMargin + cellHeight * (board.getRows() - 1));
		}
		for (int i = 0; i < board.getRows(); i++){
			g.drawLine(xMargin, yMargin + cellHeight * i, xMargin + cellWidth * (board.getCols() - 1), yMargin + cellHeight * i);
		}
		
// creates new painting context
		Graphics subG = g.create(xMargin,yMargin,getWidth()-xMargin,getHeight()-yMargin);
		subG.setClip(-xMargin, -yMargin, getWidth(), getHeight());

		int dotDiameter = (int) (cellWidth*REL_DOT_DIAMETER);
		int stoneDiameter = (int) (cellWidth*REL_STONE_DIAMETER);
		
// make dot array - if true, draw dot there, otherwise false
		
		boolean[][] dotArray = new boolean[board.getCols()][board.getRows()];
			
		if(!(board.getCols()==19 && board.getRows()==19)) {
			
			for(int i=DOT_DISTANCE;i<=board.getCols()/2;i+=DOT_DISTANCE) {
				for(int j=DOT_DISTANCE;j<=board.getRows()/2;j+=DOT_DISTANCE) {
					
					dotArray[i][j] = true;
					
				}
			}
			
			for(int i=board.getCols()-DOT_DISTANCE-1;i>=board.getCols()/2;i-=DOT_DISTANCE) {
				for(int j=DOT_DISTANCE;j<=board.getRows()/2;j+=DOT_DISTANCE) {
					
					dotArray[i][j] = true;
					
				}
			}
			
			for(int i=board.getCols()-DOT_DISTANCE-1;i>=board.getCols()/2;i-=DOT_DISTANCE) {
				for(int j=board.getRows()-DOT_DISTANCE-1;j>=board.getRows()/2;j-=DOT_DISTANCE) {
					
					dotArray[i][j] = true;
					
				}
			}
			
			for(int i=DOT_DISTANCE;i<=board.getCols()/2;i+=DOT_DISTANCE) {
				for(int j=board.getRows()-DOT_DISTANCE-1;j>=board.getRows()/2;j-=DOT_DISTANCE) {
					
					dotArray[i][j] = true;
					
				}
			}
		
		} else {
// standard dot spacing for 19x19
			dotArray[3][3] = true;
			dotArray[9][3] = true;
			dotArray[15][3] = true;
			dotArray[3][9] = true;
			dotArray[9][9] = true;
			dotArray[15][9] = true;
			dotArray[3][15] = true;
			dotArray[9][15] = true;
			dotArray[15][15] = true;
			
		}
		
		for(int i=0;i<board.getCols();i++) {
			for(int j=0;j<board.getRows();j++){
				if(dotArray[i][j]==true) {
					
					drawDot(subG,i,j,cellWidth,dotDiameter);
					
				}
			}
		}
		
		for(int i=0;i<board.getCols();i++) {
			for(int j=0;j<board.getRows();j++){
				if(!board.getStone(i, j).equals(CellState.EMPTY)) {
					drawShadow(subG, i, j, cellWidth, stoneDiameter);
				} else {
					
					if (blackCrosses.contains(new Coord(i,j))) {
						drawCross(subG, i, j, cellWidth, Color.BLACK);
					} else if (whiteCrosses.contains(new Coord(i,j))) {
						drawCross(subG, i, j, cellWidth, Color.WHITE);
					}
						
				}
				
				if(deadStones.contains(new Coord(i,j))) {
					drawDeadStone(subG, i, j, cellWidth, stoneDiameter, board.getStone(i, j));
				} else {
					drawStone(subG, i, j, cellWidth, stoneDiameter, board.getStone(i, j));
				}
			}
		}
		
		if(crossCoord!=null) {
			drawCross(subG, crossCoord.getX(), crossCoord.getY(), cellWidth, CROSS_COLOR);
		}
		
		
		
	}
	
	private void drawShadow(Graphics g, int col, int row, int cellSize, int stoneDiameter) {

		g.setColor(SHADOW_COLOR);
		int shadowOffset = (int) (cellSize*REL_SHADOW_OFFSET);
		g.fillOval(col*cellSize - stoneDiameter/2 + shadowOffset, row*cellSize - stoneDiameter/2 + shadowOffset, stoneDiameter, stoneDiameter);
		
	}
	
	private void drawStone(Graphics g, int col, int row, int cellSize, int stoneDiameter, CellState state) {

		if(state.equals(CellState.BLACK)) {
			g.setColor(Color.BLACK);
			g.fillOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
		} else if (state.equals(CellState.WHITE)) {
			g.setColor(Color.WHITE);
			g.fillOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
			g.setColor(Color.BLACK);
			g.drawOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
		}
		
	}
	
	private void drawDeadStone(Graphics g, int col, int row, int cellSize, int stoneDiameter, CellState state) {

		if(state.equals(CellState.BLACK)) {
			g.setColor(new Color(150,0,0));
			g.fillOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
		} else if (state.equals(CellState.WHITE)) {
			g.setColor(new Color(255,105,105));
			g.fillOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
			g.setColor(new Color(150,0,0));
			g.drawOval(col*cellSize - stoneDiameter/2, row*cellSize - stoneDiameter/2, stoneDiameter, stoneDiameter);
		}
		
	}
	
	private void drawDot(Graphics g, int col, int row, int cellSize, int dotDiameter) {
		
		g.setColor(GRID_COLOR);
		g.fillOval(col*cellSize - dotDiameter/2, row*cellSize - dotDiameter/2, dotDiameter, dotDiameter);
		
	}
	
	public void drawCross (Graphics g, int col, int row, int cellSize, Color color) {
		
		int crossSizeHalved = (int) ((cellSize*REL_CROSS_SIZE)/2);
		
		g.setColor(color);
		g.drawLine(col*cellSize - crossSizeHalved,
				row*cellSize - crossSizeHalved,
				col*cellSize + crossSizeHalved,
				row*cellSize + crossSizeHalved
		);
		g.drawLine(col*cellSize - crossSizeHalved,
				row*cellSize + crossSizeHalved,
				col*cellSize + crossSizeHalved,
				row*cellSize - crossSizeHalved
		);
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent me) {
		
		Coord bufferCoord = getGridLoc(me.getX(), me.getY());
		
		if(!gui.gameIsOver()) {
			
			gui.setUndoButtonEnabled(true);
			
			if(bufferCoord!=null && board.getStone(bufferCoord.getX(), bufferCoord.getY()).equals(CellState.EMPTY)) {
				
				gui.nextTurn();
				
				if (gui.isBlacksTurn()) {
					board = gui.getTurnLogic().doTurn(board,bufferCoord,CellState.BLACK,gui);
					gui.setPlayLabel("White to play");
				} else {
					board = gui.getTurnLogic().doTurn(board,bufferCoord,CellState.WHITE,gui);
					gui.setPlayLabel("Black to play");
				}
				
				setCrossCoord(bufferCoord);
				
			}
		
		} else {
			
			if(bufferCoord!=null) {
				
				CoordSet bufferGroup = groupOn(bufferCoord);
				
				if(!board.getStone(bufferCoord).equals(CellState.EMPTY)) {
				
					if(!deadStones.contains(bufferCoord)) {
						
						deadStones.add(bufferGroup);
					
						if(board.getStone(bufferCoord).equals(CellState.WHITE)) {
							gui.setBlackScore(gui.getBlackScore() + bufferGroup.size()*2);
						} else {
							gui.setWhiteScore(gui.getWhiteScore() + bufferGroup.size()*2);
						}
					
					} else {
						
						deadStones.remove(bufferGroup);
					
						if(board.getStone(bufferCoord).equals(CellState.WHITE)) {
							gui.setBlackScore(gui.getBlackScore() - bufferGroup.size()*2);
						} else {
							gui.setWhiteScore(gui.getWhiteScore() - bufferGroup.size()*2);
						}
						
					}
					
				} else {
					
					if(blackCrosses.contains(bufferCoord)) {
						gui.setBlackScore(gui.getBlackScore() - bufferGroup.size());
						gui.setWhiteScore(gui.getWhiteScore() + bufferGroup.size());
						blackCrosses.remove(bufferGroup);
						whiteCrosses.add(bufferGroup);
					} else if (whiteCrosses.contains(bufferCoord)) {
						gui.setWhiteScore(gui.getWhiteScore() - bufferGroup.size());
						whiteCrosses.remove(bufferGroup);
					} else {
						gui.setBlackScore(gui.getBlackScore() + bufferGroup.size());
						blackCrosses.add(bufferGroup);
					}
					
				}
				
			}
			
			gui.updateTurnLabel();
			
		}
		
		repaint();
			
	}
	
	private CoordSet groupOn(Coord coord) {
		
		return groupOnPrime(coord,board.getStone(coord), new CoordSet());
		
	}
	
	private CoordSet groupOnPrime(Coord coord, CellState colour, CoordSet tagged) {
		
		tagged.add(coord);
		
		for (Coord c : getAdjacentCoords(coord)) {
			if (!tagged.contains(c) && board.getStone(c).equals(colour)) {
				tagged.add(groupOnPrime(c, colour, tagged));
			}
		}
	
		return tagged;
		
	}
	
	private Coord[] getAdjacentCoords (Coord coord) {
		
		return new Coord[] {
			new Coord(coord.getX(),coord.getY()-1),
			new Coord(coord.getX()+1,coord.getY()),
			new Coord(coord.getX(),coord.getY()+1),
			new Coord(coord.getX()-1,coord.getY()),
		};
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	private void setCrossCoord(Coord coord) {
		
		crossCoord = coord;
		
	}
	
// gets the grid co-ordinates from a window-relative pixel distance x/y pair
	private Coord getGridLoc(int x, int y) {
		
		int cellWidth = getWidth()/(board.getCols()+1);
		int cellHeight = getHeight()/(board.getRows()+1);
		int xMargin;
		int yMargin;
		
		if(cellWidth<cellHeight) {
			
			cellHeight = cellWidth;
			xMargin = cellWidth;
			yMargin = (getHeight() - ((board.getRows() - 1)*cellHeight)) / 2;
			
		} else {
			
			cellWidth = cellHeight;
			xMargin = (getWidth()-((board.getCols() - 1)*cellWidth)) / 2;
			yMargin = cellHeight;
			
		}
		int xPos = (x-xMargin+cellWidth/2)/cellWidth;
		int yPos = (y-yMargin+cellHeight/2)/cellHeight;
		
// if outside the board
		if (xPos < 0 || yPos < 0 || xPos >= board.getCols() || yPos >= board.getRows()) {
			return null;
		}
		
		return new Coord(xPos, yPos);
	
	}
	
	public void unsetCrossCoord() {
		crossCoord = null;
	}

}