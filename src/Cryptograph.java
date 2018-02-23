import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.*;
public class Cryptograph {
	public static String encode(String inString, String type, String Key) throws Exception{
		StringBuilder encodedString = new StringBuilder();
		final byte[] key;
		encodedString.append("<encrypted type=" + type + " key="+Key + "> ");
		if(type=="Caesar"){
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö'};
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						try{
							encodedString.append(alphabet[(indexOf(alphabet,b)
									+Integer.parseInt(Key))%alphabet.length]);
							used = 1;
							break;
						}catch(Exception c){
							System.out.print(c.getMessage());
						}
					}
					else if(Character.toLowerCase(a)==b){
						try{
							encodedString.append(Character.toUpperCase(
									alphabet[(indexOf(alphabet,b)+Integer.parseInt(Key))%alphabet.length]));
							used = 1;
							break;
						}
						catch(Exception c){
							System.out.print(c.getMessage());
							
						}
					}
				}
				if(used==0){
					encodedString.append(a);
				}
			}
		}
		else if(type=="AES"){
			key = Key.getBytes(Charset.forName("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key, type);
			Cipher cip = Cipher.getInstance(type);
			cip.init(Cipher.ENCRYPT_MODE, secret);
			encodedString.append(cip.doFinal
					(inString.getBytes(Charset.forName("UTF-8"))).toString());
		}
		else{
			throw new Exception("Unknown encryption");
		}
		encodedString.append(" </encrypted>");
		return encodedString.toString();
		
	}
	public static String decode(String inString) throws Exception{
		StringBuilder encodedString = new StringBuilder();
		final byte[] key;
		String[] splitString = inString.split("\\s");
		String type = splitString[1].substring(5, splitString[1].length());
		String Key = splitString[2].substring(4, splitString[2].length()-1);
		System.out.println(splitString[3].length());
		if(type.equals("Caesar")){
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö'};
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				int used = 0;
				for(char b: alphabet){
					if(b==a){
						try{
							encodedString.append(alphabet[(indexOf(alphabet,b)
									-Integer.parseInt(Key))%alphabet.length]);
							used = 1;
							break;
						}catch(Exception c){
							System.out.print(c.getMessage());
						}
					}
					else if(Character.toLowerCase(a)==b){
						try{
							encodedString.append(Character.toUpperCase(
									alphabet[(indexOf(alphabet,b)-Integer.parseInt(Key))%alphabet.length]));
							used = 1;
							break;
						}
						catch(Exception c){
							System.out.print(c.getMessage());
							
						}
					}
				}
				if(used==0){
					encodedString.append(a);
				}
			}
		}
		else if(type.equals("AES")){
			key = Key.getBytes(Charset.forName("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key, type);
		    Cipher cip = Cipher.getInstance(type);
		    cip.init(Cipher.DECRYPT_MODE, secret);
		    encodedString.append(cip.doFinal
					(splitString[3].getBytes(Charset.forName("UTF-8"))).toString());
		}
		else{
			throw new Exception("Not a valid encryption");
		}
		return encodedString.toString();
		
	}
	public static int indexOf(char[] array, char control) throws Exception{
		for(int j=0;j<array.length;j++){
			if(array[j]==control){
				return j;
			}
		}
		throw new Exception("Non-valid arguments for encryption");
	}
	public static void main(String[] args){
		try {
			String encrypted = Cryptograph.encode("omjag skriverqwe", "AES", "MZygpewJsCpRrfOr");
			System.out.println(encrypted);
			System.out.println(encrypted.substring(5, 10));
			System.out.print(Cryptograph.decode(encrypted));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
