package battle;
import java.awt.Rectangle;


public class Node {
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
		this.x = x;
		this.y = y;
		this.width = width;
		blocked = false;
		rect = new Rectangle(x*width, y*width, width, width);
		type = 0;
		moveCost = 1;
	}
	
	public float getF(){
		return f;
	}
	public void setF(float newF){
		f = newF;
	}
	
	public float getG(){
		return g;
	}
	public void setG(float newG){
		g = newG;
	}
	
	public float getH(){
		return h;
	}
	public void setH(float newH){
		h = newH;
	}
	
	public boolean getBlocked(){
		return blocked;
	}
	public void setBlocked(boolean newBlocked){
		blocked = newBlocked;
	}
	
	public int getGridX(){
		return x;
	}
	public int getGridY(){
		return y;
	}
	
	public int getActualX(){
		return x*width;
	}
	public int getActualY(){
		return y*width;
	}
	
	public void setPrecursor(Node newPrecursor){
		precursor = newPrecursor;
	}
	public Node getPrecursor(){
		return precursor;
	}
	public Rectangle getRect(){
		return rect;
	}
	public int getMoveCost(){
		return moveCost;
	}
	public void setMoveCost(int newMoveCost){
		moveCost = newMoveCost;
	}
	public int getTileType(){
		return type;
	}
	public void setTileType(int newType){
		type = newType;
	}
}
