package utilities;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;


public class Obstruction implements Serializable{
	//declare the variables for an obstruction
	int x;
	int y;
	int width;
	int height;
	Rectangle rect;
	Color color;
	int roughness;
	int type;
	Boolean isSettlement;
	int settlementID;
	
	public Obstruction(int x, int y, int width, int height, Color color, int roughness, int type, Boolean isSettlement, int settlementID){
		//initialise the variables with the values from the constructor
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		rect = new Rectangle(x, y, width, height);
		this.color = color;
		this.roughness = roughness;
		this.type = type;
		this.isSettlement = isSettlement;
		this.settlementID = settlementID;
	}
	
	//add methods and functions to set and return the variables for each obstruction
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public Rectangle getRect(){
		return rect;
	}
	public Color getColor(){
		return color;
	}
	public int getRoughness(){
		return roughness;
	}
	public int getTileType(){
		return type;
	}
	public int getSettlementID(){
		return settlementID;
	}
	public Boolean isSettlement(){
		return isSettlement;
	}
	
	public void setLocation(int newX, int newY){
		x = newX;
		y = newY;
	}
}
