package overworld;
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

import settlement.SettlementManager;
import utilities.CardManager;

public class SettlementInfoPanel extends JPanel{
	int settlementID;
	int width;
	int height;
	String settlementName;
	CardManager cM;
	SettlementManager sM;
	int settlementOwner;
	int settlementIncome;
	int ownerFunds;
	int playerTurn;

	public SettlementInfoPanel(int width, int height, int startPlayerTurn){
		//set the size and layout for this panel
		setPreferredSize(new Dimension(width, height));
		//set the layout
		setLayout(new GridLayout(0, 1));
		this.width = width;
		this.height = height;
		
		playerTurn = startPlayerTurn;

		//set an initial settlement ID
		settlementID = 1;
		//set an initial settlement owner
		settlementOwner = 0;

		//initialise the settlement name so it can display something 
		settlementName = "No settlement selected";

		//create the button that will link this to the settlement manager
		JButton settlementButton = new JButton("Settlement Manager");
		settlementButton.setFocusable(false);
		settlementButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//check if the player whose turn it is owns the settlement
				if(isSettlementOwnedByPlayer()){
					//show the settlement manager and give it the correct settlement
					updateSettlementIDForSettlementManager();
					//set the frame focus onto the settlement manager
					cM.showCard("OverCard", "SettlementManager");
				}
			}

		});

		//create the display for this panel
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(width, height));
		add(display);
		add(settlementButton);
	}
	
	public void updateSettlementIDForSettlementManager(){
		//update the settlement being shown in the settlement manager
		sM.setSettlement(settlementID, playerTurn);
	}

	public void setNewSettlement(int newID){
		//function to allow other classes to set the settlement that is currently being focused on
		settlementID = newID;
		updateInformation();
	}
	public void updatePlayerTurn(int newPlayerTurn){
		//set the player turn to the new player index
		playerTurn = newPlayerTurn;
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
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementID);
			while(rs.next()){
				//loop through all rows in the table that were returned
				//set the settlement name, owner and income to that stored in the database
				settlementName = rs.getString(2);
				settlementOwner = rs.getInt(9);
				settlementIncome = rs.getInt(10);
			}

			//get the funds of the player that owns the settlement
			Statement stmt2 = con.createStatement();
			//get the result set for the query executed
			ResultSet rs2 = stmt2.executeQuery("select * from player where id = " + settlementOwner);
			while(rs2.next()){
				//loop through all rows in the table that were returned
				//set the owner funds to the correct value
				ownerFunds = rs2.getInt(3);
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}
	public Boolean isSettlementOwnedByPlayer(){
		return (settlementOwner == playerTurn);
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			//paint the background
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);

			//indicate the type of item that is selected
			g.setColor(Color.BLACK);
			//draw in the settlement name and relevant information
			g.drawString("Settlement", 10, 50);
			g.drawString(settlementName, 10, 70);
			g.drawString("Settlement owned by player " + settlementOwner, 10, 90);
			g.drawString("Settlement income " + settlementIncome, 10, 110);
			g.drawString("Player Funds " + ownerFunds, 10, 130);

		}
	}
}
