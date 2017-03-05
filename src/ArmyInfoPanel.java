import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ArmyInfoPanel extends JPanel{
	Army currentArmy;
	int width;
	int height;
	Image coverImage;
	ArrayList<Unit> unitList;
	
	public ArmyInfoPanel(int width, int height){
		setPreferredSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
		
		//initialise the unit list to avoid errors
		unitList = new ArrayList<Unit>();
		
		Paint display = new Paint();
		display.setPreferredSize(new Dimension(width, height));
		add(display);
		repaint();
	}
	
	public void updateInformation(Army newArmy){
		coverImage = newArmy.getImage();
		unitList = newArmy.getUnits();
		repaint();
	}
	
	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			//paint the background
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);
			//display that the type is an army
			g.setColor(Color.BLACK);
			g.drawString("Army", 10, 50);
			//paint the cover image for that army
			g.drawImage(coverImage, 10, 100, width - 20, width - 20, null);
			//indicate that the below will be the units in that army
			g.drawString("Units", 10, 440);
			//paint all of the unit images for that army
			//set the initial x offset to 0 and the initial y offset to 0
			int xOffset = 0;
			int yOffset = 0;
			for(int i = 0; i < unitList.size(); i ++){
				//draw each image in a grid pattern going from top left to bottom right
				g.drawImage(unitList.get(i).getImage(), 10 + (xOffset*70), 470 + (yOffset*70), 70, 70, null);
				//highlight a boarder around each unit
				g.setColor(Color.BLACK);
				g.drawRect(10 + (xOffset*70), 470 + (yOffset*70), 70, 70);
				//increase the x offset
				xOffset ++;
				//check if this new position should be on a new line
				if(xOffset == 4){
					//if the end of a line has been reached then go down to the next line and reset the x offset
					xOffset = 0;
					yOffset ++;
				}
			}
		}
	}
}
