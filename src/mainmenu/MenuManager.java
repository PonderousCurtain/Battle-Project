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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import utilities.CardManager;


public class MenuManager extends JPanel{
	int screenWidth;
	int screenHeight;
	CardManager cM;

	public MenuManager(int screenWidth, int screenHeight){
		//set the size of the panel to the size given 
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
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

}
