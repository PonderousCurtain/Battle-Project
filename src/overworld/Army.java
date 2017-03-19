package overworld;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Army implements Serializable{
	
	//declare the variables to be used throughout the class
	
	ArrayList<Unit> containedUnits;
	//Image coverImage;
	Boolean selected;
	int x;
	int y;
	int maximumMovement;
	int movedThisTurn;
	int playerID;
	
	public Army(ArrayList<Unit> containedUnits, Image coverImage, int x, int y, int playerID){
		//initialise the main variables
		this.containedUnits = containedUnits;
		//this.coverImage = coverImage;
		this.x = x;
		this.y = y;
		this.playerID = playerID;
		selected = false;
		movedThisTurn = 0;
		
	}
	
	public void addUnits(ArrayList<Unit> unitsToAdd){
		//adds any units added to the units contained in the current army
		containedUnits.addAll(unitsToAdd);
	}
	
	public void updateMaxMovement(int newValue){
		//set the maximum movement value to the new value
		maximumMovement = newValue;
	}
	
	//add return statements for each of the values needed for the army
	public Image getImage(){
		return new ImageIcon("TestUnitOne.jpg").getImage();
	}
	
	public int getPlayerIndex(){
		return playerID;
	}
	
	public int getPotentialMovement(){
		return maximumMovement - movedThisTurn;
	}
	
	public void moveSquares(int newMove){
		movedThisTurn += newMove;
	}
	
	public void resetTurnMovement(){
		movedThisTurn = 0;
	}
	
	public Boolean isSelected(){
		return selected;
	}
	
	public void setSelected(Boolean isSelected){
		selected = isSelected;
	}

	public int getX() {
		return x;
	}
	public void setX(int newX){
		x = newX;
	}
	public int getY() {
		return y;
	}
	public void setY(int newY){
		y = newY;
	}
	public ArrayList<Unit> getUnits(){
		return containedUnits;
	}
	public void replaceUnits(ArrayList<Unit> newUnits){
		containedUnits = new ArrayList<Unit>();
		containedUnits.addAll(newUnits);
	}
}
