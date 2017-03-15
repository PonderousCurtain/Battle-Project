package mapmaker;
import java.awt.Color;


public class MMEventManager{
	//declare the classes that the event manager will communicate with
	MMCanvasUI cUI;
	MMPanelManager MManager;
	
	public MMEventManager(MMCanvasUI cUI, MMPanelManager MManager){
		//set the classes used in this class to those given in the constructor
		this.cUI = cUI;
		this.MManager = MManager;
	}

	public void updateBrushSize(int newSize){
		//set the brush size in the canvas display to the given colour
		cUI.updateBrushSize(newSize);
	}
	public void updateColor(Color newColor){
		//set the colour in the canvas display to the given colour
		cUI.updateColor(newColor);
	}
	public void updateRoughness(int newRoughness){
		//set the roughness value in the canvas display to the given value
		cUI.updateRoughness(newRoughness);
	}
	public void updateTileType(int newTileType){
		//set the terrain type of the canvas display to the new value
		cUI.updateTileType(newTileType);
	}
	public void updatePanel(int roughness, Color color, int tileType, int brushSize){
		//update the values shown on the control panel to the new values given
		MManager.getCP().updateValues(roughness, color, tileType, brushSize);
	}
	public void updateScale(int newMapSize){
		//set the scaling of the canvas display to the given value
		cUI.changeGridSize(newMapSize);
	}
	public void changeMode(Boolean mode){
		//change whether the user is placing settlements or not on the canvas display
		cUI.placingSettlements(mode);
	}
}

