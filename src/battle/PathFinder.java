package battle;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

import utilities.Obstruction;



//Path finding class working as of 4/10/16

public class PathFinder {
	ArrayList<Node[][]> mapLibrary;
	Obstruction[][] staticBlockages;
	int screenSize;
	LinkedList<Node> closedList;
	LinkedList<Node> openList;
	ArrayList<Node> scanned;

	public PathFinder(Obstruction[][] blockages, int screenSize){
		this.screenSize = screenSize;
		closedList = new LinkedList<Node>();
		openList = new LinkedList<Node>();
		scanned = new ArrayList<Node>();
		mapLibrary = new ArrayList<Node[][]>();

		staticBlockages = blockages;
	}

	public void setUpMap(int blockWidth){
		Boolean alreadyMade = false;
		for(Node[][] nextMap: mapLibrary){
			if(nextMap.length == screenSize/blockWidth){
				alreadyMade = true;
			}
		}
		if(alreadyMade == false){
			Node[][] grid = new Node[screenSize/blockWidth][screenSize/blockWidth];
			for(int i = 0; i < screenSize; i += blockWidth){
				for(int j = 0; j < screenSize; j += blockWidth){
					grid[i/blockWidth][j/blockWidth] = new Node(i/blockWidth, j/blockWidth, blockWidth);
					
					grid[i/blockWidth][j/blockWidth].setMoveCost(staticBlockages[i/blockWidth][j/blockWidth].getRoughness());
					grid[i/blockWidth][j/blockWidth].setTileType(staticBlockages[i/blockWidth][j/blockWidth].getTileType());
					if(grid[i/blockWidth][j/blockWidth].getMoveCost() == 0){
						grid[i/blockWidth][j/blockWidth].setBlocked(true);
					}
				}
			}
			mapLibrary.add(grid);
		}
	}
	
	
	public int[] getAproxXY(int x, int y, int width, int screenSize, int blockWidth){
		int[] ijStore = new int[2];
		for(int i = 0; i < screenSize/blockWidth; i++){
			for(int j = 0; j < screenSize/blockWidth; j++){
				if(new Rectangle(x, y, width, width).intersects(new Rectangle(i * blockWidth, j * blockWidth, blockWidth, blockWidth))){
					ijStore[0] = i;
					ijStore[1] = j;
					break;
				}
			}
		}
		return ijStore;
	}

	public ArrayList<Node> pathFind(int currentX, int currentY, int width, int targetX, int targetY, int blockWidth, int type){
		ArrayList<Node> path;
		Node[][] neededMap = null;
		int[] recivedIJ;
		for(Node[][] checkMap: mapLibrary){
			if(checkMap.length == screenSize/blockWidth){
				neededMap = checkMap;
			}
		}

		int currentGridX = 0;
		int currentGridY = 0;

		int targetGridX = 0;
		int targetGridY = 0;
		
		recivedIJ = getAproxXY(currentX, currentY, width, screenSize, blockWidth);
		currentGridX = recivedIJ[0];
		currentGridY = recivedIJ[1];
		
		recivedIJ = getAproxXY(targetX, targetY, width, screenSize, blockWidth);
		targetGridX = recivedIJ[0];
		targetGridY = recivedIJ[1];

		path = getPath(currentGridX, currentGridY, targetGridX, targetGridY, blockWidth, neededMap, type);

		return path;
	}

	public float getHeuristic(Node node, int targX, int targY){
		int changeX = Math.abs(node.getGridX() - targX);
		int changeY = Math.abs(node.getGridY() - targY);

		int d = 1;
		float d2 = 1.4f;

		return (float) ((d * (changeX + changeY) + (d2 - 2 * d) * Math.min(changeX, changeY)) * (1 + 0.01));

		//fix, rest working as of 27/9/16
		//fixed as of 28/9/16
	}

	public float getMoveCost(Node A, Node B, int type){
		if(A.getGridX() != B.getGridX() && A.getGridY() != B.getGridY()){
			if(type != 1){
				return (float) Math.sqrt(2*Math.pow(B.getMoveCost(), 2));
			} else {
				return 1.4f;
			}
		} else {
			if(type != 1){
				return B.getMoveCost();
			} else {
				return 1;
			}
		}
		//working with new move costs as of 11/10/16
	}

	public boolean getValid(int x, int y, int blockWidth, Node[][] map, int type){
		boolean valid = true;
		if((x < 0) || (y < 0) || (x > (screenSize/blockWidth) - 1) || (y > (screenSize/blockWidth) - 1)){
			valid = false;
		} 
		if(valid){
			switch(type){
			case 0:
				//land unit
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
		ArrayList<Node> path = new ArrayList<Node>();

		Node current = map[cX][cY];
		current.setPrecursor(null);

		openList.clear();
		closedList.clear();

		openList.add(current);

		current.setF(getHeuristic(current, tX, tY));

		//work out the actual route
		while(openList.size() > 0){

			if(!getValid(tX, tY, blockWidth, map, type)){
				break;
			}
			current = openList.getFirst();


			for(int c = 0; c < openList.size(); c ++){
				if(openList.get(c).getF() < current.getF()){
					current = openList.get(c);
				}
			}

			if(current == map[tX][tY]){
				break;
			}

			openList.remove(current);
			closedList.add(current);

			for(int x = -1; x < 2; x++){
				for(int y = -1; y < 2; y++){
					if(x == 0 && y == 0){
						continue;
					}

					int xTest = x + current.getGridX();
					int yTest = y + current.getGridY();
					if(!getValid(xTest, yTest, blockWidth, map, type)){
						continue;
					}

					Node testNode = map[xTest][yTest];
					scanned.add(testNode);

					if(closedList.contains(testNode)){
						continue;
					}
					float stepCost = current.getG() + getMoveCost(current, testNode, type);
					if(!openList.contains(testNode)){
						openList.add(testNode);
					} else if(stepCost >= testNode.getG()){
						continue;
					}

					testNode.setPrecursor(current);
					testNode.setG(stepCost);
					testNode.setH(getHeuristic(testNode, tX, tY));
					testNode.setF(testNode.getG() + testNode.getH());

				}
			}
		}
		//openList is now empty

		if(openList.size() == 0){
			System.out.println("Open List empty");
		}

		if(current.getPrecursor() == null){ 
			//System.out.println("Failed to make path");
		}
		while(current.getPrecursor() != null){
			path.add(current);
			current = current.getPrecursor();
		}

		return path;
	}

	public void clearScanned(){
		scanned.clear();
	}
	public ArrayList<Node> getScanned(){
		return scanned;
	}
}
