package battle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import overworld.Unit;
import utilities.CardManager;


public class InfoPanel extends JPanel{
	//declare any variables used throughout the class
	int panelWidth;
	int panelHeight;
	ArrayList<Unit> selectedUnits;
	JPanel controlOptionPane;
	JSlider agroSlider;
	EventManager eM;
	Boolean gamePaused;
	JButton pauseButton;
	JLabel currentGameSpeedLabel;
	float currentGameSpeed = 1;
	
	CardManager cM;



	public InfoPanel(int panelWidth, int panelHeight, EventManager newEventManager){
		//initialise most of the declared variables
		setPreferredSize(new Dimension(panelWidth, panelHeight));

		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		eM = newEventManager;

		selectedUnits = new ArrayList<Unit>();

		//create the display panel
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(panelWidth, panelHeight - 300));

		//create a new pane to hold the controls
		controlOptionPane = new JPanel();
		controlOptionPane.setPreferredSize(new Dimension(panelWidth, 300));

		//create a slider to control the aggression range of selected units
		JLabel agroSliderLabel = new JLabel("Aggression Range");
		agroSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		agroSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 100);
		agroSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				//update aggression range on selected units
				eM.updateAgroRangeForMapDisplay(agroSlider.getValue());
				repaint();
			}

		});
		agroSlider.setFocusable(false);
		agroSlider.setMajorTickSpacing(100);
		agroSlider.setMinorTickSpacing(20);
		agroSlider.setPaintTicks(true);
		agroSlider.setPaintLabels(true);

		//set the game as starting paused
		gamePaused = true;

		//create a panel for the time controls
		JPanel timeControlls = new JPanel();
		timeControlls.setPreferredSize(new Dimension(300, 100));
		
		currentGameSpeedLabel  = new JLabel("Game Paused");
		pauseButton = new JButton("Play");
		pauseButton.setFocusable(false);
		pauseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//pause the game
				pauseGame();
			}

		});

		//create a button to slow doen the speed of the game
		JButton slowTimeButton = new JButton("Slower");
		slowTimeButton.setFocusable(false);
		slowTimeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//check if the current speed is already at its minimum
				if(currentGameSpeed > 0.25){
					//half the current game speed
					currentGameSpeed = currentGameSpeed / 2;
					//update the speed of the game to the new value and update the label to indicate this
					eM.updateTime(currentGameSpeed);
					currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
				}
			}

		});

		//add a button to increase game speed
		JButton fastTimeButton = new JButton("Faster");
		fastTimeButton.setFocusable(false);
		fastTimeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//check if the game speed is already at the maximum value
				if(currentGameSpeed < 4){
					//double the game speed
					currentGameSpeed = currentGameSpeed * 2;
					//update the speed with the new value and update the label to reflect the new value
					eM.updateTime(currentGameSpeed);
					currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
				}
			}

		});
		
		//add a button to retreat from battle
		JButton retreatButton = new JButton("Retreat");
		retreatButton.setFocusable(false);
		retreatButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//escape from the battle
				escapeBattle();
			}
			
		});

		//add all the buttons and displays to the panel
		timeControlls.setLayout(new GridLayout(3, 0));
		timeControlls.add(slowTimeButton);
		timeControlls.add(pauseButton);
		timeControlls.add(fastTimeButton);
		timeControlls.add(retreatButton);

		controlOptionPane.add(agroSliderLabel);
		controlOptionPane.add(agroSlider);
		controlOptionPane.add(currentGameSpeedLabel);
		controlOptionPane.add(timeControlls);

		add(BorderLayout.CENTER, display);
		add(BorderLayout.SOUTH, controlOptionPane);
	}

	public void updateSelectedUnits(ArrayList<Unit> newSelectedUnits){
		//clear the list of selected untis and replace them with the given units
		selectedUnits.clear();
		selectedUnits.addAll(newSelectedUnits);
		repaint();
	}
	
	public void escapeBattle(){
		//show the over world card and tell the map display to end the battle with a retreat
		cM.showCard("BattleCard", "OverCard");
		eM.retreatFromBattle();
	}
	
	public void pauseGame(){
		eM.updateTime(0);
		//pause the game
		gamePaused = !gamePaused;
		if(gamePaused){
			//update the labels to show the new state of the game
			pauseButton.setText("Play");
			currentGameSpeedLabel.setText("Game Paused");
		} else {
			pauseButton.setText("Pause");
			currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
		}
	}
	
	public void giveCardManager(CardManager newCM){
		//get the card manager used throughout the program
		cM = newCM;
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			//paint a background
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, panelWidth, panelHeight);

			//display each unit that is currently selected
			for(int i = 0; i < selectedUnits.size(); i ++){
				g.setColor(Color.BLACK);
				switch(selectedUnits.get(i).getType()){
				case 0:
					g.drawString("Type: Land", 20, (70 * i ) + 10);
					break;
				case 1:
					g.drawString("Type: Air", 20, (70 * i ) + 10);
					break;
				case 2:
					g.drawString("Type: Sea", 20, (70 * i ) + 10);
					break;
				default:
					break;
				}
				//display the aggression range and health bars of the selected units
				g.drawString("Agression range: " + selectedUnits.get(i).getAgroRange(), 20, (70 * i) + 29);
				g.setColor(Color.GREEN);
				g.drawRect(10, (70 * i ) + 49, 181, 11);
				g.setColor(Color.RED);
				g.fillRect(11, (70 * i ) + 50, 180, 10);
				g.setColor(Color.GREEN);
				g.fillRect(11, (70 * i ) + 50, (int)(180 * selectedUnits.get(i).getHealthRatio()), 10);
			}

		}
	}
}
