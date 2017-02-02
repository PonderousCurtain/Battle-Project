import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;


public class OverworldInformationPanel extends JPanel{
	//declare the variables used throughout the class
	int width;
	int height;
	int selectedID;
	Boolean isSelectedASettlement;
	String settlementName;
	Army selectedArmy;
	ArrayList<Army> allArmiesList;
	SettlementInfoPanel settlementPanel;
	ArmyInfoPanel armyPanel;
	CardLayout cl;

	public OverworldInformationPanel(int width, int height, ArrayList<Army> allArmiesList){
		//initialise the variables  as those passed to this class
		this.width = width;
		this.height = height;
		this.allArmiesList  = allArmiesList;
		//set the default settlement name to a non null value
		settlementName = "";
		//set the initially selected army as the first in the list
		selectedArmy = allArmiesList.get(0);
		//set the size to the space given
		setPreferredSize(new Dimension(width, height));
		selectedID = 0;
		isSelectedASettlement = false;

		//initialisation of a new card layout
		cl = new CardLayout();
		
		//initialise the two information panels
		settlementPanel = new SettlementInfoPanel(300, 1000);
		armyPanel = new ArmyInfoPanel(300, 1000);
		
		//set the layout of this panel and add the two information panels
		setLayout(cl);
		add(settlementPanel, "Settlement");
		add(armyPanel, "Army");
		
		//start by showing a settlement panel
		cl.show(this, "Army");
		
		//create a paint class
		Paint display = new Paint();
		//allocate the paint class the full panel space
		display.setPreferredSize(new Dimension(width, height));
		//add the paint class to the panel
		//add(display);

	}

	public void updateSelected(int newSelectedID, Boolean newIsSelectedIsASettlement){
		//update the selected id and the type of item that has been selected
		selectedID = newSelectedID;
		isSelectedASettlement = newIsSelectedIsASettlement;
		//determine the type of item that is selected
		if(isSelectedASettlement){
			//if the selected item is a settlement then update the settlement display
			settlementPanel.updateInformation();
			//then display that settlement panel
			cl.show(this, "Settlement");
		} else {
			//otherwise if an army is selected then update the army display
			armyPanel.updateInformation(allArmiesList.get(newSelectedID));
		// then display the army information panel
			cl.show(this, "Army");
		}
		//repaint the information display with the values of the newly selected item
		repaint();
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			//paint a background on the panel
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);
		}
	}
}
