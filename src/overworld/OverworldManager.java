package overworld;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import battle.MapDisplay;
import settlement.SettlementManager;
import utilities.CardManager;
import utilities.Obstruction;
import utilities.SaveManager;


public class OverworldManager extends JPanel{
	//declare the variables and classes that will be used throughout this class
	int screenWidth;
	int screenHeight;
	int currentPlayerTurn;
	CardManager cM;
	SettlementManager settlementM;
	OverworldViewManager viewport;
	OverworldInformationPanel infoPanel;
	Obstruction[][] map;
	ArrayList<Army> allArmies;

	public OverworldManager(int screenWidth, int screenHeight){
		//initialise the new layout manager that this class and panel will use
		GridBagConstraints c = new GridBagConstraints();
		setLayout(new GridBagLayout());

		//create a new save manager
		SaveManager sM = new SaveManager();
		//use the save manager to retrieve the map terrain and settlement locations as a map
		map = sM.loadBlockages("OverworldMap.txt");

		// set the size of the panel to the size of the application frame
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;

		//create a new viewport passing the start offset (currently 0, 0 to indicate that the initial view is of the top left corner of the map
		viewport = new OverworldViewManager(0, 0, map, 700, screenHeight);
		//create a new paint display and allocate it the entire frame to allow animation and display
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(screenWidth, screenHeight));
		
		//initialise the array of the armies on the world map
		allArmies = new ArrayList<Army>();
		
