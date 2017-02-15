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
	//declare the required variables for the class
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
		//Initialise the main variables needed
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		clearToPlace = false;
		removing = false;

		selectedList = new ArrayList<int[]>();

		//create the list of settlements
		settlementList = new ArrayList<Settlement>();
		//create a settlement for testing purposes and add it to the list
		Settlement testSettlement1 = new Settlement(10, 2);
		settlementList.add(testSettlement1);

		//set the default settlement as the first in the list
		currentSettlement = settlementList.get(0);
		//get the list of buildings available to the settlement
		currentBuildingList = currentSettlement.getBuildings();
		//get the buildings that are already placed
		placedBuildingList = currentSettlement.getPlacedBuildings();
		//set the default image for the placed buildings
		defaultImage = new ImageIcon("default.jpg").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);

		//set up the layout manager for the panel
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;

		//set up the return button
		JButton returnButton = new JButton("Return");
		returnButton.setFocusable(false);
		returnButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//add the code to return the focus of the screen to the over world
				cM.showCard("SettlementManager", "OverCard");
			}

		});
		//set up the remove button to the panel
		removeButton = new JButton("Remove Buildings");
		removeButton.setFocusable(false);
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//toggle the boolean for whether a building is presently being removed or placed
				removing = !removing;
				//correct the button text to reflect the boolean value
				if(removing){
					removeButton.setText("Place Buildings");
				} else {
					removeButton.setText("Remove Buildings");
				}
			}

		});

		//set up the information panel for the settlement
		JPanel informationPanel = new JPanel();
		informationPanel.setPreferredSize(new Dimension(screenWidth, 200));
		informationPanel.setLayout(new GridLayout(0,2));
		//add the return and remove buttons to the information panel
		informationPanel.add(returnButton);
		informationPanel.add(removeButton);

		GridPaint display = new GridPaint();
		display.setPreferredSize(new Dimension(screenWidth - 500, screenHeight - 200));

		JPanel buildingSelection = new JPanel();
		buildingSelection.setLayout(new GridLayout(0, 1));
		buildingSelection.setPreferredSize(new Dimension(500, screenHeight - 200));

		//set up the displacement for the building list 
		displacement = 0;
		//set up the max displacement to avoid null pointers on the end of the list
		maxDisplacement = currentBuildingList.size() - 5;

		//set up the scroll up button
		JButton scrollUpButton = new JButton("Up");
		scrollUpButton.setFocusable(false);
		scrollUpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//check if the list is not all ready at the top
				if(displacement > 0){
					//if the list can go up then let the list move one slot upwards
					displacement--;
					//update the buttons on the panel
					updateButtonDisplay();
				}
			}

		});
		//set up the buildings buttons to select placeables
		buildingOne = new JButton("");
		buildingOne.setFocusable(false);
		buildingOne.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentBuildingList.size() > 0){
					//if there exists another building type in the settlement then return that building
					getBuildingAtButton(0);
				}
			}

		});
		//set up the other 4 buttons for the panel in the same way as button one
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

		//set up the scroll down button for the button list
		JButton scrollDownButton = new JButton("Down");
		scrollDownButton.setFocusable(false);
		scrollDownButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//check if the end of the list has not been reached
				if(displacement < maxDisplacement){
					//if the end of the list has not been reached then shuffle the list one index down
					displacement++;
					//update the buttons
					updateButtonDisplay();
				}
			}

		});

		updateButtonDisplay();

		//add all the buttons to the building selection panel
		buildingSelection.add(scrollUpButton);
		buildingSelection.add(buildingOne);
		buildingSelection.add(buildingTwo);
		buildingSelection.add(buildingThree);
		buildingSelection.add(buildingFour);
		buildingSelection.add(buildingFive);
		buildingSelection.add(scrollDownButton);

		//add all the panels to the main class panel
		add(display, c);

		c.gridx = 1;
		add(buildingSelection, c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		add(informationPanel, c);
	}
	
	public void getBuildingAtButton(int buttonNumber){
		//set all the building variables to the ones at the referenced index from the buttons
		currentImage = currentBuildingList.get(buttonNumber + displacement).getImage();
		currentBuildingSize = currentBuildingList.get(buttonNumber + displacement).getTakenBlocks();
		currentBuildingColor = currentBuildingList.get(buttonNumber + displacement).getColor();
		currentBuildingPlacedImage = currentBuildingList.get(buttonNumber + displacement).getPlacedImage();
		currentBuilding = currentBuildingList.get(buttonNumber + displacement);
	}

	public void updateButtonDisplay(){
		//check if there exists another building in the list
		if(currentBuildingList.size() > 0){
			//set the building one button to have the icon of the building at that index of the list
			buildingOne.setIcon(new ImageIcon(currentBuildingList.get(0 + displacement).getImage()));
		} else {
			//if there is not another building type available for the settlement then display the default image
			buildingOne.setIcon(new ImageIcon(defaultImage));
		}
		//repeat the building button one for the rest of the buttons with their respective off sets
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
		//get the card manager from the rest of the program
		cM = newCM;
	}

	public void setSettlement(int newSettlement){
		//set the new settlement variables to that of the new settlement
		currentSettlement = settlementList.get(newSettlement);
		//update the building lists to those of the new settlement
		currentBuildingList = currentSettlement.getBuildings();
		placedBuildingList = currentSettlement.getPlacedBuildings();
		//update the button display to allow for the new building types in the new settlement
		updateButtonDisplay();
	}

	public void mouseMoved(MouseEvent event){
		//handle the mouse event for this class
		giveMouseX(event.getX());
		giveMouseY(event.getY());

		//update the hovering place and remove images depending on whether the button has been selected
		if(!removing){
			updateSettlementGridPlace();
		} else {
			updateSettlementGridRemove();
		}

		repaint();
	}

	public void updateSettlementGridPlace(){
		//update the shadow placement of the currently selected building
		if(currentBuildingSize != null){
			clearShadowPlacement();
			clearToPlace = true;
			for(int i = 0; i < currentBuildingSize.size(); i ++){
				//run through the size of the building
				//check if the segment of the building is inside the grid
				if(mouseGridX + currentBuildingSize.get(i)[0] > -1 && mouseGridX + currentBuildingSize.get(i)[0] < settlementGrid.length && mouseGridY + currentBuildingSize.get(i)[1] > -1 && mouseGridY + currentBuildingSize.get(i)[1] < settlementGrid.length){
					//check the content of the grid square 
					switch(settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].getValue()){
					case 0:
						//if there is nothing there then change the index to 2 signifying it is being hovered over but there is nothing there
						settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(2);
						break;
					case 1:
						//if there is a building in that section already then change the index to 3 to show it is being hovered over but there is still a building there
						settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(3);
						//if any of the squares are occupied then then set the clear to place to be false
						clearToPlace = false;
						break;
					default:
						break;
					}
				} else {
					//if any of the squares of the building being placed are outside the grid then setr clear to place to be false
					clearToPlace = false;
				}
			}
		}
	}

	public void updateSettlementGridRemove(){
		//update the hovering of the remove option
		//clear the previous placement
		clearShadowPlacement();
		//clear the list of the selected building squares that are highlighted for removal
		selectedList.clear();
		//check if the mouse location is within the settlement grid
		if(mouseGridX > -1 && mouseGridX < settlementGrid.length && mouseGridY > -1 && mouseGridY < settlementGrid.length){
			//get the ID of the building segment that the mouse is currently over
			int placeBuildingID = settlementGrid[mouseGridX][mouseGridY].getPlacedBuildingID();
			//run through the list of placed buildings
			for(int i = 0; i < placedBuildingList.size(); i ++){
				//check if the building being checked has the same ID as the segment the mouse is hovering over
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
					//set the selected building to the placed buildign with that index
					selectedBuilding = placedBuildingList.get(i);
					break;
				}
			}
		}
	}

	public void removeSelected(){
		//remove the buildign from the placed building list and re-index the grid segments that the building took up
		for(int i = 0; i < selectedList.size(); i ++){
			int xRef = selectedList.get(i)[0];
			int yRef = selectedList.get(i)[1];
			//set the value of the squares to that of an empty square (0)
			settlementGrid[xRef][yRef].setValue(0);
			//set the building index of the segment to -1 to ensure it is not found as a removeable building
			settlementGrid[xRef][yRef].setBuildingID(-1);
		}
		//clear the selected list and remove the building
		selectedList.clear();
		placedBuildingList.remove(selectedBuilding);
	}

	public void clearShadowPlacement(){
		//clear the shadow placement
		//loop through the settlement grid
		for(int x = 0; x < settlementGrid.length; x ++){
			for(int y = 0; y < settlementGrid.length; y ++){
				//check the index of the grid square
				switch(settlementGrid[x][y].getValue()){
				case 2:
					//if the index is 2 (signifying there is the nothing in the square but it was being hovered over) set the value to 0 (just empty)
					settlementGrid[x][y].setValue(0);
					break;
				case 3:
					//if the index is 3 (signifying there is a building there and it was being hovered over) set the value to just having a building in it (1)
					settlementGrid[x][y].setValue(1);
					break;
				default:
					break;
				}
			}
		}
	}

	public void rotateBuilding(ArrayList<int[]> toRotate){
		//rotate the building
		//check if there exists a current building able to rotate
		if(toRotate != null){
			//if there is then for each location that building takes up re-define it such that the building rotates 90 degrees clockwise
			for(int i = 0; i < toRotate.size(); i ++){
				int holder = toRotate.get(i)[1]; 
				toRotate.get(i)[1] = toRotate.get(i)[0];
				toRotate.get(i)[0] = (holder * -1);
			}
			//update the settlement grid placement
			updateSettlementGridPlace();
			repaint();
		}
	}

	public void giveMouseX(int newX){
		//get the x position of the mouse
		mouseX = newX;
		//convert the x position to a relative grid square coordinate
		mouseGridX = Math.floorDiv(mouseX - 50, gapWidth);
	}

	public void giveMouseY(int newY){
		//get the y position of the mouse
		mouseY = newY;
		//convert the y position to a relative grid square coordinate
		mouseGridY = Math.floorDiv(mouseY - 50, gapWidth);
	}

	public void mouseClicked(MouseEvent event){
		//get the mouse clicked event for this panel
		//check if the user is currently trying to place a building and that building is in a place where it can be placed down
		if(clearToPlace && !removing){
			//get the placed index of the building trying to be placed to the next available index
			currentBuilding.setPlacedIndex(indexCount);

			//add the building trying to be placed to the placed building list
			placedBuildingList.add(new Building(currentBuilding));

			//loop through the new building size
			for(int i = 0; i < currentBuildingSize.size(); i ++){
				//set the values for the settlement grid square to that of the new values fo the building being placed
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setValue(1);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setColor(currentBuildingColor);
				//settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setImage(currentBuildingPlacedImage);
				settlementGrid[mouseGridX + currentBuildingSize.get(i)[0]][mouseGridY + currentBuildingSize.get(i)[1]].setBuildingID(indexCount);
			}
			//update the settlement grid placement to reflect that there is a building in that position now
			updateSettlementGridPlace();
			//increase the index count for the next building
			indexCount ++;
		} else if(removing){
			//if the user is trying to remove a building and clicks then remove the selected building
			removeSelected();
		}
		repaint();
	}

	public void mouseDragged(MouseEvent event){
		//handle mouse dragging
		//as mouse clicked and dragged are no different currently then just call the mouse clicked function
		mouseClicked(event);
	}

	public void keyPressed(KeyEvent event){
		switch(event.getKeyCode()){
		case KeyEvent.VK_R:
			//add a key binding for rotating buildings
			rotateBuilding(currentBuildingSize);
			break;
		case KeyEvent.VK_ESCAPE:
			//add a key binding for exiting the settlement manager to the over world screen
			cM.showCard("SettlementManager", "OverCard");
			break;
		default:
			break;
		}
	}

	public class GridPaint extends JPanel{
		public void paintComponent(Graphics gr){
			//paint the panel display
			Graphics2D g = (Graphics2D) gr;
			
			//set the grid width and size

			int gridWidth = screenWidth - 600;
			int gridHeight = screenHeight - 300;

			//set the grid values to that of the current settlement
			settlementGrid = currentSettlement.getGrid();

			//fill a grey background
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth - 300, screenHeight- 200);

			//draw the settlement grid outline
			g.setColor(Color.BLACK);
			g.drawRect(50, 50, gridWidth, gridHeight);

			//set the gapWidth
			gapWidth = gridWidth/currentSettlement.getGridSize();

			//loop for the current settlement size for each grid square
			for(int countX = 0; countX < currentSettlement.getGridSize(); countX ++){
				for(int countY = 0; countY < currentSettlement.getGridSize(); countY ++){
					//draw the grid square 
					g.setColor(Color.BLACK);
					g.drawRect(50 + countX*gapWidth, 50 + countY*gapWidth, gapWidth, gapWidth);
					//check the value of the square being checked
					switch(settlementGrid[countX][countY].getValue()){
					case 1:
						//if there is a building in that square then fill the colour of the correct building
						g.setColor(settlementGrid[countX][countY].getColor());
						g.fillRect(51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1);
			
						//g.drawImage(settlementGrid[countX][countY].getPlacedImage(), 51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1, null);
						break;
					case 2:
						//if there is a building and the square is being hovered over then pain the square yellow
						g.setColor(Color.YELLOW);
						g.fillRect(51 + countX*gapWidth, 51 + countY*gapWidth, gapWidth - 1, gapWidth - 1);
						break;
					case 3:
						//if the user if trying to place a building on a square but cannot because there is already a building there paint it red
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
