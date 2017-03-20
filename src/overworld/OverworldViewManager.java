package overworld;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import battle.MapDisplay;
import utilities.CardManager;
import utilities.Obstruction;


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
	int playerTurn;

	public OverworldViewManager(int startX, int startY, Obstruction[][] worldMap, int width, int height, int playerTurn){
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
		this.playerTurn = playerTurn;
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

	public void addNewUnitToMap(Unit unitToAdd, int settlementIDToAddToo, int player){
		//get the x and y of the settlement that the unit has been added from
		int newArmyX = 0;
		int newArmyY = 0;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementIDToAddToo);
			while(rs.next()){
				//loop through all rows in the table that were returned
				//set the x and y of the new army to just above and to the left of the settlement
				newArmyX = rs.getInt(3) - 1;
				newArmyY = rs.getInt(4) - 1;
			}

			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
		//make a new army with the new unit
		ArrayList<Unit> controlledUnits = new ArrayList<Unit>();
		controlledUnits.add(unitToAdd);
		Army newArmy = new Army(controlledUnits, unitToAdd.getImageString(), newArmyX, newArmyY, player);
		//add the new army to the list of armies on the map
		//set the default movement size to the speed of the unit in the army
		newArmy.updateMaxMovement(newArmy.getUnits().get(0).getSpeed());
		allArmies.add(newArmy);
		//check the location the new army was created at for interaction
		checkNewArmyLocation(newArmy);
		//refresh the map
		repaint();
	}

	public int[] getAvailableLocation(Unit unit, ArrayList<Unit> unitList, int[] unitCoords){
		//create a rectangle with the dimensions of the unit in the location being tried
		Rectangle testRectangle = new Rectangle(unitCoords[0], unitCoords[1], unit.getWidth(), unit.getWidth());
		//loop through the units
		for(Unit nextUnit: unitList){
			//check if the new rectangle intersects a unit which is not itself
			if(testRectangle.intersects(nextUnit.getRect()) && nextUnit != unit){
				//create two random numbers between -1 and 1
				Random rand = new Random();
				int randomX = 1 - rand.nextInt(3);
				int randomY = 1 - rand.nextInt(3);
				//change the coordinates of the test location one unit width in the random directions
				unitCoords[0] += randomX * unit.getWidth();
				unitCoords[1] += randomY * unit.getWidth();
				//check the availability of the new location
				unitCoords = getAvailableLocation(unit, unitList, unitCoords);
				break;
			}
		}
		//once the recursion has found an available location return it
		return unitCoords;
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
				allArmies.get(i).replaceUnits(sortUnitsOnMovement(allArmies.get(i).getUnits()));
				//set the new maximum movement value on the sorted army
				allArmies.get(i).updateMaxMovement(allArmies.get(i).getUnits().get(0).getSpeed());
			}
		}
		repaint();
	}

	public ArrayList<Unit> sortUnitsOnMovement(ArrayList<Unit> unitArray){
		//convert the array list into a object array
		Unit[] unitList = new Unit[unitArray.size()];
		for(int i = 0; i < unitArray.size(); i ++){
			unitList[i] = unitArray.get(i);
		}
		//loop through the number of items in the list (the maximum number of iterations)
		for(Unit nextUnit: unitList){
			//set a boolean to changed being false
			Boolean changed = false;
			//loop through the unit list
			for(int count = 0; count < unitList.length - 1; count ++){
				//check if one unit has a speed greater than the speed of the next unit in the list
				if(unitList[count].getSpeed() > unitList[count + 1].getSpeed()){
					//if it does then swap the units
					Unit holder = unitList[count];
					unitList[count]= unitList[count + 1];
					unitList[count + 1] = holder;
					//indicate that there has been a change
					changed = true;
				}
			}
			if(!changed){
				//if there was no change in this run then the units are in order and so stop running iterations
				break;
			}
		}
		//convert the unit list back into an array list
		unitArray.clear();
		for(int index = 0; index < unitList.length; index ++){
			unitArray.add(unitList[index]);
		}
		//return the array list of units
		return unitArray;
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
		//check if an army is selected
		if(armyHovering && selectionLocked){
			//update the armies movement army in case one has been unselected
			checkArmyMovementArea();
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

	public void updatePlayerTurn(int currentPlayerTurn){
		//update the current player turn
		playerTurn = currentPlayerTurn;
	}

	public void resetArmyMovement(){
		//loop through the armies and reset their movement to their maximum
		for(Army nextArmy: allArmies){
			nextArmy.resetTurnMovement();
		}
	}

	public void moveSelectedArmy(int x, int y){
		//check if the player whose turn it is controls this army
		if(allArmies.get(hoveringID).getPlayerIndex() == playerTurn){
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
			allArmies.get(hoveringID).moveSquares(distanceMoved);
			//recalculate the area the army can now more into
			checkArmyMovementArea();

			//check the new army location to see if the army was moved onto another army or a settlement
			checkNewArmyLocation(allArmies.get(hoveringID));
			repaint();
		}
	}

	public void giveCardManager(CardManager newCM){
		//be given the card manager used throughout the program
		cM = newCM;
	}

	public void giveMapDisplay(MapDisplay newMD){
		//be given the map display used by the program
		mD = newMD;
	}

	public void checkNewArmyLocation(Army movedArmy){
		//get the coordinates of the new army location
		//check if another army is in that square
		for(int i = 0; i < allArmies.size(); i ++){
			Army nextArmy = allArmies.get(i);
			if(nextArmy.getX() == movedArmy.getX() && nextArmy.getY() == movedArmy.getY() && nextArmy != movedArmy){
				//check if the army already at the location is owned by the same player
				if(nextArmy.getPlayerIndex() == movedArmy.getPlayerIndex()){
					//the army has intersected another player owned army
					//move the units from the army just moved into the army that it was moved onto
					nextArmy.addUnits(movedArmy.getUnits());
					//for each unit in added check their location and if needed move them so they do not intersect another unit
					for(Unit nextUnit: movedArmy.getUnits()){
						int[] currentLocation = new int[2];
						currentLocation[0] = nextUnit.getX();
						currentLocation[1] = nextUnit.getY();
						int[] freeLocation = getAvailableLocation(nextUnit, nextArmy.getUnits(), currentLocation);
						nextUnit.setX(freeLocation[0]);
						nextUnit.setY(freeLocation[1]);
					}
					//re-sort the units in the new larger army in order of their movement speeds
					nextArmy.replaceUnits(sortUnitsOnMovement(nextArmy.getUnits()));
					//set the new maximum movement value on the sorted army to the lowest speed of the units in it
					nextArmy.updateMaxMovement(nextArmy.getUnits().get(0).getSpeed());
					//delete the army that was just moved
					allArmies.remove(movedArmy);
					//check if the index of the army joined was higher than the one that was moved
					if(hoveringID > i){
						//set the hoveringID to the new larger army
						hoveringID = i;
					} else {
						//otherwise set the hoveringID to the new larger army and then decrease it by 1 to accommodate for there being one less army remaining
						hoveringID = i - 1;
					}

				} else {
					//the army has intersected another player's army
					//cause a battle between the two armies
					fightArmies(allArmies.get(hoveringID), nextArmy);
				}
			}
		}
		//if the new location does not intersect another army then check if it intersects a settlement
		if(map[movedArmy.getX()][movedArmy.getY()].isSettlement()){
			//the army was moved onto a settlement
			//get the settlement id that was moved onto
			int intersectedSettlementID = map[movedArmy.getX()][movedArmy.getY()].getSettlementID();
			//interact with the settlement
			interactWithSettlement(allArmies.get(hoveringID), intersectedSettlementID);
		}

	}

	public void fightArmies(Army attacker, Army defender){
		//create a list with all the units that are to be involved in the fight
		ArrayList<Unit> allUnits = new ArrayList<Unit>();
		allUnits.addAll(attacker.getUnits());
		allUnits.addAll(defender.getUnits());
		//check all units on the attacking army to check they are not intersection any units on the enemy team
		for(Unit nextUnit: allUnits){
			int[] currentLocation = new int[2];
			currentLocation[0] = nextUnit.getX();
			currentLocation[1] = nextUnit.getY();
			int[] freeLocation = getAvailableLocation(nextUnit, allUnits, currentLocation);
			nextUnit.setX(freeLocation[0]);
			nextUnit.setY(freeLocation[1]);
		}
		//set up a fight between the two armies then set the frame focus to the map display 
		mD.setUpNewBattle(attacker, defender);
		cM.showCard("OverCard", "BattleCard");
	}

	public void interactWithSettlement(Army army, int settlementID){
		//set a default player number
		int settlementPlayer = 0;
		//connect to the data base and get the player number of the settlement
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
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
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
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
				//check if the player whose turn it is controls the army selected
				if(allArmies.get(hoveringID).getPlayerIndex() == playerTurn){
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
			
			//display the index of the player whose turn it is
			g.setColor(Color.BLACK);
			g.drawString("Current player: " + playerTurn, 10, 20);
		}
	}
}
