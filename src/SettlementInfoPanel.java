import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SettlementInfoPanel extends JPanel{
	int settlementID;
	int width;
	int height;
	String settlementName;
	CardManager cM;
	SettlementManager sM;
	int settlementOwner;
	
	public SettlementInfoPanel(int width, int height){
		//set the size and layout for this panel
		setPreferredSize(new Dimension(width, height));
		//set the layout
		setLayout(new GridLayout(0, 1));
		this.width = width;
		this.height = height;
		
		//set an initial settlement ID
		settlementID = 1;
		//set an initial settlement ownder
		settlementOwner = 0;
		
		//initialise the settlement name so it can display something 
		settlementName = "No settlement selected";
		
		//create the button that will link this to the settlement manager
		JButton settlementButton = new JButton("Settlement Manager");
		settlementButton.setFocusable(false);
		settlementButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//show the settlement manager and give it the correct settlement
				sM.setSettlement(settlementID);
				//set the frame focus onto the settlement manager
				cM.showCard("OverCard", "SettlementManager");
			}
			
		});
		
		//create the display for this panel
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(width, height));
		add(display);
		add(settlementButton);
	}

	public void setNewSettlement(int newID){
		//function to allow other classes to set the settlement that is currently being focused on
		settlementID = newID;
		updateInformation();
	}
	
	public void giveCardManager(CardManager cM){
		//set the card manager to that used throughout the program to allow changing cards
		this.cM = cM;
	}
	public void giveSettlementManager(SettlementManager sM){
		//set the settlement manager to that used throughout the program to update settlements
		this.sM = sM;
	}
	
	public void updateInformation(){
		//attempt to connect to the database containing the settlement table
				try{
					System.out.println("Attempting connection");
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
					System.out.println("Connected \n");
					//create the query to be made to the table
					Statement stmt = con.createStatement();
					//get the result set for the query executed
					ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementID);
					while(rs.next()){
						//loop through all rows in the table that were returned
						//set the settlement name to that stored in the correct column
						settlementName = rs.getString(2);
						settlementOwner = rs.getInt(9);
					}
					//close the connection to the database
					con.close();
				} catch (Exception e){
					//in case of an error print the error code for trouble shooting
					System.out.println(e.toString());
				}
	}
	
	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			//paint the background
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);
			
			//indicate the type of item that is selected
			g.setColor(Color.BLACK);
			g.drawString("Settlement", 10, 50);
			
			g.drawString(settlementName, 10, 70);
			
			g.drawString("Settlement owned by player " + settlementOwner, 10, 90);
			
		}
	}
}
