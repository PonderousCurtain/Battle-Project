package overworld;
import java.awt.Image;
import java.util.ArrayList;


public class Army{
	
	//declare the variables to be used throughout the class
	
	ArrayList<Unit> containedUnits;
	Image coverImage;
	Boolean selected;
	int x;
	int y;
	int maximumMovement;
	int movedThisTurn;
	int playerID;
	
	public Army(ArrayList<Unit> containedUnits, Image coverImage, int x, int y, int playerID){
		//initialise the main variables
		this.containedUnits = containedUnits;
		this.coverImage = coverImage;
		this.x = x;
		this.y = y;
		this.playerID = playerID;
		selected = false;
		movedThisTurn = 0;
		
		//update the maximum movement distance
		updateMaxMovement();
		
	}
	
	public void addUnits(ArrayList<Unit> unitsToAdd){
		//adds any units added to the units contained in the current army
		containedUnits.addAll(unitsToAdd);
	}
	
	public void updateMaxMovement(){
		//set the maximum movement to a large value temporarily
				maximumMovement = 10;
				//loop through the contained units 
				for(int i = 0; i < containedUnits.size(); i ++){
					//check if the next unit has a lower speed than the current maximum movement
					if(containedUnits.get(i).getSpeed() < maximumMovement){
						//if the speed is lower then lower the maximum movement to that value
						maximumMovement = containedUnits.get(i).getSpeed();
					}
				}
	}
	
	//add return statements for each of the values needed for the army
	public Image getImage(){
		return coverImage;
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
}
