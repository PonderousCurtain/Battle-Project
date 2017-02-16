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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class MapDisplay extends JPanel{

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

	ArrayList<Player> playerList;

	Timer runTimer;
	int normalRunTimerSpeed;
	Boolean gamePaused;

	JPanel mapDisplay;

	public MapDisplay(int screenWidth, int screenHeight){
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;

		allList = new ArrayList<Unit>();

		setPreferredSize(new Dimension(screenWidth - 300, screenHeight));

		blockages = new Obstruction[100][100];

		//unitList = new ArrayList<Unit>();
		selectedList = new ArrayList<Unit>();
		dragBox = new Rectangle();
		draggingBox = false;
		clickTimer = 0;

		playerList = new ArrayList<Player>();
		playerList.add(new Player(new ArrayList<Unit>(), "Player One"));
		playerList.add(new Player(new ArrayList<Unit>(), "Player Two"));

		sM = new SaveManager();
		blockages = sM.loadBlockages("StartMap.txt");
		pF = new PathFinder(blockages, screenWidth - 300);
		mM = new MovementManager(blockages);


		//set up the units for testing
		setUpUnits();


		Paint display = new Paint();
		display.setPreferredSize(new Dimension(screenWidth - 300, screenHeight));
		add(display);

		normalRunTimerSpeed = 20;
		runTimer = new Timer(normalRunTimerSpeed, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				for(Player nextPlayer: playerList){
					for(Unit nextUnit: nextPlayer.getControlledUnits()){
						if(!nextUnit.getArrived()){
							if(!nextUnit.getAttacking()){
								mM.moveStep(nextUnit);
							}
							if(!nextUnit.getAgro()){
								checkAggression(nextUnit, nextPlayer);
							}
						}								
						if(!nextUnit.getAttacking()){
							checkWithinAttackRange(nextUnit, nextPlayer);
						}


						for(Unit checkFriends: nextPlayer.getControlledUnits()){
							if(checkFriends == nextUnit || nextUnit.getAgro() || nextUnit.getAttacking()){
								continue;
							} else {
								if(checkFriends.getAttacking()){
									if(getDistance(checkFriends.getSparringPartner(), nextUnit) < nextUnit.getAgroRange()){
										ArrayList<Node> path = pF.pathFind(nextUnit.getX(), nextUnit.getY(), 20, checkFriends.getSparringPartner().getX(), checkFriends.getSparringPartner().getY(), nextUnit.getWidth(), nextUnit.getType());
										if(path.size() > 0 && path.size() < ((int)(nextUnit.getAgroRange()/nextUnit.getWidth()) * 1)){
											nextUnit.setTempPath(path);
											nextUnit.setAgro(true);
											nextUnit.setArrived(false);
											nextUnit.setAssisting(checkFriends);
										}
									}
								}
							}
						}


						if(nextUnit.getAttacking()){
							battleTurn(nextUnit);
						}
					}
				}
				repaint();
			}

		});

		runTimer.start();
		gamePaused = false;
	}

	public void setUpUnits(){

		playerList.get(0).getControlledUnits().clear();
		playerList.get(1).getControlledUnits().clear();
		//add units for testing purposes
		
		/*	NOTE TO SELF
		 * 
		 * JUST ADDED UNIT TABLE TO DATA BASE, INCORPORATE THIS AND ADD THE TIE BETWEEN THE OVERWORLD
		 * AND THE BATTLES THEN BEGIN WORK ON MAKING UNITS FROM THE SETTLEMENT MANAGER
		 * 
		 */

		playerList.get(0).addUnit(new Unit(500, 340, 5, 100, Color.BLACK, 10, 0, 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		playerList.get(0).addUnit(new Unit(430, 270, 5, 200, Color.ORANGE, 10, 1, 500, 10, new ImageIcon("TestUnitOne.jpg").getImage()));
		playerList.get(0).addUnit(new Unit(750, 550, 5, 100, Color.MAGENTA, 10, 2, 200, 10, new ImageIcon("TestUnitOne.jpg").getImage()));

		playerList.get(1).addUnit(new Unit(700, 340, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(715, 340, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(700, 355, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));
		playerList.get(1).addUnit(new Unit(715, 355, 5, 100, Color.RED, 10, 0, 100, 10, new ImageIcon("TestUnitTwo.jpg").getImage()));


		//fill the map library with the required size grids
		allList.clear();
		for(Player nextPlayer: playerList){
			allList.addAll(nextPlayer.getControlledUnits());
		}

		for(Unit nextUnit: allList){
			pF.setUpMap(nextUnit.getWidth());
		}

		mM.emptyUnitList();

		//give the units to the movement manager for collision detection
		mM.addUnitList(allList);

		selectedList.clear();
		if(informationPanel != null){
			informationPanel.updateSelectedUnits(selectedList);
		}
		repaint();
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth - 300, screenHeight);

			for(int x = 0; x < 100; x ++){
				for(int y = 0; y < 100; y ++){
					g.setColor(blockages[x][y].getColor());
					g.fill(blockages[x][y].getRect());
				}
			}

			for(Player nextPlayer: playerList){
				for(Unit nextUnit: nextPlayer.getControlledUnits()){

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


					g.setColor(nextUnit.getColor());
					g.fill(nextUnit.getRect());

					if(nextUnit.getSelected()){
						g.setColor(nextUnit.getSelectedColor());
						g.draw(nextUnit.getRect());
					}

				}
			}

			if(draggingBox){
				g.setColor(Color.WHITE);
				g.draw(dragBox);
			}

			if(clickTimer > 0){
				g.setColor(Color.RED);
				clickTimer --;
			}
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
		return (int) Math.sqrt(Math.pow((A.getX() - B.getX()), 2) + Math.pow((A.getY() - B.getY()), 2));
	}
	public InfoPanel getInformationPanel(){
		return informationPanel;
	}
	public void giveInfoPanel(InfoPanel iP){
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
			runTimer.setDelay((int) (normalRunTimerSpeed / timeCommand));
		}
	}
	public void updateAgroRangeOfSelected(int agroRange){
		for(Unit nextUnit: selectedList){
			nextUnit.setAgroRange(agroRange);
		}
		repaint();
	}

	public void battleTurn(Unit attackingUnit){
		Boolean targetDead = attackingUnit.getSparringPartner().deadFromDamage(attackingUnit.getAttack());
		if(targetDead){
			for(Player nextPlayer: playerList){
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					if(nextUnit == attackingUnit){
						continue;
					}
					if(nextUnit == attackingUnit.getSparringPartner()){
						nextPlayer.getControlledUnits().remove(attackingUnit.getSparringPartner());
						break;
					} else if(nextUnit.getAssisting() == attackingUnit){
						nextUnit.emptyTempPath();
						if(nextUnit.getPath().size() == 0){
							nextUnit.setArrived(true);
						}
						nextUnit.setAttacking(false);
						nextUnit.setAgro(false);
					}
				}
			}

			mM.removeUnit(attackingUnit.getSparringPartner());
			allList.remove(attackingUnit.getSparringPartner());
			attackingUnit.emptyTempPath();
			attackingUnit.setAttacking(false);
			attackingUnit.setAgro(false);

			attackingUnit.emptyPath();

			if(selectedList.contains(attackingUnit.getSparringPartner())){
				selectedList.remove(attackingUnit.getSparringPartner());
			}

			pF.clearScanned();
			ArrayList<Node> path = pF.pathFind(attackingUnit.getX(), attackingUnit.getY(), 20, attackingUnit.getTargetX(), attackingUnit.getTargetY(), attackingUnit.getWidth(), attackingUnit.getType());
			if(path.size() > 0){
				attackingUnit.setPath(path);
				attackingUnit.setArrived(false);
			} else {
				attackingUnit.setArrived(true);
			}

		}

		informationPanel.updateSelectedUnits(selectedList);
	}

	public void checkWithinAttackRange(Unit checkUnit, Player checkPlayer){
		for(Player nextPlayer: playerList){
			if(nextPlayer == checkPlayer){
				continue;
			} else{
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					if(getDistance(checkUnit, nextUnit) < 20){
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
		Boolean foundTarget = false;
		for(Player nextPlayer: playerList){
			if(nextPlayer == checkPlayer){
				continue;
			} else{
				for(Unit nextUnit: nextPlayer.getControlledUnits()){
					if(getDistance(checkUnit, nextUnit) < checkUnit.getAgroRange()){
						foundTarget = true;
						ArrayList<Node> path = pF.pathFind(checkUnit.getX(), checkUnit.getY(), 20, nextUnit.getX(), nextUnit.getY(), checkUnit.getWidth(), checkUnit.getType());
						if(path.size() > 0 && path.size() < ((int)(checkUnit.getAgroRange()/checkUnit.getWidth()) * 1)){
							checkUnit.setTempPath(path);
							checkUnit.setAgro(true);
						}
						break;
					}
				}
			}
			if(foundTarget){
				break;
			}
		}
	}

	public void updateDragBox(int mouseX, int mouseY, int initialMouseX, int initialMouseY){
		dragBox.setBounds(Math.min(mouseX,  initialMouseX), Math.min(mouseY,  initialMouseY), Math.abs(mouseX - initialMouseX), Math.abs(mouseY - initialMouseY));
	}

	public void updateSelected(){
		for(Player nextPlayer: playerList){
			for(Unit nextUnit: nextPlayer.getControlledUnits()){
				if(!selectedList.contains(nextUnit) && dragBox.intersects(nextUnit.getRect())){
					nextUnit.setSelected(true);
					selectedList.add(nextUnit);
				} else if(selectedList.contains(nextUnit) && dragBox.intersects(nextUnit.getRect())){
					nextUnit.setSelected(false);
					selectedList.remove(nextUnit);
				}
			}
		}
		informationPanel.updateSelectedUnits(selectedList);
		repaint();
	}
	public void clearSelected(){
		for(Unit nextUnit: selectedList){
			nextUnit.setSelected(false);
		}
		selectedList.clear();
		informationPanel.updateSelectedUnits(selectedList);
	}

	//mouse controll
	public void mouseDraggedInput(MouseEvent event) {
		if(!SwingUtilities.isRightMouseButton(event)){
			if(!draggingBox){
				draggingBox = true;
				savedMouseX = event.getX();
				savedMouseY = event.getY();
			}
			updateDragBox(event.getX(), event.getY(), savedMouseX, savedMouseY);
		} else {
			clickTimer = 30;
			lastMouseX = event.getX();
			lastMouseY = event.getY();
			for(Unit next: selectedList){
				next.setTarget(event.getX(), event.getY());
				pF.clearScanned();
				ArrayList<Node> path = pF.pathFind(next.getX(), next.getY(), 20, event.getX(), event.getY(), next.getWidth(), next.getType());
				if(path.size() > 0){
					next.setPath(path);
					next.setArrived(false);
				}
			}
			//clearSelected();
		}
		repaint();
	}

	public void mouseClickedInput(MouseEvent event) {
		if(event.getButton() == 3){
			clickTimer = 30;
			lastMouseX = event.getX();
			lastMouseY = event.getY();
			for(Unit next: selectedList){

				next.setTarget(event.getX(), event.getY());
				pF.clearScanned();
				ArrayList<Node> path = pF.pathFind(next.getX(), next.getY(), 20, event.getX(), event.getY(), next.getWidth(), next.getType());
				if(path.size() > 0){
					next.setPath(path);
					next.setArrived(false);
				}
			}
			//clearSelected();
		}
		repaint();

	}
	public void mouseReleasedInput(MouseEvent event) {
		if(draggingBox){
			updateSelected();
			draggingBox = false;
		}
	}

	public void keyPressedInput(KeyEvent event) {
		switch(event.getKeyCode()){
		case KeyEvent.VK_Q:
			clearSelected();
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(1);
			break;
		default:
			break;
		}
	}
}
