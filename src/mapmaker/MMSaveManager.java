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
	public MMSaveManager(){
		
	}
	
	public Obstruction[][] loadObstructionMap(File file){
		Obstruction[][] loadedObstructionMap = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			try {
				loadedObstructionMap = (Obstruction[][]) inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loadedObstructionMap;
	}
	
	public void saveObstructionMap(File file, Obstruction[][] toSave) throws ClassNotFoundException{
		
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(toSave);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
