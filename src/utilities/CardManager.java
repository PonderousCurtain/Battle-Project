package utilities;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import mapmaker.MMPanelManager;
import settlement.SettlementManager;


public class CardManager extends JPanel{
	CardLayout cl;
	String currentCard;
	String origin;
	
	public CardManager(JPanel battleCard, JPanel menuCard, int screenWidth, int screenHeight, JPanel overWorldPanel, MMPanelManager mMManager, SettlementManager sManager){
		cl = new CardLayout();
		
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		
		setLayout(cl);
		
		add(battleCard, "BattleCard");
		add(overWorldPanel, "OverCard");
		add(menuCard, "MenuCard");
		add(mMManager, "MapMakerCard");
		add(sManager, "SettlementManager");
		
		cl.show(this, "MenuCard");
		currentCard = "MenuCard";
		
	}
	
	public void showCard(String newOrigin, String cardName){
		origin = newOrigin;
		cl.show(this, cardName);
		currentCard = cardName;
	}
	public void returnToOrigional(){
		cl.show(this, origin);
		currentCard = origin;
	}
	public String getCurrentCard(){
		return currentCard;
	}
	
}
