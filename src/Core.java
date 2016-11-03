
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



public class Core implements MouseListener, MouseMotionListener, KeyListener{
	public static void main(String [] args){
		new Core();
	}

	JFrame frame;
	//ArrayList<Unit> unitList;
	ArrayList<Unit> selectedList;
	ArrayList<Unit> allList;
	Obstruction[][] blockages;

	int screenHeight = 1000;
	int screenWidth = 1200;
	JPanel mapDisplay;
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


	public Core(){
		frame = new JFrame();
		frame.setSize(new Dimension(screenWidth, screenHeight));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addKeyListener(this);
		frame.setUndecorated(true);
		
		System.out.println("Hello");

		allList = new ArrayList<Unit>();

		mapDisplay = new JPanel();
		mapDisplay.setPreferredSize(new Dimension(screenWidth - 200, screenHeight));
		informationPanel = new InfoPanel(200, screenHeight);
		informationPanel.setPreferredSize(new Dimension(200, screenHeight));

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
		pF = new PathFinder(blockages, screenWidth - 200);
		mM = new MovementManager(blockages);


		//add units for testing purposes

		playerList.get(0).addUnit(new Unit(500, 340, 5, 100, Color.BLACK, 10, 0, 200, 10));
		playerList.get(0).addUnit(new Unit(430, 270, 5, 200, Color.ORANGE, 10, 1, 500, 10));
		playerList.get(0).addUnit(new Unit(750, 550, 5, 100, Color.MAGENTA, 10, 2, 200, 10));

		playerList.get(1).addUnit(new Unit(700, 340, 5, 100, Color.RED, 10, 0, 100, 10));
		playerList.get(1).addUnit(new Unit(715, 340, 5, 100, Color.RED, 10, 0, 100, 10));
		playerList.get(1).addUnit(new Unit(700, 355, 5, 100, Color.RED, 10, 0, 100, 10));
		playerList.get(1).addUnit(new Unit(715, 355, 5, 100, Color.RED, 10, 0, 100, 10));


		//fill the map library with the required size grids
		for(Player nextPlayer: playerList){
			allList.addAll(nextPlayer.getControlledUnits());
		}

		for(Unit nextUnit: allList){
			pF.setUpMap(nextUnit.getWidth());
		}

		//give the units to the movement manager for collision detection
		mM.addUnitList(allList);

		Paint display = new Paint();
		display.setPreferredSize(new Dimension(screenWidth - 200, screenHeight));
		mapDisplay.add(display);

		frame.add(BorderLayout.WEST, mapDisplay);
		frame.add(BorderLayout.EAST, informationPanel);

		frame.setVisible(true);

		Timer runTimer = new Timer(20, new ActionListener(){

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
				frame.repaint();
			}

		});

		runTimer.start();

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

	public int getDistance(Unit A, Unit B){
		return (int) Math.sqrt(Math.pow((A.getX() - B.getX()), 2) + Math.pow((A.getY() - B.getY()), 2));
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth - 200, screenHeight);

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

			frame.repaint();
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
		frame.repaint();
	}
	public void clearSelected(){
		for(Unit nextUnit: selectedList){
			nextUnit.setSelected(false);
		}
		selectedList.clear();
		informationPanel.updateSelectedUnits(selectedList);
	}




	@Override
	public void mouseDragged(MouseEvent event) {
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
		frame.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}

	@Override
	public void mouseClicked(MouseEvent event) {
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
		frame.repaint();

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
		if(draggingBox){
			updateSelected();
			draggingBox = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
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

	@Override
	public void keyReleased(KeyEvent event) {

	}

	@Override
	public void keyTyped(KeyEvent event) {

	}
}