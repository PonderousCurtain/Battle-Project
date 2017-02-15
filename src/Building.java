import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Building implements Cloneable{
	String name;
	ArrayList<int[]> size;
	int placedIndex;
	int cost;
	int upkeep;
	
	public Building(String name, ArrayList<int[]> size, int cost, int upkeep){
		this.name = name;
		this.size = size;
		placedIndex = 0;
		this.cost = cost;
		this.upkeep = upkeep;
	}
	
	public Building(Building copyBuilding){
		name = copyBuilding.getName();
		placedIndex = copyBuilding.getPlacedIndex();
		size = new ArrayList<int[]>(copyBuilding.getTakenBlocks());
	}
	
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
		placedIndex = newIndex;
	}
}
