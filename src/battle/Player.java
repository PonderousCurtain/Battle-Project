package battle;
import java.util.ArrayList;

import overworld.Unit;


public class Player {
	//set up the variables for a player
	ArrayList<Unit> unitList;
	String playerName;
	
	public Player(ArrayList<Unit> unitList, String playerName){
		//initialise the values from the given player statistics
		this.unitList = unitList;
		this.playerName = playerName;
	}
	
	public ArrayList<Unit> getControlledUnits(){
		//return the list of controlled units
		return unitList;
	}
	
	public String getName(){
		//return the player name
		return playerName;
	}
	
	public void updateUnits(ArrayList<Unit> newUnits){
		//replace the controlled units with the newly given units
		unitList = newUnits;
	}
	public void updateName(String newName){
		//update the name of the player with the newly given name
		playerName = newName;
	}
	
	public void addUnit(Unit newUnit){
		//add a unit the list of units the player uses
		unitList.add(newUnit);
	}
}