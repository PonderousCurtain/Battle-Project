import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;


public class SettlementManager extends JPanel{
	int screenWidth;
	int screenHeight;
	Settlement currentSettlement;
	ArrayList<Building> currentBuildingList;
	CardManager cM;
	ArrayList<Settlement> settlementList;
	int displacement;
	int maxDisplacement;

	JButton buildingOne;
	JButton buildingTwo;
	JButton buildingThree;
	JButton buildingFour;
	JButton buildingFive;
	Image defaultImage;

	Image currentImage;
	ArrayList<int[]> currentBuildingSize;
	Color currentBuildingColor;

	int mouseX;
	int mouseY;

	int mouseGridX;
	int mouseGridY;

	int gapWidth;
	SettlementGridSpace[][] settlementGrid;

	Boolean clearToPlace;

	public SettlementManager(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		clearToPlace = false;

		settlementList = new ArrayList<Settlement>();
		settlementList.add(new Settlement(10, 10, 10));

		currentSettlement = settlementList.get(0);
		currentBuildingList = currentSettlement.getBuildings();
		defaultImage = new ImageIcon("default.jpg").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;

		JButton returnButton = new JButton("Return");
		returnButton.setFocusable(false);
		returnButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				cM.showCard("SettlementManager", "OverCard");
			}

		});

		JPanel informationPanel = new JPanel();
		informationPanel.setPreferredSize(new Dimension(screenWidth, 200));
		informationPanel.setLayout(new GridBagLayout());
		informationPanel.add(returnButton, c);

		GridPaint display = new GridPaint();
		display.setPreferredSize(new Dimension(screenWidth - 500, screenHeight - 200));

		JPanel buildingSelection = new JPanel();
		buildingSelection.setLayout(new GridLayout(0, 1));
		buildingSelection.setPreferredSize(new Dimension(500, screenHeight - 200));

		displacement = 0;
		maxDisplacement = currentBuildingList.size() - 5;

		JButton scrollUpButton = new JButton("Up");
		scrollUpButton.setFocusable(false);
		scrollUpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(displacement > 0){
					displacement--;
					updateButtonDisplay();
				}
			}

		});

		buildingOne = new JButton("");
		buildingOne.setFocusable(false);
		buildingOne.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 0){
					currentImage = currentBuildingList.get(0 + displacement).getImage();
					currentBuildingSize = currentBuildingList.get(0 + displacement).getTakenBlocks();
					currentBuildingColor = currentBuildingList.get(0 + displacement).getColor();
				}
			}

		});

		buildingTwo = new JButton("");
		buildingTwo.setFocusable(false);
		buildingTwo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 1){
					currentImage = currentBuildingList.get(1 + displacement).getImage();
					currentBuildingSize = currentBuildingList.get(1 + displacement).getTakenBlocks();
					currentBuildingColor = currentBuildingList.get(1 + displacement).getColor();
				}
			}

		});

		buildingThree = new JButton("");
		buildingThree.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 2){
					currentImage = currentBuildingList.get(2 + displacement).getImage();
					currentBuildingSize = currentBuildingList.get(2 + displacement).getTakenBlocks();
					currentBuildingColor = currentBuildingList.get(2 + displacement).getColor();
				}
			}

		});

		buildingFour = new JButton("");
		buildingFour.setFocusable(false);
		buildingFour.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 3){
					currentImage = currentBuildingList.get(3 + displacement).getImage();
					currentBuildingSize = currentBuildingList.get(3 + displacement).getTakenBlocks();
					currentBuildingColor = currentBuildingList.get(3 + displacement).getColor();
				}
			}

		});

		buildingFive = new JButton("");
		buildingFive.setFocusable(false);
		buildingFive.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 4){
					currentImage = currentBuildingList.get(4 + displacement).getImage();
					currentBuildingSize = currentBuildingList.get(4 + displacement).getTakenBlocks();
					currentBuildingColor = currentBuildingList.get(4 + displacement).getColor();
				}
			}

		});


		JButton scrollDownButton = new JButton("Down");
		scrollDownButton.setFocusable(false);
		scrollDownButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(displacement < maxDisplacement){
					displacement++;
					updateButtonDisplay();
				}
			}

		});

		updateButtonDisplay();

		buildingSelection.add(scrollUpButton);
		buildingSelection.add(buildingOne);
		buildingSelection.add(buildingTwo);
		buildingSelection.add(buildingThree);
		buildingSelection.add(buildingFour);
		buildingSelection.add(buildingFive);
		buildingSelection.add(scrollDownButton);

		add(display, c);

		c.gridx = 1;
		add(buildingSelection, c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		add(informationPanel, c);
	}

	public void updateButtonDisplay(){
		if(currentBuildingList.size() > 0){
			buildingOne.setIcon(new ImageIcon(currentBuildingList.get(0 + displacement).getImage()));
		} else {
			buildingOne.setIcon(new ImageIcon(defaultImage));
		}
		if(currentBuildingList.size() > 1){
			buildingTwo.setIcon(new ImageIcon(currentBuildingList.get(1 + displacement).getImage()));
		} else {
			buildingTwo.setIcon(new ImageIcon(defaultImage));
		}
		if(currentBuildingList.size() > 2){
			buildingThree.setIcon(new ImageIcon(currentBuildingList.get(2 + displacement).getImage()));
		} else {
			buildingThree.setIcon(new ImageIcon(defaultImage));
		}
		if(currentBuildingList.size() > 3){
			buildingFour.setIcon(new ImageIcon(currentBuildingList.get(3 + displacement).getImage()));
		} else {
			buildingFour.setIcon(new ImageIcon(defaultImage));
		}
		if(currentBuildingList.size() > 4){
			buildingFive.setIcon(new ImageIcon(currentBuildingList.get(4 + displacement).getImage()));
		} else {
			buildingFive.setIcon(new ImageIcon(defaultImage));
		}
	}

	public void giveCardManager(CardManager newCM){
		cM = newCM;
	}

	public void giveSettlement(Settlement newSettlement){
		currentSettlement = newSettlement;
	}

	public void mouseMoved(MouseEvent event){
		giveMouseX(event.getX());
		giveMouseY(event.getY());

		updateSettlementGrid();

		repaint();
	}

	public void updateSettlementGrid(){
		if(currentBuildingSize != null){
			clearShadowPlacement();
			clearToPlace = true;
			for(int i = 0; i < currentBuildingSize.size(); i ++){
				if(mouseGridX + currentBuildingSize.get(i)[0] > -1 && mouseGridX + currentBuildingSize.get(i)[0] < settlementGrid.length && mouseGridY + currentBuildingSize.get(i)[1] > -1 && mouseGridY + currentBuildingSize.get(i)[1] < settlementGrid.length){ 
					switch(settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].getValue()){
					case 0:
						settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(2);
						break;
					case 1:
						settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(3);
						clearToPlace = false;
						break;
					default:
						break;
					}
				} else {
					clearToPlace = false;
				}
			}
		}
	}

	public void clearShadowPlacement(){
		for(int x = 0; x < settlementGrid.length; x ++){
			for(int y = 0; y < settlementGrid.length; y ++){
				switch(settlementGrid[x][y].getValue()){
				case 2:
					settlementGrid[x][y].setValue(0);
					break;
				case 3:
					settlementGrid[x][y].setValue(1);
					break;
				default:
					break;
				}
			}
		}
	}

	public void rotateBuilding(ArrayList<int[]> toRotate){
		if(toRotate != null){
			for(int i = 0; i < toRotate.size(); i ++){
				int holder = toRotate.get(i)[1]; 
				toRotate.get(i)[1] = toRotate.get(i)[0];
				toRotate.get(i)[0] = (holder * -1);
			}
			updateSettlementGrid();
			repaint();
		}
	}

	public void giveMouseX(int newX){
		mouseX = newX;
		mouseGridX = Math.floorDiv(mouseX - 50, gapWidth);
	}
	public void giveMouseY(int newY){
		mouseY = newY;
		mouseGridY = Math.floorDiv(mouseY - 50, gapWidth);
	}
	public void mouseClicked(MouseEvent event){
		if(clearToPlace){
			for(int i = 0; i < currentBuildingSize.size(); i ++){
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(1);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setColor(currentBuildingColor);
			}
		}
		updateSettlementGrid();
		repaint();
	}
	public void mouseDragged(MouseEvent event){
		if(clearToPlace){
			for(int i = 0; i < currentBuildingSize.size(); i ++){
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(1);
			}
		}
		updateSettlementGrid();
		repaint();
	}
	public void keyPressed(KeyEvent event){
		switch(event.getKeyCode()){
		case KeyEvent.VK_R:
			rotateBuilding(currentBuildingSize);
			break;
		default:
			break;
		}
	}

	public class GridPaint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			int gridWidth = screenWidth - 600;
			int gridHeight = screenHeight - 300;

			settlementGrid = currentSettlement.getGrid();

			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth - 300, screenHeight- 200);

			g.setColor(Color.BLACK);
			g.drawRect(50, 50, gridWidth, gridHeight);

			gapWidth = gridWidth/currentSettlement.getGridSize();

			for(int countX = 0; countX < currentSettlement.getGridSize(); countX ++){
				for(int countY = 0; countY < currentSettlement.getGridSize(); countY ++){
					g.setColor(Color.BLACK);
					g.drawRect(50 + countX*gapWidth, 50 + countY*gapWidth, gapWidth, gapWidth);
					switch(settlementGrid[countX][countY].getValue()){
					case 1:
						g.setColor(settlementGrid[countX][countY].getColor());
						g.fillRect(51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1);
						break;
					case 2:
						g.setColor(Color.YELLOW);
						g.fillRect(51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1);
						break;
					case 3:
						g.setColor(Color.RED);
						g.fillRect(51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1);
						break;
					default:
						break;
					} 
				}
			}
		}
	}
}
