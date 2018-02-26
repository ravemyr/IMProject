import javax.crypto.spec.*;
import javax.crypto.*;
public class Cryptograph {
	/**
	 * Encodes given string with given crypto and key. If the key should be auto-generated
	 * The program will override the input and autogenerate a key.
	 * @param inString
	 * @param type
	 * @param Key
	 * @return
	 * @throws Exception
	 */
	public static String encode(String inString, String type, String Key) throws Exception{
		StringBuilder encodedString = new StringBuilder();
		String thisString;
		encodedString.append("<encrypted type=" + type + " key="+
				Integer.toHexString(Integer.parseInt(Key)) + "> ");
		if(type=="Caesar"){
			System.out.print(inString);
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
							thisString = String.format("%04x",(int)alphabet[(indexOf(alphabet,b)
									+Integer.parseInt(Key))%alphabet.length]);
							
//							encodedString.append(alphabet[(indexOf(alphabet,b)
//									+Integer.parseInt(Key))%alphabet.length]);
							encodedString.append(thisString);
							used = 1;
							break;
						}catch(Exception c){
							System.out.print(c.getMessage());
						}
					}
				}
				if(used==0){
					thisString = String.format("%04x", (int)a);
					encodedString.append(thisString);
				
				}
			}
		}
		else if(type=="AES"){
			KeyGenerator AESgen = KeyGenerator.getInstance("AES");
			AESgen.init(128);
			SecretKeySpec AESkey = (SecretKeySpec)AESgen.generateKey();
			byte[] keyContent = AESkey.getEncoded();
			System.out.println(keyContent);
			encodedString.replace(0,encodedString.length(),"<encrypted type=" + type 
					+ " key="+keyContent.toString() + "> ");
			
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
			byte[] cipherData = AEScipher.doFinal(inString.getBytes());
			encodedString.append(cipherData.toString());
		}
		else{
			throw new Exception("Unknown encryption");
		}
		encodedString.append(" </encrypted> ");
		return encodedString.toString();
		
	}
	public static String decode(String inString) throws Exception{
		StringBuilder decodedString = new StringBuilder();
		String[] splitString = inString.split("\\s");
		String type = splitString[3].substring(5, splitString[3].length());
		String Key = splitString[4].substring(4, splitString[4].length()-1);
		StringBuilder temp = new StringBuilder();
		String thisString;
		for(int j=0;j<5;j++){
			decodedString.append(splitString[j]);
			decodedString.append(" ");
		}
		String encodedString = unHex(splitString[5]);
		String[] brokenDown = encodedString.split("\\s");
		for(int k = 1;k<brokenDown.length;k++){
			temp.append(splitString[k]+ " ");
		}
		inString = temp.toString();
		if(type.equals("Caesar")){
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö','A','B',
					'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
					'X','Y','Z','Å','Ä','Ö','0','1','2','3',
					'4','5','6','7','8','9','!','?',')','(','=','>','<','/','&','%','#','@','$','[',']'};
			int Keyint = Integer.decode(Key)%alphabet.length;
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
			decodedString.append(splitString[splitString.length-2]);
			decodedString.append(" ");
			
			decodedString.append(splitString[splitString.length-1]);
		}
		else if(type.equals("AES")){
			System.out.println(Key);
			System.out.println("Here");
			byte[] keyContent = Key.getBytes("UTF8");
			System.out.println(keyContent);
			SecretKeySpec decodeKey = new SecretKeySpec(keyContent,"AES");
			Cipher AEScipher = Cipher.getInstance("AES");
			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
			byte[] decryptedData;
			decryptedData = AEScipher.doFinal(inString.getBytes());
		    decodedString.append(decryptedData);
		}
		else{
			throw new Exception("Not a valid encryption");
		}
		return decodedString.toString();
		
	}
	public static int indexOf(char[] array, char control) throws Exception{
		for(int j=0;j<array.length;j++){
			if(array[j]==control){
				return j;
			}
		}
		throw new Exception("Non-valid arguments for encryption");
	}
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
	public static void main(String[] args){
		try {
			String encrypted = Cryptograph.encode("MEMES ARE GREAT", "Caesar", "1123");
			System.out.println(encrypted);
			System.out.println(Cryptograph.decode(encrypted));
//			byte[] dataToEncrypt = "Hej".getBytes();
//			byte[] keyContent;
//			// Skapa nyckel 
//			KeyGenerator AESgen = KeyGenerator.getInstance("AES");
//			AESgen.init(128);
//			SecretKeySpec AESkey = (SecretKeySpec)AESgen.generateKey(); 
//			keyContent = AESkey.getEncoded();
//			// Kryptera
//			Cipher AEScipher = Cipher.getInstance("AES");
//			AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
//			byte[] cipherData = AEScipher.doFinal(dataToEncrypt);
//			// Avkryptera 
//			SecretKeySpec decodeKey = new SecretKeySpec(keyContent, "AES");
//			AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
//			byte[] decryptedData = AEScipher.doFinal(cipherData);
//			System.out.println("Decrypted: " + new String(decryptedData));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
