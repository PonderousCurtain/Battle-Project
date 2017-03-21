package utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import settlement.Settlement;
import overworld.Army;
import overworld.Unit;


public class SaveManager {

	public Obstruction[][] loadBlockages(String mapName){
		//create a new array to store the loaded obstructions
		Obstruction[][] loadedObstructions = null;
		try {
			//create a connection to the save file
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(mapName));
			try {
				//read the array from the save file
				loadedObstructions = (utilities.Obstruction[][]) inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//close the connection
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return the loaded array
		return loadedObstructions;
	}
	public ArrayList<Settlement> loadSettlementList(String accountName){
		//initialise an array for the settlements
		ArrayList<Settlement> settlementList = null;
		try {
			//create a connection to the save file
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("C:/Users/Nat/git/Battle-Project/" + accountName + "Settlements.txt"));
			try {
				//read the array from the save file
				settlementList = (ArrayList<Settlement>) inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//close the connection
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return settlementList;
	}

	public ArrayList<Army> loadArmies(String accountName){
		//initialise an array for the settlements
		ArrayList<Army> ArmyList = null;
		try {
			//create a connection to the save file
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("C:/Users/Nat/git/Battle-Project/" + accountName + "Armies.txt"));
			try {
				//read the array from the save file
				ArmyList = (ArrayList<Army>) inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//close the connection
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ArmyList;
	}
	
	public void saveDefaultDatabaseValues(String accountName){
		//save the database values referring to the player
		try {
			PrintWriter write = new PrintWriter(new FileOutputStream(accountName + "PlayerDatabaseInfo.txt"));
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select * from player");
				while(rs.next()){
					//for each player set the funds to a default value
					write.println(rs.getInt(2) + ":500");
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//save the database values referring to each settlement
		try {
			PrintWriter write = new PrintWriter(new FileOutputStream(accountName + "SettlementDatabaseInfo.txt"));
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select * from settlements");
				while(rs.next()){
					//calculate the owner (as default even settlements are owned by player 0 and odd by player 1)
					int owner = rs.getInt(1) % 2;
					//save each line returned into the text file
					write.println(rs.getInt(1) + ":" + owner + ":0");
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveCurrentDatabaseValues(String accountName){
		//save the database values referring to the player
		try {
			PrintWriter write = new PrintWriter(new FileOutputStream(accountName + "PlayerDatabaseInfo.txt"));
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select * from player");
				while(rs.next()){
					//save each line returned into the text file
					write.println(rs.getInt(2) + ":" + rs.getInt(3));
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//save the database values referring to each settlement
		try {
			PrintWriter write = new PrintWriter(new FileOutputStream(accountName + "SettlementDatabaseInfo.txt"));
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
				//create the query to be made to the table
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select * from settlements");
				while(rs.next()){
					//save each line returned into the text file
					write.println(rs.getInt(1) + ":" + rs.getInt(9) + ":" + rs.getInt(10));
				}
				//close the connection to the database
				con.close();
			} catch (Exception e){
				//in case of an error print the error code for trouble shooting
				System.out.println(e.toString());
			}
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadDatabaseValues(String accountName){
		//load the player funds from the player database information file for this account
		try {
			//create a connection to the save file
			BufferedReader inputReader = new BufferedReader(new FileReader("C:/Users/Nat/git/Battle-Project/" + accountName + "PlayerDatabaseInfo.txt"));
			String nextLine;
			while((nextLine = inputReader.readLine()) != null){
				//convert the string into a char array
				char[] stringValues = nextLine.toCharArray();
				//get the index of the player the line refers to
				int playerIndex = Integer.parseInt("" + stringValues[0]);
				String fundsString = "";
				//get the funds of that player
				for(int count = 2; count< stringValues.length; count ++){
					fundsString += stringValues[count];
				}
				int playerFunds = Integer.parseInt(fundsString);
				//save the values retrieved in the database
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
					//create the query to be made to the table
					Statement stmt = con.createStatement();
					stmt.executeUpdate("update player set funds = " + playerFunds + " where id = " + playerIndex);
					//close the connection to the database
					con.close();
				} catch (Exception e){
					//in case of an error print the error code for trouble shooting
					System.out.println(e.toString());
				}
			}
			//close the connection
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//load the settlement income and player from the settlement database info file for this account
		try {
			//create a connection to the save file
			BufferedReader inputReader = new BufferedReader(new FileReader("C:/Users/Nat/git/Battle-Project/" + accountName + "SettlementDatabaseInfo.txt"));
			String nextLine;
			while((nextLine = inputReader.readLine()) != null){
				//convert the string into a char array
				char[] stringValues = nextLine.toCharArray();
				//get the index of the player the line refers to
				int settlementIndex = Integer.parseInt("" + stringValues[0]);
				int ownerIndex = Integer.parseInt("" + stringValues[2]);
				String incomeString = "";
				//get the income of that settlement
				for(int count = 4; count< stringValues.length; count ++){
					incomeString += stringValues[count];
				}
				int settlementIncome = Integer.parseInt(incomeString);
				//save the values retrieved in the database
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
					//create the query to be made to the table
					Statement stmt = con.createStatement();
					stmt.executeUpdate("update settlements set player = " + ownerIndex + " where id = " + settlementIndex);
					//create the second query to be made to the table
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate("update settlements set income = " + settlementIncome + " where id = " + settlementIndex);
					//close the connection to the database
					con.close();
				} catch (Exception e){
					//in case of an error print the error code for trouble shooting
					System.out.println(e.toString());
				}
			}
			//close the connection
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveGame(String accountName, ArrayList<Settlement> settlementList, ArrayList<Army> armyList){
		//save the settlement list
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(accountName + "Settlements.txt"));
			//write the obstruction array to the opened file
			outputStream.writeObject(settlementList);
			//close the connection
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//save the army list
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(accountName + "Armies.txt"));
			//write the obstruction array to the opened file
			outputStream.writeObject(armyList);
			//close the connection
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//save the database values
		saveCurrentDatabaseValues(accountName);
	}

	public void saveDefaultValues(String accountName){
		//create an army list with the default units and save it
		ArrayList<Army> allArmies = new ArrayList<Army>();

		//for testing purposes add in an initial army to manipulate
		ArrayList<Unit> testArmyUnits = new ArrayList<Unit>();
		testArmyUnits.add(new Unit(400, 340, 5, 100, 10, "land", 200, 10, "TestUnitOne.jpg"));
		testArmyUnits.add(new Unit(330, 270, 5, 200, 20, "air", 500, 10, "TestUnitTwo.jpg"));
		testArmyUnits.add(new Unit(650, 550, 5, 100, 10, "sea", 200, 10, "TestUnitThree.jpg"));
		Army testArmy1 = new Army(testArmyUnits, "TestArmy.png", 20, 20, 0);
		//add the test army to the array of armies
		testArmy1.updateMaxMovement(20);
		allArmies.add(testArmy1);

		//create and add a second army on a different team
		ArrayList<Unit> testArmyUnits2 = new ArrayList<Unit>();
		testArmyUnits2.add(new Unit(600, 320, 5, 100, 10, "land", 100, 10, "TestUnitFour.jpg"));
		testArmyUnits2.add(new Unit(615, 320, 5, 100, 10, "land", 100, 10, "TestUnitFour.jpg"));
		testArmyUnits2.add(new Unit(600, 335, 5, 100, 10, "land", 100, 10, "TestUnitFour.jpg"));
		testArmyUnits2.add(new Unit(615, 335, 5, 100, 10, "land", 100, 10, "TestUnitFour.jpg"));
		Army testArmy2 = new Army(testArmyUnits2, "TestArmy.png", 80, 70, 1);
		//add the test army to the array of armies
		testArmy2.updateMaxMovement(20);
		allArmies.add(testArmy2);

		ArrayList<Unit> testArmyUnits3 = new ArrayList<Unit>();
		testArmyUnits3.add(new Unit(400, 340, 5, 100, 10, "land", 200, 10, "TestUnitOne.jpg"));
		testArmyUnits3.add(new Unit(330, 270, 5, 200, 20, "air", 500, 10, "TestUnitTwo.jpg"));
		testArmyUnits3.add(new Unit(650, 550, 5, 100, 10, "sea", 200, 10, "TestUnitThree.jpg"));
		Army testArmy3 = new Army(testArmyUnits3, "TestArmy.png", 30, 30, 0);
		//add the test army to the array of armies
		testArmy3.updateMaxMovement(20);
		allArmies.add(testArmy3);

		//create a default settlement list
		ArrayList<Settlement> settlementList = new ArrayList<Settlement>();

		//use SQL to get the settlements from the table to add the correct number of settlements to the settlement list
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localHost:3306/battle?useSSL=true", "root", "root");
			//create the query to be made to the table
			Statement stmt = con.createStatement();
			//get the result set for the query executed
			ResultSet rs = stmt.executeQuery("select * from settlements");
			while(rs.next()){
				//loop through all rows in the table that were returned
				//add new settlements for each of those in the settlement table with the correct ID
				settlementList.add(new Settlement(10, rs.getInt(1)));
			}
			//close the connection to the database
			con.close();
		} catch (Exception e){
			//in case of an error print the error code for trouble shooting
			System.out.println(e.toString());
		}

		//save the default lists to the account name
		saveGame(accountName, settlementList, allArmies);
		//save the default database values
		saveDefaultDatabaseValues(accountName);
	}
}
