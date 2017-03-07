package overworld;
import java.util.ArrayList;


public class Player {
	ArrayList<Unit> unitList;
	String playerName;
	int[] ownedTeritory;
	
	public Player(ArrayList<Unit> unitList, String playerName/*, int[] ownedTeritory*/){
		this.unitList = unitList;
		this.playerName = playerName;
		this.ownedTeritory = ownedTeritory;
	}
	
	public ArrayList<Unit> getControlledUnits(){
		return unitList;
	}
	
	public int[] getTeritory(){
		return ownedTeritory;
	}
	
	public String getName(){
		return playerName;
	}
	
	public void updateUnits(ArrayList<Unit> newUnits){
		unitList = newUnits;
	}
	public void updateTeritory(int[] newTeritory){
		ownedTeritory = newTeritory;
	}
	public void updateName(String newName){
		playerName = newName;
	}
	
	public void addUnit(Unit newUnit){
		unitList.add(newUnit);
	}
}
