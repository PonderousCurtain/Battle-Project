package utilities;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import mapmaker.MMPanelManager;
import settlement.SettlementManager;


public class CardManager extends JPanel{
	//declare the variable to be used throughout the class
	CardLayout cl;
	String currentCard;
	String origin;
	
	public CardManager(JPanel battleCard, JPanel menuCard, int screenWidth, int screenHeight, JPanel overWorldPanel, MMPanelManager mMManager, SettlementManager sManager){
		//initialise the layout
		cl = new CardLayout();
		//set the size of the panel to the frame size
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		//set the panel layout
		setLayout(cl);
		
		//add all of the different cards to the panel from the constructor
		add(battleCard, "BattleCard");
		add(overWorldPanel, "OverCard");
		add(menuCard, "MenuCard");
		add(mMManager, "MapMakerCard");
		add(sManager, "SettlementManager");
		
		//start by showing the main menu panel
		cl.show(this, "MenuCard");
		currentCard = "MenuCard";
		
	}
	
	public void showCard(String newOrigin, String cardName){
		//set the origin for any panels that are accessed from more than one other card
		origin = newOrigin;
		//show the desired card
		cl.show(this, cardName);
		//set the name of the now current card
		currentCard = cardName;
	}
	public void returnToOrigional(){
		//return to the previously shown card
		cl.show(this, origin);
		currentCard = origin;
	}
	public String getCurrentCard(){
		//get the card that is currently being shown
		return currentCard;
	}
	
}
