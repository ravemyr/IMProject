
public class Cryptograph {
	public static String encode(String inString, String type, String Key){
		StringBuilder encodedString = new StringBuilder();
		encodedString.append("<encrypted type=" + type + " key="+Key);
		if(type=="Caesar"){
			char[] alphabet ={'a','b','c','d','e','f','g','h','i','j','k','l','m',
					'n','o','p','q','r','s','t','u','v','w','x','y','z','å','ä','ö'};
			for(int i = 0; i < inString.length(); i++){
				char a = inString.charAt(i);
				if(alphabet.contains(a)||
						(Character.isUpperCase('a')&&
								alphabet.contains(Character.toLowerCase(a))){ //If alphabetic
					char newChar = 
				}
			}
		}
		return encodedString.toString();
		
	}
	public static String decode(String inString, String type, String Key){
		return Key;
		
	}
}
