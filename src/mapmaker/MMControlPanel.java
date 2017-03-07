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
	
	Boolean overMode = false;

	public MMControlPanel(MMEventManager newManager){
		eManager = newManager;
		brushColor = Color.BLACK;
		Paint cDisplay = new Paint();
		cDisplay.setPreferredSize(new Dimension(300, 700));
		cDisplay.setLayout(new GridLayout(0,1));
		roughness = 1;
		brushSize = 10;
		
		spaceHolder = new JPanel();
		spaceHolder.setOpaque(false);

		colorChooser = new JColorChooser();
		
		sizeBox = new JComboBox();
		


		JLabel sizeLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		sizeLabel.setText("Brush Size");
		sizeLabel.setVerticalTextPosition(JLabel.BOTTOM);
		sizeLabel.setHorizontalTextPosition(JLabel.CENTER);
			
		sizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 2);
		sizeSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				eManager.updateBrushSize(sizeSlider.getValue());
				repaint();
			}

		});
		sizeSlider.setFocusable(false);
		sizeSlider.setMajorTickSpacing(1);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);

		colorChooseButton = new JButton("Pick Colour");
		colorChooseButton.setFocusable(false);
		colorChooseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				brushColor = colorChooser.showDialog(null, "Color Picker", brushColor);
				eManager.updateColor(brushColor);
				repaint();
			}

		});

		JLabel roughnessLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		roughnessLabel.setText("Roughness (0 for wall)");
		roughnessLabel.setVerticalTextPosition(JLabel.BOTTOM);
		roughnessLabel.setHorizontalTextPosition(JLabel.CENTER);

		roughnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 1);
		roughnessSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				eManager.updateRoughness(roughnessSlider.getValue());
				repaint();
			}

		});
		roughnessSlider.setFocusable(false);
		roughnessSlider.setMinorTickSpacing(1);
		roughnessSlider.setMajorTickSpacing(5);
		roughnessSlider.setPaintTicks(true);
		roughnessSlider.setPaintLabels(true);
		
		String[] terrainTypes = {"Land", "Chasm", "Water", "Bridge"};
		terrainTypeBox = new JComboBox(terrainTypes);
		terrainTypeBox.setSelectedIndex(0);
		terrainTypeBox.setFocusable(false);
		terrainTypeBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				eManager.updateTileType(terrainTypeBox.getSelectedIndex());
				repaint();
			}
			
		});
		
		JLabel scaleLabel = new JLabel(new ImageIcon("BlankHolder.png"));
		scaleLabel.setText("Map Scaling");
		scaleLabel.setVerticalTextPosition(JLabel.BOTTOM);
		scaleLabel.setHorizontalTextPosition(JLabel.CENTER);

		scaleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		scaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 10);
		scaleSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent event) {
				eManager.updateScale(scaleSlider.getValue() * 7);
				repaint();
			}

		});
		scaleSlider.setFocusable(false);
		scaleSlider.setMinorTickSpacing(5);
		scaleSlider.setMajorTickSpacing(5);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		
		placeSettlementButton = new JButton("Place settlement");
		placeSettlementButton.setFocusable(false);
		placeSettlementButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				changeMode();
				repaint();
			}
			
		});

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


		add(cDisplay);
	}
	
	public void changeMode(){
		overMode = !overMode;
		eManager.changeMode(overMode);
	}
	
	public void updateValues(int newRoughness, Color newColor, int newTileType, int newBrushSize){
		roughnessSlider.setValue(newRoughness);
		roughness = newRoughness;
		brushColor = newColor;
		terrainTypeBox.setSelectedIndex(newTileType);
		brushSize = newBrushSize;
		repaint();
	}

	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 300, 700);

			g.setColor(brushColor);
			g.fillRect((300-sizeSlider.getValue()*brushSize)/2, (140-sizeSlider.getValue()*brushSize)/2, sizeSlider.getValue()*brushSize, sizeSlider.getValue()*brushSize);
		}
	}
}
