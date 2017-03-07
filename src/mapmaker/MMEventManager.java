package mapmaker;
import java.awt.Color;


public class MMEventManager{
	MMCanvasUI cUI;
	MMPanelManager MManager;
	public MMEventManager(MMCanvasUI cUI, MMPanelManager MManager){
		this.cUI = cUI;
		this.MManager = MManager;
	}

	public void updateBrushSize(int newSize){
		cUI.updateBrushSize(newSize);
	}
	public void updateColor(Color newColor){
		cUI.updateColor(newColor);
	}
	public void updateRoughness(int newRoughness){
		cUI.updateRoughness(newRoughness);
	}
	public void updateTileType(int newTileType){
		cUI.updateTileType(newTileType);
	}
	public void updatePanel(int roughness, Color color, int tileType, int brushSize){
		MManager.getCP().updateValues(roughness, color, tileType, brushSize);
	}
	public void updateScale(int newMapSize){
		cUI.changeGridSize(newMapSize);
	}
	public void changeMode(Boolean mode){
		cUI.placingSettlements(mode);
	}
}

