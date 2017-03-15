package mapmaker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utilities.Obstruction;


public class MMCanvasUI extends JPanel implements MouseMotionListener, MouseListener{
	
	//declare the variables to be used throughout the class
	int mouseX;
	int mouseY;
	int brushSize;
	Color brushColor;
	int roughness;
	int tileType;

	Obstruction[][] obstructionMap;
	Obstruction selectedObstruction;
	MMEventManager eM;

	int gridSize;
	int squareSize;
	Boolean isSettlement;
	int settlementID;


	public MMCanvasUI(){
		//set the size of the panel and initialise the variables to be used
		setPreferredSize(new Dimension(700, 700));
		brushSize = 2;
		roughness = 1;
		tileType = 0;
		isSettlement = false;
		settlementID = 0;

		//create the display and set its size to all available space
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(700, 700));

		//add the display to the panel and add mouse listeners to the class
		add(display);
		addMouseListener(this);
		addMouseMotionListener(this);
		brushColor = Color.BLACK;

		//set the default size of the grid and squares on it
		gridSize = 70;
		squareSize = 10;

		//create a new array to hold the created map
		obstructionMap = new Obstruction[gridSize][gridSize];
		//make sure the map is empty and ready to be used
		resetMap();
		//set a default obstruction to be used
		selectedObstruction = new Obstruction(0, 0, squareSize, squareSize, Color.WHITE, 1, 0, false, 0);
	}

	public void changeGridSize(int newSize){
		//change the size of the obstruction array to reflect the new grid size
		obstructionMap = new Obstruction[newSize][newSize];
		gridSize = newSize;

		//recalculate the size of a square with the new size grid
		squareSize = 700 / gridSize;
		//update the values on the control panel for the map maker
		eM.updatePanel(roughness, brushColor, tileType, squareSize);
		//reset the map ready for a new map
		resetMap();
		repaint();
	}

	public void giveEManager(MMEventManager eManager){
		//get the even manager used by the map maker
		eM = eManager;
	}

	public void createNewObstruction(int cX, int cY){
		//check if a settlement is being placed
		if(isSettlement){
			//increase the index of the settlement ID to avoid duplicate settlements
			settlementID ++;
		}
		//loop through all the grid squares covered by the brush
		for(int sizeX = 0; sizeX < brushSize; sizeX ++){
			for(int sizeY = 0; sizeY < brushSize; sizeY ++){
				//check if the location is on the grid
				if(cX + sizeX < gridSize && cX + sizeX > -1 && cY + sizeY < gridSize && cY + sizeY > -1){
					//if t is on the grid then set the square there to have the values currently selected by the user
					obstructionMap[cX + sizeX][cY  +sizeY] = new Obstruction((cX + sizeX) * squareSize, (cY + sizeY) * squareSize, squareSize, squareSize, brushColor, roughness, tileType, isSettlement, settlementID);
				}
			}
		}

	}
	public void copyObject(int cX, int cY){
		//copy the values of the grid square that the brush is currently over
		roughness = obstructionMap[cX][cY].getRoughness();
		brushColor = obstructionMap[cX][cY].getColor();
		tileType = obstructionMap[cX][cY].getTileType();
		//update the control panel with the new values
		eM.updatePanel(roughness, brushColor, tileType, squareSize);
	}
	public void removeObstruction(int cX, int cY){
		//loop though all the grid squares that the brush is currently over
		for(int sizeX = 0; sizeX < brushSize; sizeX ++){
			for(int sizeY = 0; sizeY < brushSize; sizeY ++){
				//return the grid squares to their default values
				obstructionMap[cX + sizeX][cY  +sizeY] = new Obstruction((cX + sizeX) * squareSize, (cY + sizeY) * squareSize, squareSize, squareSize, Color.WHITE, 1, 0, false, 0);
			}
		}
	}

	public void updateBrushSize(int newBrushSize){
		//update the brush size to the given value
		brushSize = newBrushSize;
	}
	public void updateRoughness(int newRoughness){
		//update the roughness to the given value
		roughness = newRoughness;
	}
	public void updateColor(Color newColor){
		//update the brush colour to the given value
		brushColor = newColor;
	}
	public void updateTileType(int newTileType){
		//update the terrain type to the given value
		tileType = newTileType;
	}

	public Obstruction[][] getObstructionMap(){
		//return the current map
		return obstructionMap;

	}
	public void setObstructionMap(Obstruction[][] loadObstructionMap){
		//set the current map to the given array of squares
		obstructionMap = loadObstructionMap;
	}
	public void emptyMap(){
		//clear the map and repaint it
		resetMap();
		repaint();
	}
	public void resetMap(){
		//loop through all the grid squares in the array
		for(int x = 0; x < gridSize; x ++){
			for(int y = 0; y < gridSize; y ++){
				//reset them to default values
				obstructionMap[x][y] = new Obstruction(x * squareSize, y * squareSize, squareSize, squareSize, Color.WHITE, 1, 0, false, 0);
			}
		}
	}
	public void placingSettlements(Boolean isPlacing){
		//set wheter the user is placing settlements as the given boolean
		isSettlement = isPlacing;
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			//paint a background display
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 700, 700);

			//loop through all the grid spaces
			for(int x = 0; x < gridSize; x ++){
				for(int y = 0; y < gridSize; y ++){
					//paint each grid square with the correct colour
					g.setColor(obstructionMap[x][y].getColor());
					g.fill(obstructionMap[x][y].getRect());
					//check if the grid square holds part of a settlement
					if(obstructionMap[x][y].isSettlement()){
						//if there is a settlement at the location then indicate this with a red outline
						g.setColor(Color.RED);
						g.draw(obstructionMap[x][y].getRect());
					}
				}
			}
			//draw in the current brush location
			g.setColor(Color.BLACK);
			g.drawRect(selectedObstruction.getX(), selectedObstruction.getY(), brushSize * squareSize, brushSize * squareSize);
		}
	}


	@Override
	public void mouseDragged(MouseEvent event){
		//convert the mouse click location into the top left corner of the brush
		mouseX = event.getX() - (brushSize * squareSize)/2;
		mouseY = event.getY() - (brushSize * squareSize)/2;

		//check if the location is on the map grid
		if((int) (mouseX / squareSize) < gridSize && (int) (mouseX / squareSize) > -1 && (int) (mouseY / squareSize) < gridSize && (int) (mouseY / squareSize) > -1){
			//if it is then update where the brush is being draw to the new lcoation
			selectedObstruction.setLocation(obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getX(), obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getY());
		}

		//check if the mouse button being dragged is a left mouse button
		if(SwingUtilities.isLeftMouseButton(event)){
			//if it is then 'paint' the grid squares in the brush to the set values 
			createNewObstruction((int) (mouseX / squareSize), (int) (mouseY / squareSize));
		} else if(SwingUtilities.isRightMouseButton(event)){
			//otherwise if it is a right mouse button then reset the values of any grid squares in then brush to defaults
			removeObstruction((int) (mouseX / squareSize), (int) (mouseY / squareSize));
		}
		//repaint the display
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent event) {
		//correct the mouse location to the top left corner of the brush
		mouseX = event.getX() - (brushSize * squareSize)/2;
		mouseY = event.getY() - (brushSize * squareSize)/2;
		//check if the corrected location is on the grid
		if((int) (mouseX / squareSize) < gridSize && (int) (mouseX / squareSize) > -1 && (int) (mouseY / squareSize) < gridSize && (int) (mouseY / squareSize) > -1){
			//if the location is on the map then set the location of the brush to the new location
			selectedObstruction.setLocation(obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getX(), obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getY());
		}
		//repaint the display
		repaint();
	}
	@Override
	public void mouseClicked(MouseEvent event) {
		//correct the mouse location to the top left to the brush
		mouseX = event.getX() - (brushSize * squareSize)/2;
		mouseY = event.getY() - (brushSize * squareSize)/2;
		//check if the corrected location is on the grid
		if((int) (mouseX / squareSize) < gridSize && (int) (mouseX / squareSize) > -1 && (int) (mouseY / squareSize) < gridSize && (int) (mouseY / squareSize) > -1){
			//if it is then set the new location of the brush to the mouse location
			selectedObstruction.setLocation(obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getX(), obstructionMap[(int)mouseX/squareSize][(int)mouseY/squareSize].getY());
		}
		
		//switch based on which mouse button has been clicked
		switch(event.getButton()){
		case 1:
			//if the left button has been clicked then set the values of any grid squares in the brush to the current set values
			createNewObstruction((int) (mouseX / squareSize), (int) (mouseY / squareSize));
			break;
		case 2:
			//if the middle mouse button was clicked then copy the values of the grid square in the top left corner of the brush
			copyObject((int) (mouseX / squareSize), (int) (mouseY / squareSize));
			break;
		case 3:
			//if the right mouse button was clicked then reset the values of any grid squares in the brush to the default values
			removeObstruction((int) (mouseX / squareSize), (int) (mouseY / squareSize));
			break;
		default:
			break;
		}
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent event) {

	}
	@Override
	public void mouseExited(MouseEvent event) {

	}
	@Override
	public void mousePressed(MouseEvent event) {

	}
	@Override
	public void mouseReleased(MouseEvent event) {

	}
}
