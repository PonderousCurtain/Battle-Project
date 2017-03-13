package settlement;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Building implements Cloneable{
	//declare the values used for a building object
	String name;
	ArrayList<int[]> size;
	int placedIndex;
	int cost;
	int upkeep;
	
	public Building(String name, ArrayList<int[]> size, int cost, int upkeep){
		//Initialise the variables with the values given in the constructor
		this.name = name;
		this.size = size;
		placedIndex = 0;
		this.cost = cost;
		this.upkeep = upkeep;
	}
	
	public Building(Building copyBuilding){
		//initialise the building as a copy of another building
		name = copyBuilding.getName();
		placedIndex = copyBuilding.getPlacedIndex();
		size = new ArrayList<int[]>(copyBuilding.getTakenBlocks());
	}
	
	//add functions to return the values of the building
	public int getCost(){
		return cost;
	}
	
	public int getUpkeep(){
		return upkeep;
	}
	
	public String getName(){
		return name;
	}
	public ArrayList<int[]> getTakenBlocks(){
		return size;
	}
	public Image getImage(){
		return new ImageIcon(name + ".jpg").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
	}
	public Image getPlacedImage(){
		return new ImageIcon(name + "Placed.jpg").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
	}
	public int getPlacedIndex(){
		return placedIndex;
	}
	public void setPlacedIndex(int newIndex){
		//set the placed index value to the new value passed to this class
		placedIndex = newIndex;
	}
}
