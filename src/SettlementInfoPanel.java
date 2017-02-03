import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JPanel;

public class SettlementInfoPanel extends JPanel{
	int settlementID;
	int width;
	int height;
	String settlementName;
	
	public SettlementInfoPanel(int width, int height){
		setPreferredSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
	}

	public void setNewSettlement(int newID){
		settlementID = newID;
	}
	
	public void updateInformation(){
		//attempt to connect to the database containing the settlement table
				try{
					System.out.println("Attempting connection");
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
					System.out.println("Connected \n");
					//create the query to be made to the table
					Statement stmt = con.createStatement();
					//get the result set for the query executed
					ResultSet rs = stmt.executeQuery("select * from settlements where id = " + settlementID);
					while(rs.next()){
						//loop through all rows in the table that were returned
						//set the settlement name to that stored in the correct column
						settlementName = rs.getString(2);
					}
					//close the connection to the database
					con.close();
				} catch (Exception e){
					//in case of an error print the error code for trouble shooting
					System.out.println(e.toString());
				}
	}
	
	public class Paint extends JPanel{
		public void paintComponent(Graphics gr){
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, width, height);
		}
	}
}