		//for testing purposes add in an initial army to manipulate
		ArrayList<Unit> testArmyUnits = new ArrayList<Unit>();
		testArmyUnits.add(new Unit(400, 340, 5, 100, 10, "land", 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		testArmyUnits.add(new Unit(330, 270, 5, 200, 20, "air", 500, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		testArmyUnits.add(new Unit(650, 550, 5, 100, 10, "sea", 200, 10, new ImageIcon("TestUnitThree.jpg").getImage()));
		Army testArmy1 = new Army(testArmyUnits, new ImageIcon("TestArmy.png").getImage(), 20, 20, 0);
		//add the test army to the array of armies
		testArmy1.updateMaxMovement(20);
		allArmies.add(testArmy1);
		
		//create and add a second army on a different team
		ArrayList<Unit> testArmyUnits2 = new ArrayList<Unit>();
		testArmyUnits2.add(new Unit(600, 320, 5, 100, 10, "land", 100, 10, new ImageIcon("TestUnitFour.jpg").getImage()));
		testArmyUnits2.add(new Unit(615, 320, 5, 100, 10, "land", 100, 10, new ImageIcon("TestUnitFour.jpg").getImage()));
		testArmyUnits2.add(new Unit(600, 335, 5, 100, 10, "land", 100, 10, new ImageIcon("TestUnitFour.jpg").getImage()));
		testArmyUnits2.add(new Unit(615, 335, 5, 100, 10, "land", 100, 10, new ImageIcon("TestUnitFour.jpg").getImage()));
		Army testArmy2 = new Army(testArmyUnits2, new ImageIcon("TestArmy.png").getImage(), 80, 70, 1);
		//add the test army to the array of armies
		testArmy2.updateMaxMovement(20);
		allArmies.add(testArmy2);
		
		ArrayList<Unit> testArmyUnits3 = new ArrayList<Unit>();
		testArmyUnits3.add(new Unit(400, 340, 5, 100, 10, "land", 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		testArmyUnits3.add(new Unit(330, 270, 5, 200, 20, "air", 500, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		testArmyUnits3.add(new Unit(650, 550, 5, 100, 10, "sea", 200, 10, new ImageIcon("TestUnitThree.jpg").getImage()));
		Army testArmy3 = new Army(testArmyUnits3, new ImageIcon("TestArmy.png").getImage(), 30, 30, 0);
		//add the test army to the array of armies
		testArmy3.updateMaxMovement(20);
		allArmies.add(testArmy3);
		
		//pass the list of armies to the view port 
		viewport.giveArmies(allArmies);
		//create a new information panel and pass the list of armies to it
		infoPanel = new OverworldInformationPanel(300, screenHeight, allArmies);

		//configure the layout manager
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		
		//add the viewport and information panel to the panel
		add(viewport, c);

		c.gridx = 1;
		add(infoPanel, c);

	}

	public class Paint extends JPanel{
		//set up the paint class
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			//paint the background of the panel
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, screenWidth, screenHeight);
		}
	}

	public void giveCardManager(CardManager newCM){
		//set the card manager to that passed to this class
		cM = newCM;
		//pass on the card manager to the information panel and the viewport
		infoPanel.giveCardManager(cM);
		viewport.giveCardManager(cM);
	}
	
	public void giveMapDisplay(MapDisplay mD){
		//pass on the map display to the viewport
		viewport.giveMapDisplay(mD);
		//pass the viewport back to the map display
		mD.giveViewport(viewport);
	}
	public void giveSettlementManager(SettlementManager newSM){
		//set the settlement manager to that used in the running program
		settlementM = newSM;
		settlementM.giveOverworldManager(this);
		//pass the settlement manager to the information panel
		infoPanel.giveSettlementManager(settlementM);
	}
	
	public void makeNewUnit(Unit newUnit, int settlementID, int player){
		//make the viewport add a other unit to the map
		viewport.addNewUnitToMap(newUnit, settlementID, player);
	}
	
	public void endCurrentTurn(){
		//increase the index of the current player
		currentPlayerTurn++;
		//check if all players have had their turn this run
		if(currentPlayerTurn == 2){
			//if they have then calculate any turn based results
			calculateTurnResults();
			//then set the player back to the first index
			currentPlayerTurn = 0;
		}
	}
	
	public void calculateTurnResults(){
		//construct any units created by the players
		//increase the funds of each player by the value of the settlements they control
		try{
			//connect to the database
			System.out.println("Attempting connection");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			System.out.println("Connected \n");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements join player on player.id = settlements.player");
			while(rs.next()){
				//loop through all rows in the table that were returned
				//create a new statement
				Statement stmt2 = con.createStatement();
				System.out.println("Income increase for player " + rs.getInt(10));
				System.out.println("Current funds for player " + rs.getInt(13));
				//for each settlement increase the funds of that player by the income of the settlement
				stmt2.executeUpdate("update player set funds = " + (rs.getInt(13) + rs.getInt(10)) + " where id = " + rs.getInt(9));
				System.out.println("Funds increased to " + rs.getInt(13));
			}
			System.out.println("All ran through");
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}

	public void mouseMoved(MouseEvent event){
		//get the location on the frame of the mouse
		int mouseX = event.getX();
		int mouseY = event.getY();

		//convert the distance into the frame into the distance into the map
		int distIntoViewportX = mouseX - viewport.getX();
		int distIntoViewportY = mouseY - viewport.getY();

		//check if the calculated location lies within the map view port
		if(distIntoViewportX > 0 && distIntoViewportX < viewport.getWidth()){
			if(distIntoViewportY > 0 && distIntoViewportY < viewport.getHeight()){
				//if the mouse in inside the viewport update the viewport selected item to whatever is being selected currently
				viewport.selectItemAtLocation(distIntoViewportX, distIntoViewportY);
				//check if an item was selected at that location
				if(viewport.isSelectedArmy() || viewport.isSelectedSettlement()){
					//if something is selected then update the information panel with the selected ID and the the type of item selected (true for settlement false for army)
					infoPanel.updateSelected(viewport.getSelectedID(), viewport.isSelectedSettlement());
				}
			}
		}
	}

	public void mouseClicked(MouseEvent event){
		//calculate the distance into the map the mouse has been clicked
		int distIntoViewportX = event.getX() - viewport.getX();
		int distIntoViewportY = event.getY() - viewport.getY();

		//check if the mouse click was inside the viewport
		if(distIntoViewportX > 0 && distIntoViewportX < viewport.getWidth()){
			if(distIntoViewportY > 0 && distIntoViewportY < viewport.getHeight()){
				//if the mouse was inside the viewport then pass the location of the click to the view manager to be handled
				viewport.clickedAtLocation(distIntoViewportX, distIntoViewportY);
			}
		}
	}
	public void mouseDragged(MouseEvent event){
		//replicate the mouse clicked method to allow for the case where the user was still slightly moving the mouse at the time of the click
		mouseClicked(event);
	}

	public void keyPressed(KeyEvent event){
		switch(event.getKeyCode()){
		case KeyEvent.VK_ESCAPE:
			//make the menu card appear when escape is pressed
			cM.showCard("OverCard", "MenuCard");
			break;
		case KeyEvent.VK_T:
			//end turn
			endCurrentTurn();
			break;
		case KeyEvent.VK_S:
			//add a keyboard shortcut to go to the last selected settlement manager
			cM.showCard("OverCard", "SettlementManager");
			break;
			// set up the directional arrow keys to control movement of the view of the world map up down left and right
		case KeyEvent.VK_RIGHT:
			viewport.changeXOffset(1);
			break;
		case KeyEvent.VK_LEFT:
			viewport.changeXOffset(-1);
			break;
		case KeyEvent.VK_UP:
			viewport.changeYOffset(-1);
			break;
		case KeyEvent.VK_DOWN:
			viewport.changeYOffset(1);
			break;
		default:
			break;
		}
	}
}
