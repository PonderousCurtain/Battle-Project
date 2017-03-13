package battle;
import java.awt.Rectangle;


public class Node {
	//declare the variables needed for a node
	float f;
	float g;
	int x;
	int y;
	int moveCost;
	float h;
	Node precursor;
	Boolean blocked;
	int width;
	Rectangle rect;
	int type;
	
	public Node(int x, int y, int width){
		//set the variables of the node to the variables given in the constructor
		this.x = x;
		this.y = y;
		this.width = width;
		blocked = false;
		rect = new Rectangle(x*width, y*width, width, width);
		type = 0;
		moveCost = 1;
	}
	
	public float getF(){
		//return the total cost of the node
		return f;
	}
	public void setF(float newF){
		//set the total cost of the node
		f = newF;
	}
	
	public float getG(){
		//return the cost of moving to this node
		return g;
	}
	public void setG(float newG){
		//set the cost of moving to this node
		g = newG;
	}
	
	public float getH(){
		//return the heuristic cost of this node
		return h;
	}
	public void setH(float newH){
		//set the heuristic cost of this node
		h = newH;
	}
	
	public boolean getBlocked(){
		//return whether this node is blocked
		return blocked;
	}
	public void setBlocked(boolean newBlocked){
		//set whether this node is blocked
		blocked = newBlocked;
	}
	
	public int getGridX(){
		//return the grid x location of the node
		return x;
	}
	public int getGridY(){
		//return the grid y location of the node
		return y;
	}
	
	public int getActualX(){
		//return the actual x coordinate of the node
		return x*width;
	}
	public int getActualY(){
		//return the actual y coordinate of the node
		return y*width;
	}
	
	public void setPrecursor(Node newPrecursor){
		//set the node before this node in a path
		precursor = newPrecursor;
	}
	public Node getPrecursor(){
		//get the node before this node in a path
		return precursor;
	}
	public Rectangle getRect(){
		//get the rectangle with the bound of this node
		return rect;
	}
	public int getMoveCost(){
		//return the movement cost (roughness) of this node
		return moveCost;
	}
	public void setMoveCost(int newMoveCost){
		//set the movement cost of this node
		moveCost = newMoveCost;
	}
	public int getTileType(){
		//return the type of terrain on this node
		return type;
	}
	public void setTileType(int newType){
		//set the terrain type of this node
		type = newType;
	}
}
