package utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import settlement.Settlement;
import overworld.Army;


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
/*
	public void saveBlockages(String mapName, Obstruction[][] toSave) throws ClassNotFoundException{
		//connect to the names file
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("U:/Years/Year 10/Deadly Dave/Battle Prototype 2 With 2D Array/" + mapName));
			//write the obstruction array to the opened file
			outputStream.writeObject(toSave);
			//close the connection
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
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
	}
}
