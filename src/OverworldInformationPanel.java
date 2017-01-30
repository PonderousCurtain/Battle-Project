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

		//create a paint class
		Paint display = new Paint();
		//allocate the paint class the full panel space
		display.setPreferredSize(new Dimension(width, height));
		//add the paint class to the panel
		add(display);

	}

	public void updateSelected(int newSelectedID, Boolean newIsSelectedIsASettlement){
		//update the selected id and the type of item that has been selected
		selectedID = newSelectedID;
		isSelectedASettlement = newIsSelectedIsASettlement;
		//determine the type of item that is selected
		if(isSelectedASettlement){
			//if the selected item is a settlement then update the settlement display
			updateSettlementInformation();
		} else {
			//otherwise if an army is selected then update the army display
			updateArmyInformation();
		}
		//repaint the information display with the values of the newly selected item
		repaint();
	}
	
	public void updateSettlementInformation(){
		//attempt to connect to the database containing the settlement table
		try{
			System.out.println("Attempting connection");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			System.out.println("Connected \n");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements where id = " + selectedID);
			while(rs.next()){
				//loop through all rows in the table that were returned
				//set the settlement name to that stored in the correct column
				settlementName = rs.getString(2);
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}
	
	public void updateArmyInformation(){
		//set the selected Army to the correct army from the list
		selectedArmy = allArmiesList.get(selectedID);
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;

			//paint a background on the panel
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);

			//check that something is selected (upon starting up nothing will have been selected)
			if(selectedID != 0){

				//check if the selected item is a settlement or an army
				if(isSelectedASettlement){
					//a settlement is selected
					g.setColor(Color.BLACK);
					g.drawString(settlementName, 10, 10);
				} else {
					//an army is selected
				}
			}
		}
	}
}
