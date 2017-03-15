package battle;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

import utilities.Obstruction;



//Path finding class working as of 4/10/16

public class PathFinder {
	//declare the variables needed for this class
	ArrayList<Node[][]> mapLibrary;
	Obstruction[][] staticBlockages;
	int screenSize;
	LinkedList<Node> closedList;
	LinkedList<Node> openList;
	ArrayList<Node> scanned;

	public PathFinder(Obstruction[][] blockages, int screenSize){
		//initialise the variables needed by the class
		this.screenSize = screenSize;
		closedList = new LinkedList<Node>();
		openList = new LinkedList<Node>();
		scanned = new ArrayList<Node>();
		mapLibrary = new ArrayList<Node[][]>();

		staticBlockages = blockages;
	}

	public void setUpMap(int blockWidth){
		Boolean alreadyMade = false;
		//loop through the maps that have already been made
		for(Node[][] nextMap: mapLibrary){
			//if a map of the needed size has already been made then don't make a new one
			if(nextMap.length == screenSize/blockWidth){
				alreadyMade = true;
			}
		}
		if(alreadyMade == false){
			//if a map of the needed size isn't made yet then set up a new one
			Node[][] grid = new Node[screenSize/blockWidth][screenSize/blockWidth];
			//create a map of nodes the size of the new map specified
			for(int i = 0; i < screenSize; i += blockWidth){
				for(int j = 0; j < screenSize; j += blockWidth){
					grid[i/blockWidth][j/blockWidth] = new Node(i/blockWidth, j/blockWidth, blockWidth);

					//set the values of the new node to the values of the blockages present on the map
					grid[i/blockWidth][j/blockWidth].setMoveCost(staticBlockages[i/10][j/10].getRoughness());
					grid[i/blockWidth][j/blockWidth].setTileType(staticBlockages[i/10][j/10].getTileType());

					//loop through the other squares in the grid to check that the entire grid space can accommodate the larger unit
					for(int countX = 0; countX < (blockWidth / 10); countX++){
						for(int countY = 0; countY < (blockWidth / 10); countY++){
							if(staticBlockages[(i/10) + countX][(j/10) + countY].getRoughness() > grid[i/blockWidth][j/blockWidth].getMoveCost() && grid[i/blockWidth][j/blockWidth].getMoveCost() != 0){
								//if the roughness of any grid square in the larger map square is larger then replace the large square with the higher value
								grid[i/blockWidth][j/blockWidth].setMoveCost(staticBlockages[(i/10) + countX][(j/10) + countY].getRoughness());
							}
							//check if there is any discrepancy between the terrain types
							if(staticBlockages[(i/10) + countX][(j/10) + countY].getTileType() != grid[i/blockWidth][j/blockWidth].getTileType()){
								//some of the grid squares in the larger map square are different terrain types
								//check if one of the types is a bridge as if it is then the tile should be set as bridge to allow anything that uses a bridge to cross
								if((staticBlockages[(i/10) + countX][(j/10) + countY].getTileType() == 3 || grid[i/blockWidth][j/blockWidth].getTileType() == 3)){
									grid[i/blockWidth][j/blockWidth].setTileType(3);
								} else {
									//otherwise set the terrain type to a chasm so that only aircraft can use it to avoid large ships being able to run on ground and large land units on water
									grid[i/blockWidth][j/blockWidth].setTileType(1);
								}
							}
						}
					}
					//check if the block is a wall
					if(grid[i/blockWidth][j/blockWidth].getMoveCost() == 0){
						grid[i/blockWidth][j/blockWidth].setBlocked(true);
					}
				}
			}
			//add the new map to the library of made maps
			mapLibrary.add(grid);
		}
	}


