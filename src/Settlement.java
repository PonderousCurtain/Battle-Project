import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class Settlement{
	//set up the needed variables
	ArrayList<Building> avalibleBuildings;
	int gridSize;
	SettlementGridSpace[][] gridArray;
	ArrayList<Building> placedBuildings;
	int overallProffit;
	int ID;
	
	public Settlement(int gridSize, int ID){
		//initialise the main variables for this class
		this. ID = ID;
		avalibleBuildings = new ArrayList<Building>();
		this.gridSize = gridSize;
		
		placedBuildings = new ArrayList<Building>();
		gridArray = new SettlementGridSpace[gridSize][gridSize];
		
		//loop through the size of the grid 
		for(int xCount = 0; xCount < gridSize; xCount ++){
			for(int yCount = 0; yCount < gridSize; yCount++){
				//for each index of the grid array add a new settlement grid space
				gridArray[xCount][yCount] = new SettlementGridSpace(0, Color.GRAY);
			}
		}
		
		//use SQL to add the correct buildings to the settlement available buildings list
		try{
			System.out.println("Attempting connection");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			System.out.println("Connected \n");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements where id = "+ ID);
			while(rs.next()){
				//add the correct buildings for the settlement
				if(rs.getInt(5) == 1){
					//if the settlement has farms available then add farms to the settlement buildings list
					addBuilding("Farm");
				}
				if(rs.getInt(6) == 1){
					//if the settlement has barracks available then add farms to the settlement buildings list
					addBuilding("Barracks");
				}
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}
	
	public void addBuilding(String buildingName){
		try{
			System.out.println("Attempting connection");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			System.out.println("Connected \n");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from building where name = '"+ buildingName + "'");
			while(rs.next()){
				//create the correct set of blocks for the building
				ArrayList<int[]> blocks = new ArrayList<int[]>();
				if(rs.getInt(4) == 1){
					blocks.add(new int[] {-1, -1});
				}
				if(rs.getInt(5) == 1){
					blocks.add(new int[] {0, -1});
				}
				if(rs.getInt(6) == 1){
					blocks.add(new int[] {1, -1});
				}
				if(rs.getInt(7) == 1){
					blocks.add(new int[] {-1, 0});
				}
				if(rs.getInt(8) == 1){
					blocks.add(new int[] {0, 0});
				}
				if(rs.getInt(9) == 1){
					blocks.add(new int[] {1, 0});
				}
				if(rs.getInt(10) == 1){
					blocks.add(new int[] {-1, 1});
				}
				if(rs.getInt(11) == 1){
					blocks.add(new int[] {01, 1});
				}
				if(rs.getInt(12) == 1){
					blocks.add(new int[] {1, 1});
				}
				//add a new building to the available list with that set of blocks
				avalibleBuildings.add(new Building(rs.getString(1), blocks, rs.getInt(3), rs.getInt(2)));
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}
	}
	
	public int getGridSize(){
		return gridSize;
	}
	public void updateProffit(){
		//update the profit of the buildings
		overallProffit = 0;
		for(int i = 0; i < placedBuildings.size(); i ++){
			//loop through each building in the settlement and add its profit cost to the overall profit
			overallProffit += placedBuildings.get(i).getUpkeep();
		}
	}
	
	public int getProffit(){
		updateProffit();
		return overallProffit;
	}
	public SettlementGridSpace[][] getGrid(){
		return gridArray;
	}
	public void setGrid(int gridX, int gridY, int newValue){
		gridArray[gridX][gridY].setValue(newValue);
	}
	public int getID(){
		return ID;
	}
	public ArrayList<Building> getBuildings(){
		return avalibleBuildings;
	}
	public ArrayList<Building> getPlacedBuildings(){
		return placedBuildings;
	}
}
