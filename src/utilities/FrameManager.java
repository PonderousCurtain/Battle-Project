package utilities;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import battle.EventManager;
import mainmenu.MenuManager;
import mapmaker.MMPanelManager;
import overworld.OverworldManager;
import settlement.SettlementManager;


public class FrameManager implements MouseListener, MouseMotionListener, KeyListener{
	//declare the variables and classes that the frame will need access to
	JFrame frame;
	EventManager eM;
	CardManager cM;
	OverworldManager oM;
	SettlementManager sM;
	MenuManager menuPanel;
	MMPanelManager mManager;

	public FrameManager(int screenWidth, int screenHeight, EventManager eM, CardManager cM, MenuManager menuPanel, OverworldManager oM, MMPanelManager mManager, SettlementManager sM){
		//initialise the frame
		frame = new JFrame();
		frame.setSize(new Dimension(screenWidth, screenHeight));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		//set the managers and panels that to those given in the constructor
		this.eM = eM;
		this.cM = cM;
		this.menuPanel = menuPanel;
		this.oM = oM;
		this.mManager = mManager;
		this.sM =sM;

		//add the card manager to the frame
		frame.add(cM);

		//add any listeners that are needed thought the program
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addKeyListener(this);
		
		//remove the border of the frame and set it to visible
		frame.setUndecorated(true);
		frame.setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		//switch based on the card that is currently being displayed and give the key event to the class that is being shown
		switch(cM.getCurrentCard()){
		case "BattleCard":
			eM.keyPressed(event);
			break;
		case "OverCard":
			oM.keyPressed(event);
			break;
		case "MapMakerCard":
			mManager.keyPressed(event);
			break;
		case "SettlementManager":
			sM.keyPressed(event);
		default:
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent event) {


	}

	@Override
	public void keyTyped(KeyEvent event) {


	}

	@Override
	public void mouseDragged(MouseEvent event) {
		//switch based on the card that is currently being displayed and give the mouse event to the class that is being shown
		switch(cM.getCurrentCard()){
		case "BattleCard":
			eM.mouseDragged(event);
			break;
		case "SettlementManager":
			sM.mouseDragged(event);
			break;
		case "OverCard":
			oM.mouseDragged(event);
			break;
		default:
			break;
		}

	}

	@Override
	public void mouseMoved(MouseEvent event) {
		//switch based on the card that is currently being displayed and give the mouse event to the class that is being shown
		switch(cM.getCurrentCard()){
		case "SettlementManager":
			sM.mouseMoved(event);
			break;
		case "OverCard":
			oM.mouseMoved(event);
			break;
		default:
			break;
		}

	}

	@Override
	public void mouseClicked(MouseEvent event) {
		//switch based on the card that is currently being displayed and give the mouse event to the class that is being shown
		switch(cM.getCurrentCard()){
		case "BattleCard":
			eM.mouseDragged(event);
			break;
		case "SettlementManager":
			sM.mouseClicked(event);
			break;
		case "OverCard":
			oM.mouseClicked(event);
		default:
			break;
		}

	}

	@Override
	public void mouseEntered(MouseEvent event) {


	}

	@Override
	public void mouseExited(MouseEvent event) {


	}

	@Override
	public void mousePressed(MouseEvent event) {


	}

	@Override
	public void mouseReleased(MouseEvent event) {
		//switch based on the card that is currently being displayed and give the mouse event to the class that is being shown
		switch(cM.getCurrentCard()){
		case "BattleCard":
			eM.mouseReleased(event);
			break;
		default:
			break;
		}

	}
}