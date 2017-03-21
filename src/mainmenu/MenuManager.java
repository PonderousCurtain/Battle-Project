package mainmenu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import overworld.OverworldManager;
import settlement.SettlementManager;
import utilities.CardManager;
import utilities.SaveManager;


public class MenuManager extends JPanel{
	int screenWidth;
	int screenHeight;
	CardManager cM;
	String accountName;

	SaveManager sM;
	OverworldManager overworldManager;
	SettlementManager settlementManager;


	public MenuManager(int screenWidth, int screenHeight, String accountName, OverworldManager overworldManager, SettlementManager settlementManager, SaveManager sM){
		//set the size of the panel to the size given 
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.accountName = accountName;
		this.sM = sM;
		this.overworldManager = overworldManager;
		this.settlementManager = settlementManager;
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		GridBagConstraints c = new GridBagConstraints();

		JLayeredPane lPane = new JLayeredPane();
		lPane.setPreferredSize(new Dimension(screenWidth, screenHeight));

		//create a panel to hold the buttons and set the location to be the centre of the panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBounds(screenWidth/2 - 150,  screenHeight/2 - 200,  300,  400);

		//add the button to go to the map maker
		JButton mapMakerButton = new JButton("Map Maker");
		mapMakerButton.setFocusable(false);
		mapMakerButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//change the shown card to the map maker
				cM.showCard("MenuCard", "MapMakerCard");
			}

		});

		//add a button to close the program
		JButton exitButton = new JButton("Exit To Desktop");
		exitButton.setFocusable(false);
		exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//stop the program and close
				System.exit(1);
			}

		});

		//add a button to go to the over world screen
		JButton overworldButton = new JButton("View Overworld");
		overworldButton.setFocusable(false);
		overworldButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//switch the card to the over world
				cM.showCard("MenuCard", "OverCard");
			}

		});

		//add a button to go to the battle map
		JButton battleButton = new JButton("Fight");
		battleButton.setFocusable(false);
		battleButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//switch to the card battle card
				cM.showCard("MenuCard", "BattleCard");
			}

		});

		//add a button to create a new account (and so game)
		JButton newAccount = new JButton("Create a new account");
		newAccount.setFocusable(false);
		newAccount.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//run the method to add a new account
				makeNewAccount();
			}

		});

		//add a button to allow the user to save their game
		JButton saveGame = new JButton("Save game");
		saveGame.setFocusable(false);
		saveGame.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//save the current game
				saveGame();
			}});


		Paint display = new Paint(); 
		display.setBounds(0, 0, screenWidth, screenHeight);

		//add all of the components to the correct panels
		c.fill =  GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		buttonPanel.add(battleButton, c);
		c.gridy = 1;
		buttonPanel.add(mapMakerButton, c);
		c.gridy = 2;
		buttonPanel.add(overworldButton, c);
		c.gridy = 3;
		buttonPanel.add(newAccount, c);
		c.gridy = 4;
		buttonPanel.add(saveGame, c);
		c.gridy = 5;
		buttonPanel.add(exitButton, c);


		lPane.add(display, new Integer(0));
		lPane.add(buttonPanel, new Integer(1));

		add(lPane);
		setVisible(true);
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			//set a background clour and image
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, screenWidth, screenHeight);

			Image background = new ImageIcon("Test.jpg").getImage();
			g.drawImage(background, 0, 0, 1300, 1000, null);
		}
	}

	public void giveCardManager(CardManager newCM){
		//get the card manager used for the program
		cM = newCM;
	}

	public void makeNewAccount(){
		//get the desired username for the new account
		String newAccountName = JOptionPane.showInputDialog("Please input the new Username");
		//add a new account to the database with the new name
		//attempt to connect to the database
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			stmt.executeUpdate("insert into accounts value('" + newAccountName + "', 'Password', 0)");
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
		//save a set of files with the default values under the new account name
		sM.saveDefaultValues(newAccountName);
	}

	public void saveGame(){
		//save the current game with the settlement list and army list being used
		sM.saveGame(accountName, settlementManager.getSettlementList(), overworldManager.getAllArmies());
		//update the current player turn in the account database for the current player
		//attempt to connect to the database containing the settlement table
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			stmt.executeUpdate("update accounts set playerTurn = " + overworldManager.getCurrentPlayer() + " where username = '" + accountName + "'");
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}

}
