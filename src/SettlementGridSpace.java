import java.awt.Color;


public class SettlementGridSpace {
	int value;
	Color color;
	int placedBuildingID;
	
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
	
	public void setColor(Color newColor){
		color = newColor;
	}
	public Color getColor(){
		return color;
	}
	
	public void setBuildingID(int newID){
		placedBuildingID = newID;
	}
	public int getPlacedBuildingID(){
		return placedBuildingID;
	}
}