	public int[] getAproxXY(int x, int y, int width, int screenSize, int blockWidth){
		int[] ijStore = new int[2];
		//loop through a phantom grid of squares the width and height of the unit being looked for
		for(int i = 0; i < screenSize/blockWidth; i++){
			for(int j = 0; j < screenSize/blockWidth; j++){
				//check if the square being checked intersects the unit location
				if(new Rectangle(x, y, width, width).intersects(new Rectangle(i * blockWidth, j * blockWidth, blockWidth, blockWidth))){
					//if it does then store the grid x and y location of the square that the unit intersects
					ijStore[0] = i;
					ijStore[1] = j;
					//break out of the for loop
					break;
				}
			}
		}
		//return the x and y of the approximate square the unit is in
		return ijStore;
	}

	public ArrayList<Node> pathFind(int currentX, int currentY, int width, int targetX, int targetY, int blockWidth, int type){
		ArrayList<Node> path;
		Node[][] neededMap = null;
		int[] recivedIJ;
		//get the correct size map for the unit
		for(Node[][] checkMap: mapLibrary){
			if(checkMap.length == screenSize/blockWidth){
				neededMap = checkMap;
			}
		}

		//set the default current and target x and y coordinates
		int currentGridX = 0;
		int currentGridY = 0;

		int targetGridX = 0;
		int targetGridY = 0;

		//get the approximate grid location of the unit
		recivedIJ = getAproxXY(currentX, currentY, width, screenSize, blockWidth);
		currentGridX = recivedIJ[0];
		currentGridY = recivedIJ[1];

		//get the approximate grid location of the target
		recivedIJ = getAproxXY(targetX, targetY, width, screenSize, blockWidth);
		targetGridX = recivedIJ[0];
		targetGridY = recivedIJ[1];

		//get a path from the two grid locations on the correct map
		path = getPath(currentGridX, currentGridY, targetGridX, targetGridY, blockWidth, neededMap, type);

		//return the path made
		return path;
	}

	public float getHeuristic(Node node, int targX, int targY){
		//get the magnitude of the distance from the unit and the target for x and y
		int changeX = Math.abs(node.getGridX() - targX);
		int changeY = Math.abs(node.getGridY() - targY);

		//set the cost of moving horizontally and diagonally
		int d = 1;
		float d2 = 1.4f;

		//get the approximate movement distance of the target and the unit with a slight error added to prevent multiple paths having the same heuristic cost
		return (float) ((d * (changeX + changeY) + (d2 - 2 * d) * Math.min(changeX, changeY)) * (1 + 0.01));

		//fix, rest working as of 27/9/16
		//fixed as of 28/9/16
	}

	public float getMoveCost(Node A, Node B, int type){
		//check if the move form one node to another is a diagonal
		if(A.getGridX() != B.getGridX() && A.getGridY() != B.getGridY()){
			//check if the unit is an air unit
			if(type != 1){
				//if the unit is not an air unit then get the diagonal cost using the roughness of the node
				return (float) Math.sqrt(2*Math.pow(B.getMoveCost(), 2));
			} else {
				//otherwise return a default of 1.4 being the lowest movement code for a diagonal
				return 1.4f;
			}
		} else {
			//otherwise get the movement cost of the target node directly
			if(type != 1){
				return B.getMoveCost();
			} else {
				//if the unit is an air unit then return a default cost of 1 (the lowest possible cost)
				return 1;
			}
		}
		//working with new move costs as of 11/10/16
	}

	public boolean getValid(int x, int y, int blockWidth, Node[][] map, int type){
		//set the default as the move being valid
		boolean valid = true;
		//check if the x and y are actually on the screen as acceptable coordinates
		if((x < 0) || (y < 0) || (x > (screenSize/blockWidth) - 1) || (y > (screenSize/blockWidth) - 1)){
			valid = false;
		} 
		//if the position is on the map
		if(valid){
			//switch cases depending on the type of the unit
			switch(type){
			case 0:
				//land unit
				//if the map location is blocked, water or a chasm then set the movement as not valid
				if(map[x][y].getBlocked()){
					valid = false;
				} else if(map[x][y].getTileType() == 1){
					valid = false;
				} else if(map[x][y].getTileType() == 2){
					valid = false;
				}
				break;
			case 1:
				//air unit can move anywhere
				break;
			case 2:
				//if the tile is not water or a bridge then set the movement as invalid
				if(map[x][y].getTileType() != 2 && map[x][y].getTileType() != 3){
					valid = false;
				}
				//sea unit
				break;
			default:
				break;
			}
		}
		return valid;
	}

