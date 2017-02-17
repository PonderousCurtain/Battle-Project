import java.awt.Rectangle;
import java.util.ArrayList;


public class MovementManager {
	ArrayList<Unit> unitList;
	Obstruction[][] staticBlockages;

	public MovementManager(Obstruction[][] staticBlockages){
		this.staticBlockages = staticBlockages;
		unitList = new ArrayList<Unit>();
	}

	public void checkArrivedAtNode(Unit checkUnit, Node checkNode, int pathType){
		if(checkUnit.getX() == checkNode.getActualX() && checkUnit.getY() == checkNode.getActualY()){
			if(pathType == 0){
				checkUnit.removeLast();
			} else {
				checkUnit.removeTempLast();
			}
		}
		if(pathType == 0 && checkUnit.getPath().size() == 0){
			checkUnit.setArrived(true);
		}

	}

	public void addUnitList(ArrayList<Unit> newListToAdd){
		unitList.addAll(newListToAdd);
	}
	public void emptyUnitList(){
		unitList.clear();
	}
	public void removeUnit(Unit toRemove){
		for(int i = 0; i < unitList.size(); i ++){
			if(unitList.get(i) == toRemove){
				unitList.remove(i);
				i--;
			}
		}
	}

	public void moveStep(Unit nextUnit){
		Node targetNode = null;
		int pathType = 0;
		if(nextUnit.getTempPath().size() > 0){
			pathType = 1;
			targetNode = nextUnit.getTempPathNext();
		} else if(nextUnit.getPath().size() > 0){
			targetNode = nextUnit.getPathNext();
		}


		if(moveXCheck(targetNode.getActualX(), nextUnit)){
			if(nextUnit.getX() < targetNode.getActualX()){
				nextUnit.setX(nextUnit.getX() + nextUnit.getSpeed());
			} else if (nextUnit.getX() > targetNode.getActualX()){
				nextUnit.setX(nextUnit.getX() - nextUnit.getSpeed());
			}
		} else {
		}

		if(moveYCheck(targetNode.getActualY(), nextUnit)){
			if(nextUnit.getY() < targetNode.getActualY()){
				nextUnit.setY(nextUnit.getY() + nextUnit.getSpeed());
			} else if (nextUnit.getY() > targetNode.getActualY()){
				nextUnit.setY(nextUnit.getY() - nextUnit.getSpeed());
			}
		}


		checkArrivedAtNode(nextUnit, targetNode, pathType);
	}

	public Boolean moveXCheck(int targetX, Unit checkUnit){
		//working as of 16/9/16
		//amended as of 11/10/16
		Boolean moveXCheck = true;

		Rectangle checkTangleX = new Rectangle(checkUnit.getRect());

		if(checkUnit.getX() < targetX){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth() + checkUnit.getSpeed(), (int)checkTangleX.getHeight()));
		} else if(checkUnit.getX() > targetX){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX() - checkUnit.getSpeed(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth() + checkUnit.getSpeed(), (int)checkTangleX.getHeight()));
		}

		for(Unit presentUnit: unitList){
			if(checkTangleX.intersects(presentUnit.getRect()) && checkUnit != presentUnit){
				if(presentUnit.getType() == checkUnit.getType()){
					moveXCheck = false;
				}
			}
		}

		int currentXCoord = (int) Math.floor(checkTangleX.getX()/checkUnit.getWidth());
		int currentYCoord = (int) Math.floor(checkTangleX.getY()/checkUnit.getWidth());

		int height = 1;
		if(checkUnit.getY() % checkUnit.getWidth() != 0){
			height = 2;
		}

		for(int cX = 0; cX < 2; cX ++){
			for(int cY = 0; cY < height; cY ++){
				if(currentXCoord + cX < 100 && currentYCoord + cY < 100){
					switch(checkUnit.getType()){
					case 0:
						if((staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 0 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3) || staticBlockages[currentXCoord + cX][currentYCoord + cY].getRoughness() == 0){
							moveXCheck = false;
						}
						//land
						break;
					case 1:
						//air can move anywhere
						break;
					case 2:
						if(staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 2 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3){
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


		return moveXCheck;
	}

	public Boolean moveYCheck(int targetY, Unit checkUnit){
		//working as of 16/9/16
		Boolean moveYCheck = true;

		Rectangle checkTangleX = new Rectangle(checkUnit.getRect());

		if(checkUnit.getY() < targetY){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY(), (int)checkTangleX.getWidth(), (int)checkTangleX.getHeight() + checkUnit.getSpeed()));
		} else if(checkUnit.getY() > targetY){
			checkTangleX.setBounds(new Rectangle((int)checkTangleX.getX(), (int)checkTangleX.getY() - checkUnit.getSpeed(), (int)checkTangleX.getWidth(), (int)checkTangleX.getHeight() + checkUnit.getSpeed()));
		}

		for(Unit presentUnit: unitList){
			if(checkTangleX.intersects(presentUnit.getRect()) && checkUnit != presentUnit){
				if(presentUnit.getType() == checkUnit.getType()){
					moveYCheck = false;
				}
			}
		}
		int currentXCoord = (int) Math.floor(checkTangleX.getX()/checkUnit.getWidth());
		int currentYCoord = (int) Math.floor(checkTangleX.getY()/checkUnit.getWidth());

		int width = 1;
		if(checkUnit.getX() % checkUnit.getWidth() != 0){
			width = 2;
		}

		for(int cX = 0; cX < width; cX ++){
			for(int cY = 0; cY < 2; cY ++){
				switch(checkUnit.getType()){
				case 0:
					if((staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 0 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3) || staticBlockages[currentXCoord + cX][currentYCoord + cY].getRoughness() == 0){
						moveYCheck = false;
					}
					//land
					break;
				case 1:
					//air can move anywhere
					break;
				case 2:
					if(staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 2 && staticBlockages[currentXCoord + cX][currentYCoord + cY].getTileType() != 3){
						moveYCheck = false;
					}
					//sea
					break;
				default:
					break;
				}
			}
		}
		return moveYCheck;
	}
}
