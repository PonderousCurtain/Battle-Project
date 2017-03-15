package mapmaker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MMControlPanel extends JPanel{

	//declare the variables to be used throughout the class
	JComboBox sizeBox;
	JComboBox terrainTypeBox;
	
	JPanel spaceHolder;
	
	
	JSlider sizeSlider;
	MMEventManager eManager;
	JSlider roughnessSlider;
	JSlider scaleSlider;
	JButton colorChooseButton;
	JButton placeSettlementButton;
	Color brushColor;
	JColorChooser colorChooser;
	int roughness;
	int brushSize;
	
	//set the user to not placing settlements originally
	Boolean overMode = false;

	public MMControlPanel(MMEventManager newManager){
		//Initialise the variables needed in the class
		eManager = newManager;
		brushColor = Color.BLACK;
		//create the display and set the new size for it
		Paint cDisplay = new Paint();
		cDisplay.setPreferredSize(new Dimension(300, 700));
		cDisplay.setLayout(new GridLayout(0,1));
		roughness = 1;
		brushSize = 10;
		
		//initialise the space holder for display purposes
		spaceHolder = new JPanel();
		spaceHolder.setOpaque(false);

		colorChooser = new JColorChooser();
		
		sizeBox = new JComboBox();
		

		//create the label and slider for changing the brush size. Give it the blank icon so that the label can be displayed closer to the slider it refers to
		JLabel sizeLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		sizeLabel.setText("Brush Size");
		sizeLabel.setVerticalTextPosition(JLabel.BOTTOM);
		sizeLabel.setHorizontalTextPosition(JLabel.CENTER);
			
		sizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 2);
		sizeSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				//update the brush size for the canvas display
				eManager.updateBrushSize(sizeSlider.getValue());
				repaint();
			}

		});
		//set the values for handling how the size slider is displayed
		sizeSlider.setFocusable(false);
		sizeSlider.setMajorTickSpacing(1);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);

		//add a button to chose a new colour for the brush
		colorChooseButton = new JButton("Pick Colour");
		colorChooseButton.setFocusable(false);
		colorChooseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//open a panel so the user can choose the desired colour they want then give it to the canvas display
				brushColor = colorChooser.showDialog(null, "Color Picker", brushColor);
				eManager.updateColor(brushColor);
				repaint();
			}

		});

		//create a label and sider to alter the roughness of the terrain
		JLabel roughnessLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		roughnessLabel.setText("Roughness (0 for wall)");
		roughnessLabel.setVerticalTextPosition(JLabel.BOTTOM);
		roughnessLabel.setHorizontalTextPosition(JLabel.CENTER);

		roughnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 1);
		roughnessSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				//give the new roughness value to the canvas display
				eManager.updateRoughness(roughnessSlider.getValue());
				repaint();
			}

		});
		//set the values for how the roughness slider should be displayed
		roughnessSlider.setFocusable(false);
		roughnessSlider.setMinorTickSpacing(1);
		roughnessSlider.setMajorTickSpacing(5);
		roughnessSlider.setPaintTicks(true);
		roughnessSlider.setPaintLabels(true);
		
		//create a string array so the user can choose the terrain type they want to add to the map
		String[] terrainTypes = {"Land", "Chasm", "Water", "Bridge"};
		terrainTypeBox = new JComboBox(terrainTypes);
		terrainTypeBox.setSelectedIndex(0);
		terrainTypeBox.setFocusable(false);
		terrainTypeBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//update the terrain type being created in the canvas display
				eManager.updateTileType(terrainTypeBox.getSelectedIndex());
				repaint();
			}
			
		});
		
		//create a label and slider for the scale of the map to allow the user to create both maps for the over world and also the real time battles
		JLabel scaleLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		scaleLabel.setText("Map Scaling");
		scaleLabel.setVerticalTextPosition(JLabel.BOTTOM);
		scaleLabel.setHorizontalTextPosition(JLabel.CENTER);

		scaleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		scaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 10);
		scaleSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				//update the canvas display with how many pixels a grid space should be
				eManager.updateScale(scaleSlider.getValue() * 7);
				repaint();
			}

		});
		//set the values for how the scale slider should be displayed
		scaleSlider.setFocusable(false);
		scaleSlider.setMinorTickSpacing(5);
		scaleSlider.setMajorTickSpacing(5);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		
		//create a button to allow the user to switch between placing buildings and not
		placeSettlementButton = new JButton("Place settlement");
		placeSettlementButton.setFocusable(false);
		placeSettlementButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				//toggle the current mode (placing or not placing)
				changeMode();
				repaint();
			}
			
		});

		//add all the controlling sliders and selection with their labels to the diaplay panel
		cDisplay.add(spaceHolder);
		cDisplay.add(sizeLabel);
		cDisplay.add(sizeSlider);
		cDisplay.add(colorChooseButton);
		cDisplay.add(roughnessLabel);
		cDisplay.add(roughnessSlider);
		cDisplay.add(terrainTypeBox);
		cDisplay.add(scaleLabel);
		cDisplay.add(scaleSlider);
		cDisplay.add(placeSettlementButton);


		//add the control display to the main panel
		add(cDisplay);
	}
	
	public void changeMode(){
		//toggle the mode and update it for the canvas display
		overMode = !overMode;
		eManager.changeMode(overMode);
	}
	
	public void updateValues(int newRoughness, Color newColor, int newTileType, int newBrushSize){
		//update all the values of shown on the controls to the given values
		roughnessSlider.setValue(newRoughness);
		roughness = newRoughness;
		brushColor = newColor;
		terrainTypeBox.setSelectedIndex(newTileType);
		brushSize = newBrushSize;
		repaint();
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			//paint a background
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 300, 700);
			
			//display the current brush size and colour at the top of the panel
			g.setColor(brushColor);
			g.fillRect((300-sizeSlider.getValue()*brushSize)/2, (140-sizeSlider.getValue()*brushSize)/2, sizeSlider.getValue()*brushSize, sizeSlider.getValue()*brushSize);
		}
	}
}
