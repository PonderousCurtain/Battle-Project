package mapmaker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import utilities.Obstruction;


public class MMSaveManager {
	public Obstruction[][] loadObstructionMap(File file){
		//create an array to store the map in
		Obstruction[][] loadedObstructionMap = null;
		try {
			//connect to the file specified in the constructor
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			try {
				//read the array from the file
				loadedObstructionMap = (Obstruction[][]) inputStream.readObject();
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
		//return the loaded map
		return loadedObstructionMap;
	}
	
	public void saveObstructionMap(File file, Obstruction[][] toSave) throws ClassNotFoundException{
		//connect to the file specified by the constructor
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			//save the array from the constructor to the file
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
