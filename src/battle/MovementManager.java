package battle;
import java.awt.Rectangle;
import java.util.ArrayList;

import overworld.Unit;
import utilities.Obstruction;


public class MovementManager {
	//create the list of units on the map and the map itself
	ArrayList<Unit> unitList;
	Obstruction[][] staticBlockages;

	public MovementManager(Obstruction[][] staticBlockages){
		//initialise the unit list and set the map as the one passed by the map display
		this.staticBlockages = staticBlockages;
		unitList = new ArrayList<Unit>();
	}

	public void checkArrivedAtNode(Unit checkUnit, Node checkNode, int pathType){
		//check if the unit has matched its x and y values to those of the target node
		if(checkUnit.getX() == checkNode.getActualX() && checkUnit.getY() == checkNode.getActualY()){
			//check if the path was a standard path
			if(pathType == 0){
				//if it is then remove the node that has been arrived at from the path
				checkUnit.removeLast();
			} else {
				//otherwise remove the node from the temporary path
				checkUnit.removeTempLast();
			}
		}
		//check if the path type was standard and if so if the end of the path has been reached
		if(pathType == 0 && checkUnit.getPath().size() == 0){
			//if the end of the path has been reached then set the unit as having arrived
			checkUnit.setArrived(true);
		}

	}

	public void addUnitList(ArrayList<Unit> newListToAdd){
		//add a new set of units to the manager
		unitList.addAll(newListToAdd);
	}
	public void emptyUnitList(){
		//empty the list of units on the map
		unitList.clear();
	}
	public void removeUnit(Unit toRemove){
		//loop through all the units
		for(int i = 0; i < unitList.size(); i ++){
			//if the unit has been marked as should be removed then remove it
			if(unitList.get(i) == toRemove){
				unitList.remove(i);
				//if a unit is removed then lower i to accound for it so no units are missed
				i--;
			}
		}
	}

	public void moveStep(Unit nextUnit){
		//set the default target node and path type
		Node targetNode = null;
		int pathType = 0;
		//check if a temporary path exists for the unit
		if(nextUnit.getTempPath().size() > 0){
			//if the unit has a temporary path then set the type to 1 to indicate this and set the target node to the next node on the path
			pathType = 1;
			targetNode = nextUnit.getTempPathNext();
		} else if(nextUnit.getPath().size() > 0){
			//otherwise if there is a path then set the target node to the next node in the path
			targetNode = nextUnit.getPathNext();
		}

		//check if movement in the x direction is allowed
		if(moveXCheck(targetNode.getActualX(), nextUnit)){
			//check the direction of movement and move the unit in that direction the distance equal to its speed
			if(nextUnit.getX() < targetNode.getActualX()){
				nextUnit.setX(nextUnit.getX() + nextUnit.getSpeed());
			} else if (nextUnit.getX() > targetNode.getActualX()){
				nextUnit.setX(nextUnit.getX() - nextUnit.getSpeed());
			}
		}
		//check if movement in the x direction is allowed
		if(moveYCheck(targetNode.getActualY(), nextUnit)){
			//check the direction of movement and move the unit in that direction the distance equal to its speed
			if(nextUnit.getY() < targetNode.getActualY()){
				nextUnit.setY(nextUnit.getY() + nextUnit.getSpeed());
			} else if (nextUnit.getY() > targetNode.getActualY()){
				nextUnit.setY(nextUnit.getY() - nextUnit.getSpeed());
			}
		}

		//check if the unit has arrived at it's destination
		checkArrivedAtNode(nextUnit, targetNode, pathType);
	}

