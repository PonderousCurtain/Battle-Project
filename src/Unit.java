import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Unit {
	int x;
	int y;
	int speed;
	double health;
	int agroRange;
	Rectangle rect;
	Color unitColor;
	Boolean selected;
	Boolean arrived;
	ArrayList<Node> path;
	ArrayList<Node> tempPath;
	int width;
	int type;
	Boolean agro;
	Boolean attacking;
	Unit sparringPartner;
	Unit assisting;
	int attack;
	double maxHealth;
	
	Image image;
	
	int targetX;
	int targetY;

	public Unit(int x, int y, int speed, int health, Color unitColor, int width, int type, int agroRange, int attack, Image image){
		this.x = x;
		this.y = y;
		targetX = x;
		targetY = y;
		this.speed = speed;
		this.health = health;
		this.image = image;
		maxHealth = health;
		rect = new Rectangle(x, y, width, width);
		this.unitColor = unitColor;
		selected = false;
		arrived = true;
		this.width = width;
		this.type = type;
		this.agroRange = agroRange;
		agro = false;
		attacking = false;
		this.attack = attack;

		path = new ArrayList<Node>();
		tempPath = new ArrayList<Node>();
		sparringPartner = null;
	}

	public int getX(){
		return x;
	}
	
	public Image getImage(){
		return image;
	}
	public void setX(int x){
		rect.setBounds(new Rectangle(x, y, width, width));
		this.x = x;
	}

	public int getY(){
		return y;
	}
	public void setY(int y){
		rect.setBounds(new Rectangle(x, y, width, width));
		this.y = y;
	}
	
	public void setAssisting(Unit newAssisting){
		assisting = newAssisting;
	}
	
	public Unit getAssisting(){
		return assisting;
	}
	public Boolean getAttacking(){
		return attacking;
	}
	public void setAttacking(Boolean newAttacking){
		attacking = newAttacking;
	}
	
	public int getAttack(){
		return attack;
	}
	public boolean deadFromDamage(int damage){
		health = health - damage;
		if(health < 1){
			return true;
		} else {
			return false;
		}
	}
	
	public void setAgro(Boolean newAgro){
		agro = newAgro;
	}
	public Boolean getAgro(){
		return agro;
	}
	
	public Unit getSparringPartner(){
		return sparringPartner;
	}
	public void setSparringPartner(Unit newSparringPartner){
		sparringPartner = newSparringPartner;
	}
	public void killSparringPartner(){
		sparringPartner = null;
	}
	
	public int getAgroRange(){
		return agroRange;
	}
	public void setAgroRange(int newAgroRange){
		agroRange = newAgroRange;
	}

	public int getType(){
		return type;
		//land air or sea 0, 1, or 2 respectivly
	}
	public int getSpeed(){
		return speed;
	}
	public void setSpeed(int speed){
		this.speed = speed;
	}

	public double getHealth(){
		return health;
	}
	public double getMaxHealth(){
		return maxHealth;
	}
	public void setHealth(int health){
		this.health = health;
	}

	public Rectangle getRect(){
		return rect;
	}
	public void setRect(int newX, int newY, int newWidth, int newHeight){
		rect.setBounds(new Rectangle(newX, newY, newWidth, newHeight));
	}

	public Color getColor(){
		return unitColor;
	}
	public Color getSelectedColor(){
		return new Color(255 - unitColor.getRed(), 255 - unitColor.getGreen(), 255 - unitColor.getBlue());
	}
	public void setColor(Color newColor){
		unitColor = newColor;
	}

	public void setSelected(Boolean newSelected){
		selected = newSelected;
	}
	public Boolean getSelected(){
		return selected;
	}

	public void setArrived(Boolean newArrived){
		arrived = newArrived;
	}
	public Boolean getArrived(){
		return arrived;
	}

	public ArrayList<Node> getPath(){
		return path;
	}
	public Node getPathNext(){
		return path.get(path.size() - 1);
	}public void removePath(){
		path.clear();
	}
	public void removeLast(){
		path.remove(path.size() - 1);
	}
	public void setPath(ArrayList<Node> newPath){
		path = newPath;
	}
	
	public ArrayList<Node> getTempPath(){
		return tempPath;
	}
	public Node getTempPathNext(){
		return tempPath.get(tempPath.size() - 1);
	}public void removeTempPath(){
		tempPath.clear();
	}
	public void removeTempLast(){
		tempPath.remove(tempPath.size() - 1);
	}
	public void setTempPath(ArrayList<Node> newPath){
		tempPath = newPath;
	}
	
	public void emptyTempPath(){
		tempPath.clear();
	}
	public void emptyPath(){
		path.clear();
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setTarget(int targX, int targY){
		targetX = targX;
		targetY = targY;
	}
	public int getTargetX(){
		return targetX;
	}
	public int getTargetY(){
		return targetY;
	}
	
	public double getHealthRatio(){
		double healthRatio = health/maxHealth;
		return healthRatio;
	}

}
