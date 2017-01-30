import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class OverworldViewManager extends JPanel{
	//declare the needed variables
	int xOffset;
	int yOffset;
	Obstruction[][] map;
	JScrollPane viewport;
	float scaleSize;
	int scaledWidth;
	int squareSize;
	ArrayList<Army> allArmies;
	Boolean armyHovering;
	Boolean settlementHovering;
	int hoveringID;
	Boolean selectionLocked;
	int lastSelectedID;

	public OverworldViewManager(int startX, int startY, Obstruction[][] worldMap){
		//initialise the variables including the 2D array that contains the map terrain
		xOffset = startX;
		yOffset = startY;
		map = worldMap;
		scaleSize = 1f;
		scaledWidth = (int) (100 * scaleSize);
		squareSize = 1000/scaledWidth;
		armyHovering = false;
		settlementHovering = false;
		selectionLocked = false;
		lastSelectedID = 0;
		
		//set the size of the viewport to a square of 1000 by 1000 pixels

		setPreferredSize(new Dimension(1000, 1000));

		//create a new paint class
		Paint display = new Paint();
		//allow the paint class to fill the panel display
		display.setPreferredSize(new Dimension(1000, 1000));
		//add the paint class to the panel
		add(display);
	}

	public void changeXOffset(int change){
		//alter the offset to allow for a new segment being view on the world map
		if(change < 0){
			//if the change is in the left direction then check that the view is not already as far to the left as possible
			if(xOffset > 0){
				//if the change can be made then alter the offset
				xOffset += change;
			}
		} else {
			//otherwise f the change is to the right then check that the view is not already as far to the right as possible
			if(xOffset + scaledWidth < 200){
				//if the change can be made then alter the offset
				xOffset += change;
			}
		}
		repaint();
	}
	public void changeYOffset(int change){
		//as with the x offset but checking movement in the y direction
		if(change < 0){
			if(yOffset > 0){
				yOffset += change;
			}
		} else {
			if(yOffset + scaledWidth < 200){
				yOffset += change;
			}
		}
		repaint();
	}

	public Boolean selectItemAtLocation(int x, int y){
		//check that the selection is not currently locked otherwise maintain the last selected item
		if(!selectionLocked){
			//covert the location into the corresponding grid square
			int[] coords = getGridLocation(x, y);
			//check if there is a settlement at the location of the mouse
			if(map[coords[0] + xOffset][coords[1] + yOffset].getSettlementID() != 0){
				//if there is a settlement at this location then set the hovering ID to that of the settlement and indicate that it is a settlement that is selected
				settlementHovering = true;
				hoveringID = map[coords[0] + xOffset][coords[1] + yOffset].getSettlementID();
				//set that there is not a army selected (as both cannot be selected at the same time nor on the same grid square
				armyHovering = false;
				//loop through the armies to set them all to unselected
				for(Army nextArmy: allArmies){
					if(nextArmy.isSelected()){
						nextArmy.setSelected(false);
					}
				}
			} else {
				//otherwise if there is not a settlement at the mouse location
				//set that nothing is selected
				settlementHovering = false;
				armyHovering = false;
				//loop through the armies to check if there is an army in the gris square where the mouse is located
				for(int i = 0; i < allArmies.size(); i ++){
					Army nextArmy = allArmies.get(i);
					if(nextArmy.getX() == coords[0] + xOffset && nextArmy.getY() == coords[1] + yOffset){
						//if there is an army at the selected location then set is as selected
						nextArmy.setSelected(true);
						armyHovering = true;
						//set the ID of the hovering ID to the index in the army list that the army lies in
						hoveringID = i;
					} else {
						//if the army is not in the selected location then ensure that it is set as unselected
						nextArmy.setSelected(false);
					}
				}
			}
		}
		//update the GUI with any needed highlighting
		repaint();
		if(armyHovering || settlementHovering){
			//if an item is selected then return that there is something at the location passed to the class
			return true;
		} else {
			//if nothing was at the mouse location that return that there is nothing there
			return false;
		}
	}

	public void clickedAtLocation(int x, int y){
		//convert the click location into the grid space that was clicked
		int[] coords = getGridLocation(x, y);
		//check there is not currently a selection locked by a previous click
		if(!selectionLocked){
			//check if an item is currently highlighted
			if(settlementHovering || armyHovering){
				//if there is a highlighted item then lock the settlement selection
				selectionLocked = true;
			}
		} else {
			//if the selection was locked then unlock the selection
			selectionLocked = false;
			//set the last selected ID to that of the settlement that has just been unselected
			lastSelectedID = hoveringID;
			//check if there is a new item at the current click location
			if(selectItemAtLocation(x, y)){
				//if there is an item at the newly clicked location then check it is not the item that is already selected
				if(hoveringID == lastSelectedID){
					//if it is the same item that was already selected then continue having unselected it
				} else {
					//if the settlement was not the same one as was just unselected then lock it as selected
					selectionLocked = true;
				}
			}
		}

	}

	public int[] getGridLocation(int x, int y){
		//set up an array with references to a grid squaer on the map
		int[] coords = new int[2];

		//convert the x and y location into the grid reference for that square
		coords[0] = x/squareSize;
		coords[1] = y/squareSize;

		return coords;
	}

	public void giveArmies(ArrayList<Army> passedAllArmies){
		//set the armies on the map as the set passed to this class
		allArmies = passedAllArmies;
	}

	public class Paint extends JPanel{
		//construct the GUI display for this card
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			//paint a background to the map view
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 1000, 1000);

			//draw the map
			for(int xCount = 0; xCount < scaledWidth; xCount ++){
				for(int yCount = 0; yCount < scaledWidth; yCount ++){
					Obstruction currentSquare = map[xCount + xOffset][yCount + yOffset];
					g.setColor(currentSquare.getColor());
					g.fillRect(squareSize * xCount, squareSize * yCount, squareSize, squareSize);

					//check if the segment contains a settlement
					if(currentSquare.isSettlement()){
						//check if the settlement is selected
						if(settlementHovering && currentSquare.getSettlementID() == hoveringID){
							//if the settlement is selected then highlight it
							g.setColor(Color.YELLOW);
							g.drawRect(squareSize * xCount, squareSize * yCount, squareSize, squareSize);
						}
					}
				}
			}

			//draw the armies
			for(Army nextArmy: allArmies){
				g.drawImage(nextArmy.getImage(), (nextArmy.getX() - xOffset) * squareSize, (nextArmy.getY() - yOffset) * squareSize, squareSize, squareSize, null);
				//check if the army is selected
				if(nextArmy.isSelected()){
					//if the army is selected then highlight it
					g.setColor(Color.YELLOW);
					g.drawRect((nextArmy.getX() - xOffset) * squareSize, (nextArmy.getY() - yOffset) * squareSize, squareSize, squareSize);
				}
			}
		}
	}
}
