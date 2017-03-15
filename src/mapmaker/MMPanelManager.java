package mapmaker;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import utilities.CardManager;


public class MMPanelManager extends JPanel{
	//declare the variables and classes used throughout this class
	MMSaveManager sM;
	MMCanvasUI cUI;
	MMEventManager eManager;
	MMControlPanel cPanel;
	GridBagConstraints c;
	CardManager cM;
	JFileChooser chooser;
	int screenWidth;
	int screenHeight;

	public MMPanelManager(MMCanvasUI cUI, int screenWidth ,int screenHeight){
		//initialise the canvas display and set the size of this panel to the maximum sie given by the constructor
		this.cUI = cUI;
		setSize(new Dimension(screenWidth, screenHeight));
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		
		//set the values for the screen size
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		//initialise the save manager and add the canvas display to the panel
		sM = new MMSaveManager();
		add(cUI, c);

		//initialise the file chooser used for specifying where to save files and where to read them from
		chooser = new JFileChooser();

	}

	public void giveCardManager(CardManager newCM){
		//get the card manager that handles the program
		cM = newCM;
	}

	public void giveEManager(MMEventManager givenEManager){
		//get the map maker event manager to allow communication with the canvas display
		eManager = givenEManager;
		//initialise the control panel for the map maker and give it the event manager then add it to the panel display
		cPanel = new MMControlPanel(eManager);
		cPanel.setPreferredSize(new Dimension(screenWidth - screenHeight, screenHeight));
		add(cPanel, c);
		revalidate();
		repaint();

		//set the panel to visible
		setVisible(true);
	}

	public MMControlPanel getCP(){
		//used to give other clases the control panel when and if they request it
		return cPanel;
	}

	public void keyPressed(KeyEvent event) {
		//add a key listener to handle user input for this class
		switch(event.getKeyCode()){
		case KeyEvent.VK_ESCAPE:
			//add a check so that is the user presses escape they are return to the main menu screen
			cM.showCard("MapMakerCard", "MenuCard");
			break;
		case KeyEvent.VK_S:
			//if the user presses S open a panel to allow them to pick a location to save the map they have made
			try {
				int returnVal = chooser.showSaveDialog(this);
				File file = chooser.getSelectedFile();
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					//save the map using the save manager
					sM.saveObstructionMap(file, cUI.getObstructionMap());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case KeyEvent.VK_L:
			//if the user presses L then open a panel so they can pick which file they want to open
			int returnVal = chooser.showOpenDialog(this);
			File file = chooser.getSelectedFile();
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				//get the map they want to open using the save manager and give it to the canvas display
				cUI.setObstructionMap(sM.loadObstructionMap(file));
			}
			repaint();
			break;
		case KeyEvent.VK_C:
			//if the user presses C then clear the current map
			cUI.emptyMap();
			break;
		default:
			break;
		}
	}
}