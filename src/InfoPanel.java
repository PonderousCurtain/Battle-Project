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


public class InfoPanel extends JPanel{
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
		setPreferredSize(new Dimension(panelWidth, panelHeight));

		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		eM = newEventManager;

		selectedUnits = new ArrayList<Unit>();

		Paint display = new Paint();
		display.setPreferredSize(new Dimension(panelWidth, panelHeight - 300));

		controlOptionPane = new JPanel();
		controlOptionPane.setPreferredSize(new Dimension(panelWidth, 300));

		JLabel agroSliderLabel = new JLabel("Agression Range");
		agroSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		agroSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 100);
		agroSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				//update agro range on selected units
				eM.updateAgroRangeForMapDisplay(agroSlider.getValue());
				repaint();
			}

		});
		agroSlider.setFocusable(false);
		agroSlider.setMajorTickSpacing(100);
		agroSlider.setMinorTickSpacing(20);
		agroSlider.setPaintTicks(true);
		agroSlider.setPaintLabels(true);

		gamePaused = false;

		JPanel timeControlls = new JPanel();
		timeControlls.setPreferredSize(new Dimension(300, 100));
		
		currentGameSpeedLabel  = new JLabel("Current Game Speed: " + currentGameSpeed);
		pauseButton = new JButton("Pause");
		pauseButton.setFocusable(false);
		pauseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				eM.updateTime(0);
				//pause the game
				gamePaused = !gamePaused;
				if(gamePaused){
					pauseButton.setText("Play");
					currentGameSpeedLabel.setText("Game Paused");
				} else {
					pauseButton.setText("Pause");
					currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
				}
			}

		});

		JButton slowTimeButton = new JButton("Slower");
		slowTimeButton.setFocusable(false);
		slowTimeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentGameSpeed > 0.25){
					currentGameSpeed = currentGameSpeed / 2;
					eM.updateTime(currentGameSpeed);
					currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
				}
			}

		});

		JButton fastTimeButton = new JButton("Faster");
		fastTimeButton.setFocusable(false);
		fastTimeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(currentGameSpeed < 4){
					currentGameSpeed = currentGameSpeed * 2;
					eM.updateTime(currentGameSpeed);
					currentGameSpeedLabel.setText("Current Game Speed: " + currentGameSpeed);
				}
			}

		});
		
		JButton retreatButton = new JButton("Retreat");
		retreatButton.setFocusable(false);
		retreatButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				escapeBattle();
			}
			
		});

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
		selectedUnits.clear();
		selectedUnits.addAll(newSelectedUnits);
		repaint();
	}
	
	public void escapeBattle(){
		cM.showCard("BattleCard", "OverCard");
	}
	
	public void giveCardManager(CardManager newCM){
		cM = newCM;
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, panelWidth, panelHeight);

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
