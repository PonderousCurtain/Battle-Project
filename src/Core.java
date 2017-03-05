import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;


public class Core{
	public static void main(String [] args){
		new Core();
	}

	public Core(){
		int screenHeight = 700;
		int screenWidth = 1000;

		MapDisplay mD = new MapDisplay(screenWidth, screenHeight);
		EventManager eM = new EventManager(mD);
		InfoPanel iP = new InfoPanel(300, screenHeight, eM);


		JPanel battlePanel = new JPanel();
		battlePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		battlePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));

		battlePanel.add(mD, c);
		battlePanel.add(iP, c);

		OverworldManager oM = new OverworldManager(screenWidth, screenHeight);

		MenuManager menuPanel = new MenuManager(screenWidth, screenHeight);

		//add the classes for the map maker

		MMCanvasUI cUI = new MMCanvasUI();
		MMPanelManager MManager = new MMPanelManager(cUI);
		MMEventManager eManager = new MMEventManager(cUI, MManager);
		MManager.giveEManager(eManager);
		cUI.giveEManager(eManager);
		
		SettlementManager sManager  = new SettlementManager(screenWidth, screenHeight);


		CardManager cM = new CardManager(battlePanel, menuPanel, screenWidth, screenHeight, oM, MManager, sManager);

		menuPanel.giveCardManager(cM);
		oM.giveCardManager(cM);
		oM.giveSettlementManager(sManager);
		oM.giveMapDisplay(mD);
		iP.giveCardManager(cM);
		MManager.giveCardManager(cM);
		sManager.giveCardManager(cM);
		mD.giveCardManger(cM);




		FrameManager fM = new FrameManager(screenWidth, screenHeight, eM, cM, menuPanel, oM, MManager, sManager);


		mD.giveInfoPanel(iP);
	}

}