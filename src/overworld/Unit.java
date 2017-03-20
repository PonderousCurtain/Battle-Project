package overworld;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import battle.Node;


public class Unit implements Serializable{
	//declare all the variables for a unit
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

	String image;

	int targetX;
	int targetY;

	public Unit(int x, int y, int speed, int health, int width, String typeString, int agroRange, int attack, String image){
		//initialise the variables for this unit as the ones given in the constructor
		this.x = x;
		this.y = y;
		targetX = x;
		targetY = y;
		this.speed = speed;
		this.health = health;
		this.image = image;
		maxHealth = health;
		rect = new Rectangle(x, y, width, width);
		selected = false;
		arrived = true;
		this.width = width;

		//change the type from a string to an int form
		switch(typeString){
		case "land":
			type = 0;
			break;
		case "air":
			type = 1;
			break;
		case "sea":
			type = 2;
			break;
		default:
			break;
		}

		this.agroRange = agroRange;
		agro = false;
		attacking = false;
		this.attack = attack;

		path = new ArrayList<Node>();
		tempPath = new ArrayList<Node>();
		sparringPartner = null;
	}

	//add methods and functions to set and return the values of the unit
	public int getX(){
		return x;
	}

	public Image getImage(){
		//return new ImageIcon("TestUnitOne.jpg").getImage();
		return new ImageIcon(image).getImage();
	}
	public String getImageString(){
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
		//reduce the health of the unit by the damage given and check if the unit health has decreased bellow 0, return true if the unit has lost all its health, otherwise return true
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
		//land air or sea 0, 1, or 2 respectively
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
	public Color getSelectedColor(){
		return Color.RED;
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
	}
	public void removePath(){
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
		//get the radio of current health to maximum health for the unit
		double healthRatio = health/maxHealth;
		return healthRatio;
	}

}
