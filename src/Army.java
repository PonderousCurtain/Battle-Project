import java.awt.Image;
import java.util.ArrayList;


public class Army{
	
	ArrayList<Unit> containedUnits;
	Image coverImage;
	Boolean selected;
	int x;
	int y;
	
	public Army(ArrayList<Unit> containedUnits, Image coverImage, int x, int y){
		this.containedUnits = containedUnits;
		this.coverImage = coverImage;
		this.x = x;
		this.y = y;
		selected = false;
	}
	
	public Image getImage(){
		return coverImage;
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
	public int getY() {
		return y;
	}
}
