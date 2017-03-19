package utilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import battle.EventManager;
import battle.InfoPanel;
import battle.MapDisplay;
import mainmenu.MenuManager;
import mapmaker.MMCanvasUI;
import mapmaker.MMEventManager;
import mapmaker.MMPanelManager;
import overworld.OverworldManager;
import settlement.SettlementManager;


public class Core{
	public static void main(String [] args){
		//start the program
		new Core();
	}

	public Core(){
		//get the account name of the user that is going to play
		String accountName = JOptionPane.showInputDialog("Please sign in");
		//set the values for the panel width and height to be used as the 'screen'
		int screenHeight = 700;
		int screenWidth = 1000;

		//initialise a map display, information panel and event manager to handle the real time battles
		MapDisplay mD = new MapDisplay(screenWidth, screenHeight);
		EventManager eM = new EventManager(mD);
		InfoPanel iP = new InfoPanel(300, screenHeight, eM);

		//create a panel to hold the map display and information panel
		JPanel battlePanel = new JPanel();
		battlePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		battlePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));

		//add the map display and information panel to the battle panel
		battlePanel.add(mD, c);
		battlePanel.add(iP, c);
		
		//create the save manager
		SaveManager sM = new SaveManager();

		//initialise the settlement manager
		SettlementManager sManager  = new SettlementManager(screenWidth, screenHeight, accountName, sM);

		//initialise managers for the over world screen and main menu
		OverworldManager oM = new OverworldManager(screenWidth, screenHeight, accountName, sM);

		MenuManager menuPanel = new MenuManager(screenWidth, screenHeight, accountName, oM, sManager, sM);

		//add the classes for the map maker
		MMCanvasUI cUI = new MMCanvasUI();
		MMPanelManager MManager = new MMPanelManager(cUI, screenWidth, screenHeight);
		MMEventManager eManager = new MMEventManager(cUI, MManager);
		MManager.giveEManager(eManager);
		cUI.giveEManager(eManager);

		//Initialise the card manager and give it all of the panels that it will be able to switch between
		CardManager cM = new CardManager(battlePanel, menuPanel, screenWidth, screenHeight, oM, MManager, sManager);

		//give the card manager to each main card to allow them to swap display to the other cards
		menuPanel.giveCardManager(cM);
		oM.giveCardManager(cM);
		iP.giveCardManager(cM);
		MManager.giveCardManager(cM);
		sManager.giveCardManager(cM);
		mD.giveCardManger(cM);

		//give the over world the settlement manager and map display so it can communicate information with them
		oM.giveSettlementManager(sManager);
		oM.giveMapDisplay(mD);
		
		//give the map display access to the information panel in the battle panel
		mD.giveInfoPanel(iP);



		//Initialise a frame manager with all the classes that require mouse and key events
		FrameManager fM = new FrameManager(screenWidth, screenHeight, eM, cM, menuPanel, oM, MManager, sManager);

	}

}