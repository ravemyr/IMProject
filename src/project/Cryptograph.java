package project;
import javax.crypto.spec.*;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.*;
public class Cryptograph {
	/**
	 * Encodes given string with given crypto and key. It also ensures that the message is well formed.
	 * Supports AES and Caesar as of 27-02-2018 
	 * @param inString
	 * @param type
	 * @param Key
	 * @return
	 * @throws Exception
	 */
	public static String encode(String inString, String type, String Key) throws Exception{
		StringBuilder encodedString = new StringBuilder();
		String thisString;
		encodedString.append("<encrypted type=" + type + " key=");
		if(type=="Caesar"){
			encodedString.append(Integer.toHexString(Integer.parseInt(Key)) + "> ");
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö','A','B',
					'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U',
					'V','W','X','Y','Z','Å','Ä','Ö','0','1','2','3','4','5','6','7','8','9',
					'!','?',')','(','=','>','<','/','&','%','#','@','$','[',']'};
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						try{
							thisString = String.format("%02x",(int)alphabet[(indexOf(alphabet,b)
									+Integer.parseInt(Key))%alphabet.length]);
							encodedString.append(thisString);
							used = 1;
							break;
						}catch(Exception c){
							System.out.print(c.getMessage());
						}
					}
				}
				if(used==0){
					thisString = String.format("%02x", (int)a);
					encodedString.append(thisString);
				}
			}
		}
		else if(type=="AES"){
			for(int i = 0; i<Key.length();i++){
				encodedString.append(String.format("%02x", (int)Key.charAt(i)));
			}
			encodedString.append(">");
			encodedString.append(" ");
			byte[] keyContent = Base64.getDecoder().decode(Key);
			SecretKeySpec AESkey = new SecretKeySpec(keyContent,0,keyContent.length, "AES");
			System.out.println(keyContent);
			
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
			byte[] cipherData = AEScipher.doFinal(inString.getBytes());
			for(byte b: cipherData){
				encodedString.append(String.format("%02x", b));
			}
		}
		else{
			throw new Exception("Unknown encryption");
		}
		encodedString.append(" </encrypted> ");
		return encodedString.toString();
		
	}
	/**
	 * Decodes received string with given encryption and key in hexadecimal. 
	 * Supports AES and Caesar encryption as of 27-02-2018.
	 * @param inString
	 * @return
	 * @throws Exception
	 */
	public static String decode(String inString) throws Exception{
		StringBuilder decodedString = new StringBuilder();
		String[] splitString = inString.split("\\s");
		String type = splitString[3].substring(5, splitString[3].length());
		String Key = splitString[4].substring(4, splitString[4].length()-1);
		StringBuilder temp = new StringBuilder();
		for(int j=0;j<5;j++){
			decodedString.append(splitString[j]);
			decodedString.append(" ");
		}
		if(type.equals("Caesar")){
			String encodedString = unHex(splitString[5]);
			inString = encodedString;
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö','A','B',
					'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
					'X','Y','Z','Å','Ä','Ö','0','1','2','3',
					'4','5','6','7','8','9','!','?',')','(','=','>','<','/','&','%','#','@','$','[',']'};
			int Keyint = (int) (Long.parseLong(Key,16)%alphabet.length);
			System.out.println("Here is "+ Keyint);
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						if(indexOf(alphabet,b)<Keyint){
							try{
								decodedString.append(
										alphabet[(alphabet.length-
												Keyint+indexOf(alphabet,b))]);
								used = 1;
								break;
							}catch(Exception c){
								System.out.print(c.getMessage());
							}
						}
						else{
							try{
								decodedString.append(alphabet[(indexOf(alphabet,b)
										-Keyint)]);
								used = 1;
								break;
							}catch(Exception c){
								System.out.print(c.getMessage());
							}
						}
					}
				}
				if(used==0){
					decodedString.append(a);
				}
			}	
		}
		else if(type.equals("AES")){
			String neuKey = unHex(Key);
			byte[] keyContent = Base64.getDecoder().decode(neuKey);
			String encodedString = unHex(splitString[5]);
			byte[] encoded = hexStringToByteArray(splitString[5]);
			inString = encodedString;
			SecretKeySpec decodeKey = new SecretKeySpec(keyContent, "AES");
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
			byte[] decryptedData;
			decryptedData = AEScipher.doFinal(encoded);
		    decodedString.append(new String(decryptedData,"UTF8"));
		}
		else{
			throw new Exception("Not a valid encryption");
		}
		decodedString.append(splitString[splitString.length-2]);
		decodedString.append(" ");
		decodedString.append(splitString[splitString.length-1]);
		return decodedString.toString();
		
	}
	public static byte[] encryptFile(byte[] bytesIn,String type, String Key) throws Exception{
		byte[] encoded = null;	
		if(type.equals("Caesar")){
			String inString = new String(bytesIn);
			StringBuilder runningCrypto = new StringBuilder();
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö','A','B',
					'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
					'X','Y','Z','Å','Ä','Ö','0','1','2','3',
					'4','5','6','7','8','9','!','?',')','(','=','>','<','/','&','%','#','@','$','[',']'};
			int Keyint = Integer.parseInt(Key)%alphabet.length;
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						try {
							if(indexOf(alphabet,b)<Keyint){
								try{
									runningCrypto.append(
											alphabet[(alphabet.length-
													Keyint+indexOf(alphabet,b))]);
									used = 1;
									break;
								}catch(Exception c){
									System.out.print(c.getMessage());
								}
							}
							else{
								try{
									runningCrypto.append(alphabet[(indexOf(alphabet,b)
											-Keyint)]);
									used = 1;
									break;
								}catch(Exception c){
									System.out.print(c.getMessage());
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(used==0){
					runningCrypto.append(a);
				}
			}
			encoded = runningCrypto.toString().getBytes();
		}
		else if(type.equals("AES")){
			byte[] keyContent = Base64.getDecoder().decode(Key);
			SecretKeySpec AESkey = new SecretKeySpec(keyContent,0,keyContent.length, "AES");			
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
			encoded = AEScipher.doFinal(bytesIn);
		}
		else{
			throw new Exception("Unknown encryption");
		}
		return encoded;
	}
	public static byte[] decryptFile(byte[] bytesIn,String type, String Key) throws Exception{
		byte[] decoded = null;
		if(type.equals("Caesar")){
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö','A','B',
					'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
					'X','Y','Z','Å','Ä','Ö','0','1','2','3',
					'4','5','6','7','8','9','!','?',')','(','=','>','<','/','&','%','#','@','$','[',']'};
			int Keyint = Integer.parseInt(Key)%alphabet.length;
			String inString = new String(bytesIn, "UTF8");
			StringBuilder decodedString = new StringBuilder();
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						if(indexOf(alphabet,b)<Keyint){
							try{
								decodedString.append(
										alphabet[(alphabet.length-
												Keyint+indexOf(alphabet,b))]);
								used = 1;
								break;
							}catch(Exception c){
								System.out.print(c.getMessage());
							}
						}
						else{
							try{
								decodedString.append(alphabet[(indexOf(alphabet,b)
										-Keyint)]);
								used = 1;
								break;
							}catch(Exception c){
								System.out.print(c.getMessage());
							}
						}
					}
				}
			}
			decoded = decodedString.toString().getBytes();
		}
		else if(type.equals("AES")){
			byte[] keyContent = Base64.getDecoder().decode(Key);
			SecretKeySpec decodeKey = new SecretKeySpec(keyContent, "AES");
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
			decoded = AEScipher.doFinal(bytesIn);
		}
		return decoded;
	}
	/**
	 * Returns index of given character in an array. Assumes existence of character in array.
	 * @param array
	 * @param control
	 * @return
	 * @throws Exception
	 */
	public static int indexOf(char[] array, char control) throws Exception{
		for(int j=0;j<array.length;j++){
			if(array[j]==control){
				return j;
			}
		}
		throw new Exception("Non-valid arguments for encryption");
	}
	/**
	 * Converts a string of Hexadecimal into string of characters.
	 * @param arg
	 * @return
	 */
	public static String unHex(String arg) {        
	    String str = "";
	    for(int i=0;i<arg.length();i+=2)
	    {
	        String s = arg.substring(i, (i + 2));
	        int decimal = Integer.parseInt(s, 16);
	        str = str + (char) decimal;
	    }       
	    return str;
	}
	/**
	 * Converts a string of hexadecimal into a byte-array.
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