	public ArrayList<Node> getPath(int cX, int cY, int tX, int tY, int blockWidth, Node[][] map, int type){

		//working as of 30/9/16
		//create a new array of nodes to hold the new path
		ArrayList<Node> path = new ArrayList<Node>();

		//set the current node as the location of the unit currently
		Node current = map[cX][cY];
		current.setPrecursor(null);

		//clear both the open and closed lists to create a new path
		openList.clear();
		closedList.clear();

		//add the current node to the open list
		openList.add(current);

		//work ou the heuristic value of the current location
		current.setF(getHeuristic(current, tX, tY));

		//work out the actual route
		//while there are still nodes in the open list
		while(openList.size() > 0){

			//check if the target location is a position the unit can move to
			if(!getValid(tX, tY, blockWidth, map, type)){
				break;
			}
			//otherwise get the first node from the open list
			current = openList.getFirst();

			//loop through the open list to make sure the current node is the one with the lowest heuristic cost
			for(int c = 0; c < openList.size(); c ++){
				if(openList.get(c).getF() < current.getF()){
					current = openList.get(c);
				}
			}

			//check if the node being checked is the target node
			if(current == map[tX][tY]){
				break;
			}

			//otherwise move the current node from the open list to the closed list
			openList.remove(current);
			closedList.add(current);

			//look at the surrounding 8 nodes
			for(int x = -1; x < 2; x++){
				for(int y = -1; y < 2; y++){
					//ignore the current node location
					if(x == 0 && y == 0){
						continue;
					}

					//work out the location of the next adjacent node being checked
					int xTest = x + current.getGridX();
					int yTest = y + current.getGridY();
					//if the location is not a valid move for the unit then ignore it
					if(!getValid(xTest, yTest, blockWidth, map, type)){
						continue;
					}

					//add the node being tested to the list of scanned nodes
					Node testNode = map[xTest][yTest];
					scanned.add(testNode);

					//if the test node has already been looked at and added to the closed list, if it has then ignore it
					if(closedList.contains(testNode)){
						continue;
					}
					//work out the cost of moving to the node being tested from the cost of the current node added to the cost of moving to the node being tested
					float stepCost = current.getG() + getMoveCost(current, testNode, type);
					//if the node being tested isn't in the open list then add is to it
					if(!openList.contains(testNode)){
						openList.add(testNode);
					} else if(stepCost >= testNode.getG()){
						//if the node was already in the open list and the cost of that node is lower than the cost of moving there from the current node then continue to the next adjacent node needing to be tested
						continue;
					}

					//if the node was not in the open list then set the total cost, movement cost and heuristic value of the test node and set the node before it in the path to the current node
					testNode.setPrecursor(current);
					testNode.setG(stepCost);
					testNode.setH(getHeuristic(testNode, tX, tY));
					testNode.setF(testNode.getG() + testNode.getH());

				}
			}
		}
		//openList is now empty

		if(openList.size() == 0){
			//if the open list is empty indicate this to show a path could not be found
			System.out.println("Open List empty");
		}

		while(current.getPrecursor() != null){
			//rebuild the path by looking at the precursor nodes from the end of the path backwards
			path.add(current);
			current = current.getPrecursor();
		}

		//return the path created
		return path;
	}

	public void clearScanned(){
		//empty the list of scanned nodes
		scanned.clear();
	}
	public ArrayList<Node> getScanned(){
		//return the list of scanned nodes
		return scanned;
	}
}