	public Boolean moveXCheck(int targetX, Unit checkUnit){
		//working as of 16/9/16
		//amended as of 11/10/16
		//set the default as able to move
		Boolean moveXCheck = true;

		//create a rectangle that will be used as a hit box check
		Rectangle checkTangleX = new Rectangle(checkUnit.getRect());

		//check the direction the unit is trying to move in and extend the hit box in that direction by the distance to be moved
		if(checkUnit.getX() < targetX){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth() + checkUnit.getSpeed(), (int)checkTangleX.getHeight()));
		} else if(checkUnit.getX() > targetX){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX() - checkUnit.getSpeed(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth() + checkUnit.getSpeed(), (int)checkTangleX.getHeight()));
		}

		//loop through each unit present on the map
		for(Unit presentUnit: unitList){
			//check if the new location of the unit being moved will intersect any other unit
			if(checkTangleX.intersects(presentUnit.getRect()) && checkUnit != presentUnit){
				//if the two units intersect, check if they are both on the same type of terrain
				if(presentUnit.getType() == checkUnit.getType()){
					//if they are on the same terrain do not allow the movement
					moveXCheck = false;
				}
			}
		}

		//get the grid coordinates of the top right corner of the hit box
		int currentXCoord = (int) Math.floor(checkTangleX.getX()/checkUnit.getWidth());
		int currentYCoord = (int) Math.floor(checkTangleX.getY()/checkUnit.getWidth());

		//set a default height of 1 (the number of grid spaces the unit is in)
		int height = 1;
		//check if the unit is perfectly in a grid square
		if(checkUnit.getY() % checkUnit.getWidth() != 0){
			//if the unit is not perfectly in a grid square then set it as covering 2 grid squares
			height = 2;
		}

		//check all the grid spaces that the hit box is touching
		for(int cX = 0; cX < 2; cX ++){
			for(int cY = 0; cY < height; cY ++){
				//check that grid square being checked exists on the map
				if(currentXCoord + cX < 70 && currentYCoord + cY < 70){
					//switch on the unit type (as land air and sea units can travel on different terrains
					switch(checkUnit.getType()){
					case 0:
						//if the unit is a land unit then check that the grid space is land, bridge and not a wall
						if((staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 0 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3) || staticBlockages[currentXCoord + cX][currentYCoord + cY].getRoughness() == 0){
							//if the unit cannot move on the grid space then do not let it move
							moveXCheck = false;
						}
						//land
						break;
					case 1:
						//air can move anywhere
						break;
					case 2:
						//if the unit is a sea unit then check that the grid space is water or a bridge
						if(staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 2 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3){
							//if the unit cannot move on the grid space then do not let it move
							moveXCheck = false;
						}
						//sea
						break;
					default:
						break;
					}
				}
			}
		}
		//return if the move is allowed
		return moveXCheck;
	}

	public Boolean moveYCheck(int targetY, Unit checkUnit){
		//working as of 16/9/16
		//set the default as able to move
		Boolean moveYCheck = true;

		//create a rectangle that will be used as a hit box check
		Rectangle checkTangleX = new Rectangle(checkUnit.getRect());

		//check the direction the unit is trying to move in and extend the hit box in that direction by the distance to be moved
		if(checkUnit.getY() < targetY){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth(), (int)checkTangleX.getHeight() + checkUnit.getSpeed()));
		} else if(checkUnit.getY() > targetY){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY() - checkUnit.getSpeed(), (int)checkTangleX.getWidth(), (int)checkTangleX.getHeight() + checkUnit.getSpeed()));
		}

		//loop through each unit present on the map
		for(Unit presentUnit: unitList){
			//check if the new location of the unit being moved will intersect any other unit
			if(checkTangleX.intersects(presentUnit.getRect()) && checkUnit != presentUnit){
				//if the two units intersect, check if they are both on the same type of terrain
				if(presentUnit.getType() == checkUnit.getType()){
					//if they are on the same terrain do not allow the movement
					moveYCheck = false;
				}
			}
		}

		//get the grid coordinates of the top right corner of the hit box
		int currentXCoord = (int) Math.floor(checkTangleX.getX()/checkUnit.getWidth());
		int currentYCoord = (int) Math.floor(checkTangleX.getY()/checkUnit.getWidth());

		//set a default width of 1 (the number of grid spaces the unit is in)
		int width = 1;
		//check if the unit is perfectly in a grid square
		if(checkUnit.getX() % checkUnit.getWidth() != 0){
			//if the unit is not perfectly in a grid square then set it as covering 2 grid squares
			width = 2;
		}

		//check all the grid spaces that the hit box is touching
		for(int cX = 0; cX < width; cX ++){
			for(int cY = 0; cY < 2; cY ++){
				//check that grid square being checked exists on the map
				if(currentXCoord + cX < 70 && currentYCoord + cY < 70){
					//switch on the unit type (as land air and sea units can travel on different terrains
					switch(checkUnit.getType()){
					case 0:
						//if the unit is a land unit then check that the grid space is land, bridge and not a wall
						if((staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 0 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3) || staticBlockages[currentXCoord + cX][currentYCoord + cY].getRoughness() == 0){
							//if the unit cannot move on the grid space then do not let it move
							moveYCheck = false;
						}
						//land
						break;
					case 1:
						//air can move anywhere
						break;
					case 2:
						//if the unit is a sea unit then check that the grid space is water or a bridge
						if(staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 2 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3){
							//if the unit cannot move on the grid space then do not let it move
							moveYCheck = false;
						}
						//sea
						break;
					default:
						break;
					}
				}
			}
		}
		//return is the movement is allowed
		return moveYCheck;
	}
}
