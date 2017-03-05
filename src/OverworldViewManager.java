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
	Boolean withinArmyMovement;
	ArrayList<int[]> potentialMovementSquares;
	int[] mouseCoords;
	CardManager cM;
	MapDisplay mD;
	int width;
	int height;

	public OverworldViewManager(int startX, int startY, Obstruction[][] worldMap, int width, int height){
		//initialise the variables including the 2D array that contains the map terrain
		xOffset = startX;
		yOffset = startY;
		map = worldMap;
		scaleSize = 0.5f;
		scaledWidth = (int) (70 * scaleSize);
		squareSize = width/scaledWidth;
		armyHovering = false;
		settlementHovering = false;
		selectionLocked = false;
		lastSelectedID = 0;
		withinArmyMovement = false;
		potentialMovementSquares = new ArrayList<int[]>();
		mouseCoords = new int[2];
		this.width = width;
		this.height = height;

		//set the size of the viewport to a square of 700 by 700 pixels

		setPreferredSize(new Dimension(width, height));

		//create a new paint class
		Paint display = new Paint();
		//allow the paint class to fill the panel display
		display.setPreferredSize(new Dimension(width, height));
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
			if(xOffset + scaledWidth < 140){
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
			if(yOffset + scaledWidth < 140){
				yOffset += change;
			}
		}
		repaint();
	}

	public void updateArmies(){
		//loop through all armies
		for(int i = 0; i < allArmies.size(); i ++){
			//check if the army still contains units
			if(allArmies.get(i).getUnits().size() == 0){
				//check if the army is selected
				if(allArmies.get(i).isSelected()){
					//set the army to unselected and update the class to avoid pointers to the removed army
					allArmies.get(i).setSelected(false);
					//clear the movement squares
					potentialMovementSquares.clear();
					//set the mouse to not within the movement grid
					withinArmyMovement = false;
					//set the army hovering to false and set the selection to unlocked
					armyHovering = false;
					selectionLocked = false;
				}
				
				//check if a different army is selected
				else if(armyHovering){
					//check if the army that is currently selected has a ID that is lower than the index of the army to be removed
					if(i < hoveringID){
						//lower the hoveringID by 1 to compensate for removing an item lower in the list than the hovering army ID
						hoveringID --;
					}
				}

				//remove this army from the list and lower the iterator to compensate
				allArmies.remove(i);
				i--;
			} else {
				//if the army still has units then recalculate the move distance for that army
				allArmies.get(i).updateMaxMovement();
			}
		}
		repaint();
	}

	public Boolean selectItemAtLocation(int x, int y){
		//covert the location into the corresponding grid square
		int[] coords = getGridLocation(x, y);
		//set the mouse coordinates to the grid coordinates calculated
		mouseCoords = coords.clone();
		//check that the selection is not currently locked otherwise maintain the last selected item
		if(!selectionLocked){
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
		} else {
			//if the selection is locked then check if there is an army selected
			if(armyHovering){
				//if an army is selected then check if the mouse is within the movement area
				checkArmyMovementArea();
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
		//check if the user is trying to move an army
		if(armyHovering && selectionLocked && withinArmyMovement){
			moveSelectedArmy(x, y);
		} else {
			//if the user is not trying to move an army then continue with the standard selection
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

	}

	public void checkArmyMovementArea(){

		//reset the boolean for mouse within army movement to false
		withinArmyMovement = false;

		//get the movement distance remaining for the army
		int movement = allArmies.get(hoveringID).getPotentialMovement();

		//clear the list of movement coordinates
		potentialMovementSquares.clear();
		//loop through the square of locations the army can move to
		for(int iX = ((movement - 1) * -1); iX < movement; iX ++){
			for(int iY = ((movement - 1)* -1); iY < movement; iY ++){
				//add each square to an array list of the squares the army can move into
				int[] squareCoords = new int[2];
				squareCoords[0] = iX + allArmies.get(hoveringID).getX();
				squareCoords[1] = iY + allArmies.get(hoveringID).getY();
				potentialMovementSquares.add(squareCoords);
				//check if the square being currently added  is in the same location as the mouse
				if(mouseCoords[0] + xOffset == squareCoords[0] && mouseCoords[1] + yOffset == squareCoords[1]){
					//if the mouse is in one of the squares the army can move to then set the respective boolean to true
					withinArmyMovement = true;
				}
			}
		}

	}

	public void moveSelectedArmy(int x, int y){
		//get the coordinates of the click location
		int[] coords = getGridLocation(x, y);
		//get the number of squares moved (the largest out of x or y)
		int distanceMoved = Math.abs(allArmies.get(hoveringID).getX() - (coords[0] + xOffset));
		if(Math.abs(allArmies.get(hoveringID).getY() - (coords[1] + yOffset)) > Math.abs(allArmies.get(hoveringID).getX() - (coords[0] + yOffset))){
			distanceMoved = Math.abs(allArmies.get(hoveringID).getY() - (coords[1] + yOffset));
		}
		//move the selected army to the new location
		allArmies.get(hoveringID).setX(coords[0] + xOffset);
		allArmies.get(hoveringID).setY(coords[1] + yOffset);
		//reduce the further movements the army can make by the distance moved
		//allArmies.get(hoveringID).moveSquares(distanceMoved);
		//recalculate the area the army can now more into
		checkArmyMovementArea();

		//check the new army location to see if the army was moved onto another army or a settlement
		checkNewArmyLocation(x, y, allArmies.get(hoveringID));
		repaint();
	}

	public void giveCardManager(CardManager newCM){
		//be given the card manager used throughout the program
		cM = newCM;
	}

	public void giveMapDisplay(MapDisplay newMD){
		//be given the map display used by the program
		mD = newMD;
	}

	public void checkNewArmyLocation(int x, int y, Army movedArmy){
		//get the coordinates of the new army location
		int[] coords = getGridLocation(x, y);
		//check if another army is in that square
		for(Army nextArmy: allArmies){
			if(nextArmy.getX() == coords[0] + xOffset && nextArmy.getY() == coords[1] + yOffset && nextArmy != movedArmy){
				//check if the army already at the location is owned by the same player
				System.out.println("There is another army");
				if(nextArmy.getPlayerIndex() == allArmies.get(hoveringID).getPlayerIndex()){
					//the army has intersected another player owned army

				} else {
					//the army has intersected another player's army
					//cause a battle between the two armies
					System.out.println("It is an enemy");
					fightArmies(allArmies.get(hoveringID), nextArmy);
				}
			}
		}
		//if the new location does not intersect another army then check if it intersects a settlement
		if(map[coords[0] + xOffset][coords[1] + yOffset].isSettlement()){
			//the army was moved onto a settlement
			//get the settlement id that was moved onto
			int intersectedSettlementID = map[coords[0] + xOffset][coords[1] + yOffset].getSettlementID();
			//interact with the settlement
			interactWithSettlement(allArmies.get(hoveringID), intersectedSettlementID);
		}

	}

	public void fightArmies(Army attacker, Army defender){
		//set up a fight between the two armies then set the frame focus to the map display 
		mD.setUpNewBattle(attacker, defender);
		cM.showCard("OverCard", "BattleCard");
	}

	public void interactWithSettlement(Army army, int settlementID){
		//set a default player number
		int settlementPlayer = 0;
		//connect to the data base and get the player number of the settlement
		try{
			System.out.println("Attempting connection");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			System.out.println("Connected \n");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementID);
			while(rs.next()){
				//loop through all rows in the table that were returned
				//set the settlement name to that stored in the correct column
				settlementPlayer = rs.getInt(9);
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}

		//check if the player that owns the army also owns the settlement
		if(settlementPlayer == army.getPlayerIndex()){
			//the player owns both army and settlement
		} else {
			//the army is attacking the settlement
			//take over the settlement by changing the player number in the data base
			try{
				System.out.println("Attempting connection");
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				System.out.println("Connected \n");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				//change the player number of the settlement
				stmt.executeUpdate("update settlements set player = " + army.getPlayerIndex() + " where id = " + settlementID);
				ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementID);
				while(rs.next()){
					//loop through all rows in the table that were returned
					//set the settlement name to that stored in the correct column
					settlementPlayer = rs.getInt(9);
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
		}
	}

	public int getSelectedID(){
		//return the ID of the selected item
		return hoveringID;
	}
	public boolean isSelectedSettlement(){
		//return if a settlement is currently selected
		return settlementHovering;
	}
	public boolean isSelectedArmy(){
		//return if an army is currently selected
		return armyHovering;
	}

	public int[] getGridLocation(int x, int y){
		//set up an array with references to a grid square on the map
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
			g.fillRect(0, 0, 700, 700);

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

			//check if an army is currently selected and the selection is also locked
			if(selectionLocked && armyHovering){
				//highlight the grid squares that the army can move into
				g.setColor(Color.BLUE);
				for(int i = 0; i < potentialMovementSquares.size(); i ++){
					g.drawRect((potentialMovementSquares.get(i)[0] - xOffset) * squareSize, (potentialMovementSquares.get(i)[1] - yOffset)* squareSize, squareSize, squareSize);
				}
				//check if the mouse if within the grid of squares that the army can move into
				if(withinArmyMovement){
					//highlight the box that the mouse is in
					g.setColor(Color.YELLOW);
					g.drawRect(mouseCoords[0] * squareSize, mouseCoords[1] * squareSize, squareSize, squareSize);
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
