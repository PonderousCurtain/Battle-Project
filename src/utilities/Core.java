package utilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
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
		EncryptionManager encryption = new EncryptionManager();
		//get the account name of the user that is going to play
		String accountName = "";
		String accountPassword;
		Boolean signedIn = false;
		while(!signedIn){
			signedIn = true;
			accountName = JOptionPane.showInputDialog("Please sign in");
			if(accountName == null){
				System.exit(1);
				break;
			}
			accountPassword = JOptionPane.showInputDialog("Password");
			//if the user cancelled then close the program
			if(accountPassword == null){
				System.exit(1);
				break;
			}
			//check if the account exists, if it doesn't then loop to get a new sign in
			//attempt to connect to the database containing the settlement table
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				//get the result set for the query executed
				ResultSet rs = stmt.executeQuery("select * from accounts where username = '" + accountName + "'");
				//check if any accounts where returned
				if(!rs.next()){
					signedIn = false;
					//indicate that the account was not found
					JOptionPane.showMessageDialog(new JFrame(), "Account not found");
				} else {
					//otherwise get the encrypted password from the database
					String passwordFromDatabase = rs.getString(2);
					//Check if the entered password is the same as the one in the database after it is decrypted
					if(!passwordFromDatabase.equals(encryption.encrypt(accountPassword))){
						signedIn = false;
						//indicate that the password was incorrect
						JOptionPane.showMessageDialog(new JFrame(), "Password Incorrect");
					}
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
		}
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