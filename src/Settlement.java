import java.awt.Color;
import java.util.ArrayList;


public class Settlement{
	ArrayList<Building> avalibleBuildings;
	int gridSize;
	SettlementGridSpace[][] gridArray;
	ArrayList<Building> placedBuildings;
	int overallProffit;
	int ID;
	
	public Settlement(int gridSize, int ID){
		this. ID = ID;
		avalibleBuildings = new ArrayList<Building>();
		this.gridSize = gridSize;
		
		placedBuildings = new ArrayList<Building>();
		gridArray = new SettlementGridSpace[gridSize][gridSize];
		
		for(int xCount = 0; xCount < gridSize; xCount ++){
			for(int yCount = 0; yCount < gridSize; yCount++){
				gridArray[xCount][yCount] = new SettlementGridSpace(0, Color.GRAY);
			}
		}
		
		
		Color block1Color = Color.ORANGE;
		ArrayList<int[]> blocks1 = new ArrayList<int[]>();
		blocks1.add(new int[] {0, -1});
		blocks1.add(new int[] {0, 0});
		blocks1.add(new int[] {0, 1});
		blocks1.add(new int[] {1, 0});
		avalibleBuildings.add(new Building("blueprint", blocks1, block1Color, -5, 20));
		
		Color block2Color = Color.MAGENTA;
		ArrayList<int[]> blocks2 = new ArrayList<int[]>();
		blocks2.add(new int[] {-1, -1});
		blocks2.add(new int[] {-1, 0});
		blocks2.add(new int[] {0, 0});
		blocks2.add(new int[] {1, 0});
		avalibleBuildings.add(new Building("blueprint2", blocks2, block2Color, -10, 40));
		
	}
	
	public int getGridSize(){
		return gridSize;
	}
	public void updateProffit(){
		overallProffit = 0;
		for(int i = 0; i < placedBuildings.size(); i ++){
			overallProffit += placedBuildings.get(i).getCost();
		}
	}
	
	public int getProffit(){
		updateProffit();
		return overallProffit;
	}
	public SettlementGridSpace[][] getGrid(){
		return gridArray;
	}
	public void setGrid(int gridX, int gridY, int newValue){
		gridArray[gridX][gridY].setValue(newValue);
	}
	public int getID(){
		return ID;
	}
	public ArrayList<Building> getBuildings(){
		return avalibleBuildings;
	}
	public ArrayList<Building> getPlacedBuildings(){
		return placedBuildings;
	}
}
