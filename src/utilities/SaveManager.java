package utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


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
}
