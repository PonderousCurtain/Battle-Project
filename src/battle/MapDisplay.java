package battle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import overworld.Army;
import overworld.OverworldViewManager;
import overworld.Unit;
import utilities.CardManager;
import utilities.Obstruction;
import utilities.SaveManager;


public class MapDisplay extends JPanel{

	//Declare the main variables to be used throughout the class

	int screenWidth;
	int screenHeight;

	ArrayList<Unit> selectedList;
	ArrayList<Unit> allList;
	Obstruction[][] blockages;
	//ArrayList<Unit> unitList;

	InfoPanel informationPanel;
	Rectangle dragBox;
	Boolean draggingBox;
	int clickTimer;

	int savedMouseX;
	int savedMouseY;

	int lastMouseX;
	int lastMouseY;

	PathFinder pF;
	MovementManager mM;
	SaveManager sM;
	OverworldViewManager viewport;

	ArrayList<Player> playerList;

	Timer runTimer;
	int normalRunTimerSpeed;
	Boolean gamePaused;

	int aIDelayTime;

	JPanel mapDisplay;
	CardManager cM;

	Boolean userWon;

	public MapDisplay(int screenWidth, int screenHeight){
		//initialise the main variables
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;

		allList = new ArrayList<Unit>();

		//set the size of the panel
		setPreferredSize(new Dimension(screenWidth - 300, screenHeight));

		blockages = new Obstruction[70][70];

		//unitList = new ArrayList<Unit>();
		selectedList = new ArrayList<Unit>();
		dragBox = new Rectangle();
		draggingBox = false;
		clickTimer = 0;

		//set the default to the player hasn't won
		userWon = false;

		//initialise the player list
		playerList = new ArrayList<Player>();

		//set up the various managers needed by the class
		sM = new SaveManager();
		blockages = sM.loadBlockages("StartMap.txt");
		pF = new PathFinder(blockages, screenWidth - 300);
		mM = new MovementManager(blockages);


		//set up the units for testing
		//setUpUnits();


		Paint display = new Paint();
		display.setPreferredSize(new Dimension(screenWidth - 300, screenHeight));
		add(display);

		//set up the timer to be used for animation
		normalRunTimerSpeed = 20;
		//reset the AI delay count
		aIDelayTime = 0;
		runTimer = new Timer(normalRunTimerSpeed, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//loop through all the players in the game
				for(Player nextPlayer: playerList){
					//for each player loop through the units they control
					for(Unit nextUnit: nextPlayer.getControlledUnits()){
						//check if the unit is in transit
						if(!nextUnit.getArrived()){
							//if the unit is in transit then check if it is attacking something
							if(!nextUnit.getAttacking()){
								//if the unit is not attacking something then move it another step
								mM.moveStep(nextUnit);
							}
							//check if the unit is currently aggressive towards something
							if(!nextUnit.getAgro()){
								//if the unit is not aggressive towards something then check its aggression range
								checkAggression(nextUnit, nextPlayer);
							}
						}
						//check if the unit is attacking another unit
						if(!nextUnit.getAttacking()){
							//if the unit is not attacking something then check if there is something in its attack range
							checkWithinAttackRange(nextUnit, nextPlayer);
						}

						//loop through the units of the player again
						for(Unit checkFriends: nextPlayer.getControlledUnits()){
							//check that the unit being checked is not the current unit, the current unit is not aggressive towards something and is not attacking something
							if(checkFriends == nextUnit || nextUnit.getAgro() || nextUnit.getAttacking()){
								continue;
							} else {
								//if all the above conditions are met then check if the friendly unit is attacking something
								if(checkFriends.getAttacking()){
									//if it is attacking something then check the distance between the current unit and the unit attacking the friendly unit
									if(getDistance(checkFriends.getSparringPartner(), nextUnit) < nextUnit.getAgroRange()){
										//if the distance is smaller than the aggression range of the current unit then generate a path from the current unit to the unit attacking the friendly unit
										ArrayList<Node> path = pF.pathFind(nextUnit.getX(), nextUnit.getY(), 20, checkFriends.getSparringPartner().getX(), checkFriends.getSparringPartner().getY(), nextUnit.getWidth(), nextUnit.getType());
										//check if the path exists and if it is acceptably small
										if(path.size() > 0 && path.size() < ((int)(nextUnit.getAgroRange()/nextUnit.getWidth()) * 1)){
											//if the path is an allowed length then set it as a temporary path for the current unit
											nextUnit.setTempPath(path);
											//set the current unit as being aggressive and having not arrived at the enemy
											nextUnit.setAgro(true);
											nextUnit.setArrived(false);
											//set the current unit as assisting the friendly unit
											nextUnit.setAssisting(checkFriends);
										}
									}
								}
							}
						}

						//check if the current unit is attacking something
						if(nextUnit.getAttacking()){
							//if the unit is attacking something then carry out the next attack turn
							battleTurn(nextUnit);
						}
					}
				}

				//reset whether the user has won
				userWon = false;
				//check if any player has now lost all their units
				//loop through all players
				for(int i = 0; i < playerList.size(); i++){
					//check the size of the list of controlled units
					if(playerList.get(i).getControlledUnits().size() == 0){
						//remove that player from the fight
						if(i == 1){
							userWon = true;
						}
						playerList.remove(i);
						//lower i to allow for its index being removed from the list
						i--;
					}
				}

				//check if the number of armies is now 1
				if(playerList.size() == 1){
					//if only one army remains then end the battle
					endBattle(false);
				}
				//handle the AI delay timer
				//increase the delay count
				aIDelayTime ++;
				//check if it has reached the time for another AI move
				if(aIDelayTime == 50){
					//reset the AI delay
					aIDelayTime = 0;
					//run the AI movement methods
					runAIOpponents();
				}
				repaint();
			}

		});


		//set the game to paused
		gamePaused =true;
	}

	public void giveCardManger(CardManager newCM){
		//get the card manage used throughout the code
		cM = newCM;
	}

	public void runAIOpponents(){
		//loop through all players
		for(int count = 0; count < playerList.size(); count ++){
			//check if the player is the user
			if(count == 0){
				continue;
			}
			//otherwise loop through the units controlled by the other player
			for(Unit nextUnit: playerList.get(count).getControlledUnits()){
				//set a default target value and default target unit
				int targetValue = 0;
				//create a target unit and initialise it
				Unit targetUnit = null;
				//loop through enemy player units
				for(Unit enemyUnit: playerList.get(0).getControlledUnits()){
					//calculate the target value for each enemy unit and replace the default value with the highest
					int holder = calculateTargetValue(nextUnit, enemyUnit);
					if(holder > targetValue){
						targetValue = holder;
						//update the target unit
						targetUnit = enemyUnit;
					}
				}

				//set the target of the checked unit to the location of the target unit
				nextUnit.setTarget(targetUnit.getX(), targetUnit.getY());
				//clear the path scanned locations
				pF.clearScanned();
				//create a path to the current position of the target unit
				ArrayList<Node> path = pF.pathFind(nextUnit.getX(), nextUnit.getY(), 20, targetUnit.getX(), targetUnit.getY(), nextUnit.getWidth(), nextUnit.getType());
				//check if the path exists
				if(path.size() > 0){
					//if it does then set it as the path of that unit and set that unit as being in transit
					nextUnit.setPath(path);
					nextUnit.setArrived(false);

				}
			}
		}
	}

	public int calculateTargetValue(Unit attackingUnit, Unit enemyUnit){
		//set a default threat value
		int threatValue = 0;
		//calculate the distance between the two units
		int distance = getDistance(attackingUnit, enemyUnit);
		//calculate a threat value based on the distance and the statistics of the enemy unit
		threatValue = (int) ((distance / 100) + (enemyUnit.getMaxHealth() / 100) + (enemyUnit.getAttack() / 10) + (enemyUnit.getSpeed() / 5));
		//return the threat value calculated
		return threatValue;
	}

	public void setUpNewBattle(Army armyOne, Army armyTwo){
		//clear the player list and add two new players to take part in the battle
		playerList.clear();
		playerList.add(new Player(new ArrayList<Unit>(), "Player One"));
		playerList.add(new Player(new ArrayList<Unit>(), "Player Two"));

		//add the units from each army to their respective players
		playerList.get(0).updateUnits(armyOne.getUnits());
		playerList.get(1).updateUnits(armyTwo.getUnits());

		//clear the list of all units in the battle
		allList.clear();
		//loop through all players in the battle
		for(Player nextPlayer: playerList){
			//add all the units controlled by each player to the list of all units
			allList.addAll(nextPlayer.getControlledUnits());
		}

		//loop through the list of all units
		for(Unit nextUnit: allList){
			//pass each unit in turn to the path finder to set up path maps for all unit sizes in the battle
			pF.setUpMap(nextUnit.getWidth());
		}

		//empty the unit list of the movement manager
		mM.emptyUnitList();

		//give the units to the movement manager for collision detection
		mM.addUnitList(allList);

		//clear the selected unit list
		selectedList.clear();
		//check if the information panel exists (has been already initialised and implemented)
		if(informationPanel != null){
			//update the information panel to reflect that no units are selected
			informationPanel.updateSelectedUnits(selectedList);
		}
		//if the game is not paused then set the game to paused
		if(!gamePaused){
			informationPanel.pauseGame();
		}
		repaint();
	}

	/* METHOD FOR HARD CODING UNITS INTO TEH BATTLE FOR TESTING
	public void setUpUnits(){
		//clear the controlled units of the players
		playerList.get(0).getControlledUnits().clear();
		playerList.get(1).getControlledUnits().clear();
		//add units for testing purposes

		//add a set of units to both players for testing
		playerList.get(0).addUnit(new Unit(500, 340, 5, 100, Color.BLACK, 10, 0, 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		playerList.get(0).addUnit(new Unit(430, 270, 5, 200, Color.ORANGE, 10, 1, 500, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		playerList.get(0).addUnit(new Unit(750, 550, 5, 100, Color.MAGENTA, 10, 2, 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));

		playerList.get(1).addUnit(new Unit(700, 340, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(715, 340, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(700, 355, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(715, 355, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));


		//clear the list of all units in the battle
		allList.clear();
		//loop through all players in the battle
		for(Player nextPlayer: playerList){
			//add all the units controlled by each player to the list of all units
			allList.addAll(nextPlayer.getControlledUnits());
		}

		//loop through the list of all units
		for(Unit nextUnit: allList){
			//pass each unit in turn to the path finder to set up path maps for all unit sizes in the battle
			pF.setUpMap(nextUnit.getWidth());
		}

		//empty the unit list of the movement manager
		mM.emptyUnitList();

		//give the units to the movement manager for collision detection
		mM.addUnitList(allList);

		//clear the selected unit list
		selectedList.clear();
		//check if the information panel exists (has been already initialised and implemented)
		if(informationPanel != null){
			//update the information panel to reflect that no units are selected
			informationPanel.updateSelectedUnits(selectedList);
		}
		repaint();
	}
	 */

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			//paint the background
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth - 300, screenHeight);

			//loop through the map grid
			for(int x = 0; x < 70; x ++){
				for(int y = 0; y < 70; y ++){
					//paint in each square of the map grid
					g.setColor(blockages[x][y].getColor());
					g.fill(blockages[x][y].getRect());
				}
			}

			//loop through each player
			for(Player nextPlayer: playerList){
				//loop through the units controlled by that player
				for(Unit nextUnit: nextPlayer.getControlledUnits()){

					/* THIS CODE IS FOR TROUBLESHOOTING PURPOSES AND CAN BE USED TO PAINT THE PATHS OF UNITS ONTO THE SCREEN
					if(!nextUnit.getArrived()){
						g.setColor(nextUnit.getSelectedColor());
						if(!nextUnit.getAgro()){
							for(Node nextNode: nextUnit.getPath()){
								//g.fill(nextNode.getRect());
							}
						} else {
							for(Node nextNode: nextUnit.getTempPath()){
								//g.fill(nextNode.getRect());
							}
						}
					}
					 */

					//paint the unit with it's respective colour
					g.drawImage(nextUnit.getImage(), nextUnit.getRect().x, nextUnit.getRect().y, nextUnit.getRect().width, nextUnit.getRect().height, null);

					//check if the unit is selected
					if(nextUnit.getSelected()){
						//if it is selected highlight it with its correct highlight colour
						g.setColor(nextUnit.getSelectedColor());
						g.draw(nextUnit.getRect());
					}

				}
			}

			//check if the user is dragging a selection box
			if(draggingBox){
				//paint the selection box
				g.setColor(Color.WHITE);
				g.draw(dragBox);
			}

			//check if the click timer is a positive value
			if(clickTimer > 0){
				//set the colour to red
				g.setColor(Color.RED);
				clickTimer --;
			}
			//check the click timer and depending on where its value lies in the bands draw a circle to indicate the click location
			if(clickTimer > 20){
				g.drawOval(lastMouseX - 30, (int)lastMouseY - 30, 60, 60);
			} else if(clickTimer > 10){
				g.drawOval(lastMouseX - 20, (int)lastMouseY - 20, 40, 40);
			} else if (clickTimer > 0){
				g.drawOval(lastMouseX - 10, (int)lastMouseY - 10, 20, 20);

			}

			repaint();
		}
	}

	public int getDistance(Unit A, Unit B){
		//calculate the direct distance between two passed units
		return (int) Math.sqrt(Math.pow((A.getX() - B.getX()), 2) + Math.pow((A.getY() - B.getY()), 2));
	}

	public InfoPanel getInformationPanel(){
		//return the information panel being used
		return informationPanel;
	}

	public void giveInfoPanel(InfoPanel iP){
		//be passed the information panel to use
		informationPanel = iP;
	}

	public void giveUpdateTimeCommand(float timeCommand){
		if(timeCommand == 0){
			//pause the game or play the game if paused
			if(gamePaused){
				runTimer.start();
			} else {
				runTimer.stop();
			}
			gamePaused = !gamePaused;
		} else {
			//alter the game speed depending on the value passed
			runTimer.setDelay((int) (normalRunTimerSpeed / timeCommand));
		}
	}

	public void updateAgroRangeOfSelected(int agroRange){
		//update the aggression range of the selected units to the values passed
		for(Unit nextUnit: selectedList){
			nextUnit.setAgroRange(agroRange);
		}
		repaint();
	}

	public Boolean battleTurn(Unit attackingUnit){

		//check if the target unit has died from the damage dealt to it
		Boolean targetDead = attackingUnit.getSparringPartner().deadFromDamage(attackingUnit.getAttack());
		if(targetDead){
			//if the target is dead the loop through the players
			for(Player nextPlayer: playerList){
				//loop through the units each player controls
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					//check if the unit is the attacking unit
					if(nextUnit == attackingUnit){
						//if it i the same unit then ignore it and continue
						continue;
					}
					//check if the unit is the one that was just killed
					if(nextUnit == attackingUnit.getSparringPartner()){
						//remove the unit from the list of controlled units owned by that player
						nextPlayer.getControlledUnits().remove(attackingUnit.getSparringPartner());
						break;
						//otherwise if the unit was assisting the attacking unit
					} else if(nextUnit.getAssisting() == attackingUnit){
						//empty the path to the now dead unit
						nextUnit.emptyTempPath();
						//check if the unit was on the way to another destination
						if(nextUnit.getPath().size() == 0){
							//if it was not then set it to have arrived
							nextUnit.setArrived(true);
						}
						//set the unit to no longer engaging an enemy or being aggressive
						nextUnit.setAttacking(false);
						nextUnit.setAgro(false);
					}
				}
			}

			//remove the dead unit from the movement manager so it cannot be collided with
			mM.removeUnit(attackingUnit.getSparringPartner());

			//remove the dead unit from the list of all units in the battle
			allList.remove(attackingUnit.getSparringPartner());
			//clear the temporary path, aggression and attacking booleans to reflect that its target is dead
			attackingUnit.emptyTempPath();
			attackingUnit.setAttacking(false);
			attackingUnit.setAgro(false);

			//empty the path of the attacker
			attackingUnit.emptyPath();

			//check if the dead unit was selected at the time
			if(selectedList.contains(attackingUnit.getSparringPartner())){
				//if it was then remove it from the selected list
				selectedList.remove(attackingUnit.getSparringPartner());
			}

			//clear the scanner locations of the path finder
			pF.clearScanned();
			//create a new path from the attacking unit to its previous target location
			ArrayList<Node> path = pF.pathFind(attackingUnit.getX(), attackingUnit.getY(), 20, attackingUnit.getTargetX(), attackingUnit.getTargetY(), attackingUnit.getWidth(), attackingUnit.getType());
			//check if the path exists
			if(path.size() > 0){
				//if it does then set that as the new path and set the unit as not having arrived
				attackingUnit.setPath(path);
				attackingUnit.setArrived(false);
			} else {
				//if the path does not exist then set the attacker as having arrived
				attackingUnit.setArrived(true);
			}

		}

		//update the selected units
		informationPanel.updateSelectedUnits(selectedList);
		return targetDead;
	}

	public void giveViewport(OverworldViewManager newViewport){
		viewport = newViewport;
	}

	public void endBattle(Boolean userRetreated){
		//make sure that all the units in the remaining army are no longer selected and are completely reset in terms of aggression, attacking and are not in transit
		for(Unit nextUnit : playerList.get(0).getControlledUnits()){
			//remove all paths
			nextUnit.emptyPath();
			nextUnit.emptyTempPath();
			//remove aggression and attacking
			nextUnit.setAgro(false);
			nextUnit.setAttacking(false);
			//set it to have arrived
			nextUnit.setArrived(true);
			//set the unit to not be selected
			nextUnit.setSelected(false);
			repaint();
		}
		//create and show a JOptionpane to indicate if it was a victory or defeat for the player controlled army
		if(userRetreated){
			//if the user retreated then display retreat
			JOptionPane.showMessageDialog(this, "Retreated");
		}
		else if(userWon){
			//if the user won then display victory
			JOptionPane.showMessageDialog(this, "Victory");
		} else {
			//if the user lost then display defeat
			JOptionPane.showMessageDialog(this, "Defeat");
		}
		userRetreated = false;
		//run the check method on the over world to update army information
		viewport.updateArmies();
		//change the frame focus back to the over world view
		cM.showCard("BattleCard", "OverCard");
		//stop the battle timer
		runTimer.stop();

	}

	public void checkWithinAttackRange(Unit checkUnit, Player checkPlayer){
		//loop through the players
		for(Player nextPlayer: playerList){
			//if the player is the same player that owns the unit being checked then ignore it
			if(nextPlayer == checkPlayer){
				continue;
			} else{
				//loop through all the units controlled by opposing players
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					//check if the distance to an enemy unit is less that 20
					if(getDistance(checkUnit, nextUnit) < 20 + checkUnit.getWidth()){
						//if the unit is within 20 units of an enemy unit then set it as attacking that unit and set that unit as attacking the unit being checked
						checkUnit.setAttacking(true);
						checkUnit.setSparringPartner(nextUnit);
						nextUnit.setAttacking(true);
						nextUnit.setSparringPartner(checkUnit);
						break;
					}
				}
			}
		}
	}

	public void checkAggression(Unit checkUnit, Player checkPlayer){
		//set found target to false
		Boolean foundTarget = false;
		//loop through all the players
		for(Player nextPlayer: playerList){
			//if the player is the one that owns the current unit then ignore it
			if(nextPlayer == checkPlayer){
				continue;
			} else{
				//loop through all opposing units
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					//check if the distance to the enemy unit is lower than the aggression range of the unit
					if(getDistance(checkUnit, nextUnit) < checkUnit.getAgroRange()){
						//if it is then set that a target has been found
						foundTarget = true;
						//generate a path to the found target
						ArrayList<Node> path = pF.pathFind(checkUnit.getX(), checkUnit.getY(), 20, nextUnit.getX(), nextUnit.getY(), checkUnit.getWidth(), checkUnit.getType());
						//check if the path exists and is an acceptable length
						if(path.size() > 0 && path.size() < ((int)(checkUnit.getAgroRange()/checkUnit.getWidth()) * 1)){
							//if the path is ok then set the unit as being aggressive and path it towards the target
							checkUnit.setTempPath(path);
							checkUnit.setAgro(true);
						}
						break;
					}
				}
			}
			//if a target has been found then break the loop
			if(foundTarget){
				break;
			}
		}
	}

	public void updateDragBox(int mouseX, int mouseY, int initialMouseX, int initialMouseY){
		//update the bounds of the drag box to the new x and y locations of the mouse
		dragBox.setBounds(Math.min(mouseX,  initialMouseX), Math.min(mouseY,  initialMouseY), Math.abs(mouseX - initialMouseX), Math.abs(mouseY - initialMouseY));
	}

	public void updateSelected(){
		//loop through all the players
		for(Player nextPlayer: playerList){
			//loop through all units controlled by the players
			for(Unit nextUnit: nextPlayer.getControlledUnits()){
				//check that that unit is not already selected and if it is within the selection box
				if(!selectedList.contains(nextUnit) && dragBox.intersects(nextUnit.getRect())){
					//set the unit has being selected and add it to the selected list
					nextUnit.setSelected(true);
					selectedList.add(nextUnit);
					//otherwise if the unit is in the selection box and already selected
				} else if(selectedList.contains(nextUnit) && dragBox.intersects(nextUnit.getRect())){
					//reselect the unit and remove it from the list of selected units
					nextUnit.setSelected(false);
					selectedList.remove(nextUnit);
				}
			}
		}
		//update the selected units on the information panel
		informationPanel.updateSelectedUnits(selectedList);
		repaint();
	}

	public void clearSelected(){
		//loop through all the units in the selected list
		for(Unit nextUnit: selectedList){
			//set the unit as no longer being selected
			nextUnit.setSelected(false);
		}
		//empty the list of selected units
		selectedList.clear();
		//update the information panel
		informationPanel.updateSelectedUnits(selectedList);
	}

	public void mouseDraggedInput(MouseEvent event) {
		//check if the click was not a right mouse click
		if(!SwingUtilities.isRightMouseButton(event)){
			//check if the player was dragging a box
			if(!draggingBox){
				//if they were not then set they now are and save the start mouse location of the box
				draggingBox = true;
				savedMouseX = event.getX();
				savedMouseY = event.getY();
			}
			//update the drag box with the current mouse location and saved location
			updateDragBox(event.getX(), event.getY(), savedMouseX, savedMouseY);
		} else {
			//if the mouse was a right click then set the click timer to a value and set the mouse click location to the current mouse location
			clickTimer = 30;
			lastMouseX = event.getX();
			lastMouseY = event.getY();
			//loop through the selected units
			for(Unit next: selectedList){
				//set the target to the mouse location
				next.setTarget(event.getX(), event.getY());
				//clear the path scanned locations
				pF.clearScanned();
				//generate a new path to the target location for each unit
				ArrayList<Node> path = pF.pathFind(next.getX(), next.getY(), 20, event.getX(), event.getY(), next.getWidth(), next.getType());
				//check if the path exists
				if(path.size() > 0){
					//if it does then set it as the path of that unit and set that unit as being in transit
					next.setPath(path);
					next.setArrived(false);
				}
			}
			//clearSelected();
		}
		repaint();
	}

	public void mouseClickedInput(MouseEvent event) {
		//check if the button click was a right click
		if(event.getButton() == 3){
			//if it was then set a value for the click timer and mouse location of the click
			clickTimer = 30;
			lastMouseX = event.getX();
			lastMouseY = event.getY();

			//loop through the selected units
			for(Unit next: selectedList){
				//set the target of the selected unit to the click location
				next.setTarget(event.getX(), event.getY());
				//clear the path finder scanned locations
				pF.clearScanned();
				//generate a new path to the new click location
				ArrayList<Node> path = pF.pathFind(next.getX(), next.getY(), 20, event.getX(), event.getY(), next.getWidth(), next.getType());
				//check if the path exists
				if(path.size() > 0){
					//if the paths exists then set it as the new path for the unit and set the unit as being in transit
					next.setPath(path);
					next.setArrived(false);
				}
			}
		}
		repaint();

	}

	public void mouseReleasedInput(MouseEvent event) {
		//check it the user was dragging a box
		if(draggingBox){
			//if the user was dragging a box then update the selected units and set that the user is not longer dragging a box
			updateSelected();
			draggingBox = false;
		}
	}

	public void keyPressedInput(KeyEvent event) {
		switch(event.getKeyCode()){
		case KeyEvent.VK_Q:
			//if Q is pressed clear any selected units
			clearSelected();
			break;
		case KeyEvent.VK_ESCAPE:
			//if escape is pressed then close the program
			System.exit(1);
			break;
		default:
			break;
		}
	}
}
