package settlement;
import java.awt.Color;
import java.awt.Image;


public class SettlementGridSpace {
	int value;
	Color color;
	int placedBuildingID;
	Image image;
	
	public SettlementGridSpace(int value, Color color){
		this.value = value;
		this.color = color;
		placedBuildingID = -1;
	}
	
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
