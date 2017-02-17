import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class EventManager {
	MapDisplay mD;

	public EventManager(MapDisplay mD){
		this.mD = mD;
	}

	public void mouseDragged(MouseEvent event){
		mD.mouseDraggedInput(event);
	}
	public void mouseClicked(MouseEvent event){
		mD.mouseClickedInput(event);
	}
	public void mouseReleased(MouseEvent event){
		mD.mouseReleasedInput(event);
	}
	public void keyPressed(KeyEvent event){
		mD.keyPressedInput(event);
	}
	public void updateAgroRangeForMapDisplay(int newAgroRange){
		mD.updateAgroRangeOfSelected(newAgroRange);
	}
	public void updateTime(float newIndex){
		mD.giveUpdateTimeCommand(newIndex);
	}
}
