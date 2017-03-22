package utilities;

import java.util.Random;

public class EncryptionManager {
	public String encrypt(String toEncryptString){
		//convert the string to encrypt to a byte array
		byte[] toEncryptArray = toEncryptString.getBytes();
		String encrypted = "";
		//create a seed based on the string to encrypt and use it to generate a key in the form of a byte array
		int seed = toEncryptString.length();
		Random random = new Random();
		random.setSeed(seed);
		byte[] key = new byte[toEncryptString.length()];
		random.nextBytes(key);
		//loop through each byte in the to encrypt array and XOR it with the key value to get the encrypted character
		for(int count = 0; count < toEncryptString.length(); count ++){
			encrypted += key[count] ^ toEncryptArray[count];
		}
		//return the encrypted string
		return encrypted;
	}
}