package settlement;
import java.awt.Color;
import java.awt.Image;


public class SettlementGridSpace {
	//declare the variables related to the settlement grid space
	int value;
	Color color;
	int placedBuildingID;
	Image image;
	
	public SettlementGridSpace(int value, Color color){
		//set the values to through given in the constructor
		this.value = value;
		this.color = color;
		placedBuildingID = -1;
	}
	
	//add methods and functions to set and return the values of the settlement grid space
	public void setValue(int newValue){
		value= newValue;
	}
	public int getValue(){
		return value;
	}
	
	public void setImage(Image newImage){
		image = newImage;
	}
	public Image getPlacedImage(){
		return image;
	}
	
	public void setBuildingID(int newID){
		placedBuildingID = newID;
	}
	public int getPlacedBuildingID(){
		return placedBuildingID;
	}
}
