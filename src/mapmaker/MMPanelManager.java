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

	MMSaveManager sM;
	MMCanvasUI cUI;
	MMEventManager eManager;
	MMControlPanel cPanel;
	GridBagConstraints c;
	CardManager cM;
	JFileChooser chooser;

	public MMPanelManager(MMCanvasUI cUI){
		this.cUI = cUI;
		setSize(new Dimension(1000, 700));
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();

		sM = new MMSaveManager();
		add(cUI, c);

		chooser = new JFileChooser();

	}

	public void giveCardManager(CardManager newCM){
		cM = newCM;
	}

	public void giveEManager(MMEventManager givenEManager){
		eManager = givenEManager;
		cPanel = new MMControlPanel(eManager);
		cPanel.setPreferredSize(new Dimension(300, 700));
		add(cPanel, c);
		revalidate();
		repaint();

		setVisible(true);
	}

	public MMControlPanel getCP(){
		return cPanel;
	}

	public void keyPressed(KeyEvent event) {
		switch(event.getKeyCode()){
		case KeyEvent.VK_ESCAPE:
			cM.showCard("MapMakerCard", "MenuCard");
			break;
		case KeyEvent.VK_S:
			try {
				int returnVal = chooser.showSaveDialog(this);
				File file = chooser.getSelectedFile();
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					sM.saveObstructionMap(file, cUI.getObstructionMap());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case KeyEvent.VK_L:
			int returnVal = chooser.showOpenDialog(this);
			File file = chooser.getSelectedFile();
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				cUI.setObstructionMap(sM.loadObstructionMap(file));
			}
			repaint();
			break;
		case KeyEvent.VK_C:
			cUI.emptyMap();
			break;
		default:
			break;
		}
	}
}