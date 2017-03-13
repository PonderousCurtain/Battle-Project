package battle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class EventManager {
	//declare the map display that this manager will interact with
	MapDisplay mD;

	public EventManager(MapDisplay mD){
		//set the map display this class will use as the one passed to it through the constructor
		this.mD = mD;
	}

	public void mouseDragged(MouseEvent event){
		//pass a mouse dragged event to the map display
		mD.mouseDraggedInput(event);
	}
	public void mouseClicked(MouseEvent event){
		//pass a mouse clicked event to the map display
		mD.mouseClickedInput(event);
	}
	public void mouseReleased(MouseEvent event){
		//pass a mouse released event to the map display
		mD.mouseReleasedInput(event);
	}
	public void keyPressed(KeyEvent event){
		//pass a key pressed even to the map display
		mD.keyPressedInput(event);
	}
	public void updateAgroRangeForMapDisplay(int newAgroRange){
		//Update the aggression range of selected units as the value given (from the information panel)
		mD.updateAgroRangeOfSelected(newAgroRange);
	}
	public void updateTime(float newIndex){
		//update the game speed as the value given (from the information panel)
		mD.giveUpdateTimeCommand(newIndex);
	}
	public void retreatFromBattle(){
		//tell the map display to end the battle with the result of a retreat
		mD.endBattle(true);
	}
}
