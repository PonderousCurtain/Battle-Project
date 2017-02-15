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


public class SettlementManager extends JPanel implements Cloneable{
	int screenWidth;
	int screenHeight;
	Settlement currentSettlement;
	ArrayList<Building> currentBuildingList;
	ArrayList<Building> placedBuildingList;
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
	
	JButton removeButton;

	Image currentImage;
	Building currentBuilding;
	Building selectedBuilding;
	ArrayList<int[]> currentBuildingSize;
	ArrayList<int[]> selectedList;
	Color currentBuildingColor;
	Image currentBuildingPlacedImage;

	int mouseX;
	int mouseY;

	int indexCount;

	int mouseGridX;
	int mouseGridY;

	int gapWidth;
	SettlementGridSpace[][] settlementGrid;

	Boolean clearToPlace;
	Boolean removing;

	public SettlementManager(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		clearToPlace = false;
		removing = false;

		selectedList = new ArrayList<int[]>();

		settlementList = new ArrayList<Settlement>();
		Settlement testSettlement1 = new Settlement(10, 2);
		settlementList.add(testSettlement1);

		currentSettlement = settlementList.get(0);
		currentBuildingList = currentSettlement.getBuildings();
		placedBuildingList = currentSettlement.getPlacedBuildings();
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
		removeButton = new JButton("Remove Buildings");
		removeButton.setFocusable(false);
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				removing = !removing;
				if(removing){
					removeButton.setText("Place Buildings");
				} else {
					removeButton.setText("Remove Buildings");
				}
			}

		});

		JPanel informationPanel = new JPanel();
		informationPanel.setPreferredSize(new Dimension(screenWidth, 200));
		informationPanel.setLayout(new GridLayout(0,2));
		informationPanel.add(returnButton);
		informationPanel.add(removeButton);

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
					getBuildingAtButton(0);
				}
			}

		});

		buildingTwo = new JButton("");
		buildingTwo.setFocusable(false);
		buildingTwo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 1){
					getBuildingAtButton(1);
				}
			}

		});

		buildingThree = new JButton("");
		buildingThree.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 2){
					getBuildingAtButton(2);
				}
			}

		});

		buildingFour = new JButton("");
		buildingFour.setFocusable(false);
		buildingFour.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 3){
					getBuildingAtButton(3);
				}
			}

		});

		buildingFive = new JButton("");
		buildingFive.setFocusable(false);
		buildingFive.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 4){
					getBuildingAtButton(4);
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
	
	public void getBuildingAtButton(int buttonNumber){
		currentImage = currentBuildingList.get(buttonNumber + displacement).getImage();
		currentBuildingSize = currentBuildingList.get(buttonNumber + displacement).getTakenBlocks();
		currentBuildingColor = currentBuildingList.get(buttonNumber + displacement).getColor();
		currentBuildingPlacedImage = currentBuildingList.get(buttonNumber + displacement).getPlacedImage();
		currentBuilding = currentBuildingList.get(buttonNumber + displacement);
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

	public void setSettlement(int newSettlement){
		currentSettlement = settlementList.get(newSettlement);
		currentBuildingList = currentSettlement.getBuildings();
		placedBuildingList = currentSettlement.getPlacedBuildings();
	}

	public void mouseMoved(MouseEvent event){
		giveMouseX(event.getX());
		giveMouseY(event.getY());

		if(!removing){
			updateSettlementGridPlace();
		} else {
			updateSettlementGridRemove();
		}

		repaint();
	}

	public void updateSettlementGridPlace(){
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

	public void updateSettlementGridRemove(){
		clearShadowPlacement();
		selectedList.clear();
		if(mouseGridX > -1 && mouseGridX < settlementGrid.length && mouseGridY > -1 && mouseGridY < settlementGrid.length){
			int placeBuildingID = settlementGrid[mouseGridX][mouseGridY].getPlacedBuildingID();

			for(int i = 0; i < placedBuildingList.size(); i ++){
				if(placedBuildingList.get(i).getPlacedIndex() == placeBuildingID){

					//colour and select the blocks to remove
					for(int xCount = 0; xCount < settlementGrid.length; xCount ++){
						for(int yCount = 0; yCount < settlementGrid.length; yCount ++){
							if(settlementGrid[xCount][yCount].getPlacedBuildingID() == placeBuildingID){
								settlementGrid[xCount][yCount].setValue(3);
								selectedList.add(new int[] {xCount, yCount});
							}
						}
					}
					selectedBuilding = placedBuildingList.get(i);
					break;
				}
			}
		}
	}

	public void removeSelected(){
		for(int i = 0; i < selectedList.size(); i ++){
			int xRef = selectedList.get(i)[0];
			int yRef = selectedList.get(i)[1];
			settlementGrid[xRef][yRef].setValue(0);
			settlementGrid[xRef][yRef].setBuildingID(-1);
		}
		selectedList.clear();
		placedBuildingList.remove(selectedBuilding);
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
			updateSettlementGridPlace();
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

		if(clearToPlace && !removing){
			currentBuilding.setPlacedIndex(indexCount);

			placedBuildingList.add(new Building(currentBuilding));

			for(int i = 0; i < currentBuildingSize.size(); i ++){
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(1);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setColor(currentBuildingColor);
				//settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setImage(currentBuildingPlacedImage);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setBuildingID(indexCount);
			}
			updateSettlementGridPlace();
			indexCount ++;
		} else if(removing){
			removeSelected();
		}
		repaint();
	}

	public void mouseDragged(MouseEvent event){
		if(clearToPlace && !removing){
			currentBuilding.setPlacedIndex(indexCount);

			placedBuildingList.add(new Building(currentBuilding));

			for(int i = 0; i < currentBuildingSize.size(); i ++){
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(1);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setColor(currentBuildingColor);
				//settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setImage(currentBuildingPlacedImage);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setBuildingID(indexCount);
			}
			updateSettlementGridPlace();
			indexCount ++;
		} else if(removing){
			removeSelected();
		}
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
			
						//g.drawImage(settlementGrid[countX][countY].getPlacedImage(), 51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1, null);
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
